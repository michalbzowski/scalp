package pro.xstore.api.message.records;

import org.json.simple.JSONObject;

public class RateInfoRecord implements BaseResponseRecord {

    private long ctm;
    private double open;
    private double high;
    private double low;
    private double close;
    private double vol;

    public RateInfoRecord() {

    }

    public RateInfoRecord(long ctm, double open, double high, double low, double close, double vol) {
        this.ctm = ctm;
        this.open = open;
        this.high = high;
        this.low = low;
        this.close = close;
        this.vol = vol;
    }

    public long getCtm() {
        return ctm;
    }

    public void setCtm(long ctm) {
        this.ctm = ctm;
    }

    public double getOpen() {
        return open;
    }

    public void setOpen(double open) {
        this.open = open;
    }

    public double getHigh() {
        return high;
    }

    public void setHigh(double high) {
        this.high = high;
    }

    public double getLow() {
        return low;
    }

    public void setLow(double low) {
        this.low = low;
    }

    public double getClose() {
        return close;
    }

    public void setClose(double close) {
        this.close = close;
    }

    public double getVol() {
        return vol;
    }

    public void setVol(double vol) {
        this.vol = vol;
    }

    @Override
    public void setFieldsFromJSONObject(JSONObject e) {
        this.setClose((Double) e.get("close"));
        this.setCtm((Long) e.get("ctm"));
        this.setHigh((Double) e.get("high"));
        this.setLow((Double) e.get("low"));
        this.setOpen((Double) e.get("open"));
        this.setVol((Double) e.get("vol"));
    }

    @Override
    public String toString() {
        return "RateInfoRecord{" + "ctm=" + ctm + ", open=" + open + ", high=" + high + ", low=" + low + ", close=" + close + ", vol=" + vol + '}';
    }

    public RateInfoRecord shiftLeft(int digits) {
        return new RateInfoRecord(ctm, open / Math.pow(10, digits), high / Math.pow(10, digits), low / Math.pow(10, digits), close / Math.pow(10, digits), vol);
    }
}
