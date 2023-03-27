package pl.bzowski.tradingbot.strategies;

public class PostitionOpeningStatus {

    private long positionId;
    private boolean opened;

    public PostitionOpeningStatus(long positionId, boolean opened) {
        this.positionId = positionId;
        this.opened = opened;
    }

    public long positionId() {
        return positionId;
    }

    public boolean isOpened() {
        return opened;
    }
}
