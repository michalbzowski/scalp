package pl.bzowski.platform;

public class ScalpTradeRecord {

    private long order2;
    private Double openPrice;

    private double volume;
    private double closePrice;
    private String symbol;
    private long order;

    public ScalpTradeRecord(long order2, Double openPrice, double volume, Double closePrice, String symbol, long order) {
        this.order2 = order2;
        this.openPrice = openPrice;
        this.volume = volume;
        this.closePrice = closePrice;
        this.symbol = symbol;
        this.order = order;
    }

    public long getOrder2() {
        return order2;
    }

    public Double getOpen_price() {
        return openPrice;
    }

    public double getVolume() {
        return volume;
    }

    public double getClose_price() {
        return closePrice;
    }

    public String getSymbol() {
        return symbol;
    }

    public long getOrder() {
        return order;
    }
}
