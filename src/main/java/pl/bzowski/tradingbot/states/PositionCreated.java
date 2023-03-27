package pl.bzowski.tradingbot.states;

public class PositionCreated implements PositionState {

    private final long positionId;

    public PositionCreated(long positionId) {
        this.positionId = positionId;
    }

    @Override
    public boolean isOpened() {
        return true;
    }

    @Override
    public boolean canBeClosed(long positionId) {
        return this.positionId == positionId;
    }

    @Override
    public long getPositionId() {
        return positionId;
    }

}
