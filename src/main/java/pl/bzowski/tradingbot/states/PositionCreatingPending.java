package pl.bzowski.tradingbot.states;

public class PositionCreatingPending implements PositionState {

    @Override
    public boolean isOpened() {
        return false;
    }

    @Override
    public boolean canBeClosed(long positionId) {
        return false;
    }

    @Override
    public long getPositionId() {
        return 0;
    }

}
