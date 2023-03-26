package pl.bzowski.tradingbot.strategies;

import org.ta4j.core.BarSeries;
import pro.xstore.api.message.error.APICommandConstructionException;

public interface StrategyBuilder {
    StrategyWithLifeCycle getLongStrategy(BarSeries series) throws APICommandConstructionException;

    StrategyWithLifeCycle getShortStrategy(BarSeries series) throws APICommandConstructionException;

    long candlesOfMillisArchive();
}
