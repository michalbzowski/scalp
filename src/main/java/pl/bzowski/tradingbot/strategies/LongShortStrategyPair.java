package pl.bzowski.tradingbot.strategies;

public class LongShortStrategyPair {
    private final StrategyWithLifeCycle longStrategy;
    private final StrategyWithLifeCycle shortStrategy;

    public LongShortStrategyPair(StrategyWithLifeCycle longStrategy, StrategyWithLifeCycle shortStrategy) {

        this.longStrategy = longStrategy;
        this.shortStrategy = shortStrategy;
    }

    public StrategyWithLifeCycle getLongStrategy() {
        return longStrategy;
    }

    public StrategyWithLifeCycle getShortStrategy() {
        return shortStrategy;
    }
}
