package pro.xstore.api.message.records;

import org.json.simple.JSONObject;

public class TradeRecord extends WrapperTradeRecord {

    private long timestamp;

    private Long open_time;

    @Override
    public void setFieldsFromJSONObject(JSONObject ob) {
        super.setFieldsFromJSONObject(ob);
        this.timestamp = (Long) ob.get("timestamp");

        this.open_time = (Long) ob.get("open_time");

        super.close_time = (Long) ob.get("close_time");
        super.expiration = (Long) ob.get("expiration");
        super.position = (Long) ob.get("position");
        var profit1 = ob.get("profit");
        if (profit1 == null) {
            profit1 = 0.0d;
        }
        super.profit = Double.parseDouble(profit1.toString());
        super.sl = Double.parseDouble(ob.get("sl").toString());
        super.tp = Double.parseDouble(ob.get("tp").toString());
    }

    public long getTimestamp() {
        return timestamp;
    }

    public Long getOpen_time() {
        return open_time;
    }

    @Override
    public String toString() {
        return "TradeRecord [timestamp=" + timestamp + ", open_time="
                + open_time + ", toString()=" + super.toString() + "]";
    }
}