package pl.bzowski.tradingbot.states;

public interface PositionState {

    boolean isOpened();

    boolean canBeClosed(long positionId);

    long getPositionId();

}
