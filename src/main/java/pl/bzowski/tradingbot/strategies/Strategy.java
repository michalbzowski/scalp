package pl.bzowski.tradingbot.strategies;

import org.ta4j.core.BarSeries;
import pro.xstore.api.message.error.APICommandConstructionException;

public interface Strategy {
    StrategyWithLifeCycle getLongStrategy() throws APICommandConstructionException;

    StrategyWithLifeCycle getShortStrategy() throws APICommandConstructionException;

    long candlesOfMillisArchive();

    double stoplossValue(int index);
}
