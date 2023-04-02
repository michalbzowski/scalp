package pl.bzowski.tradingbot.positions;

public class TradeTransactionAbstractResponse {
    private final boolean status;
    private final long order;

    public TradeTransactionAbstractResponse(Boolean status, long order) {
        this.status = status;
        this.order = order;
    }

    public boolean getStatus() {
        return status;
    }

    public long getOrder() {
        return order;
    }
}
