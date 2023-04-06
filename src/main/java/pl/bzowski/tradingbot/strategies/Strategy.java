package pl.bzowski.tradingbot.strategies;

import pro.xstore.api.message.error.APICommandConstructionException;

public interface Strategy {
    StrategyWithLifeCycle getLongStrategy() throws APICommandConstructionException;

    StrategyWithLifeCycle getShortStrategy() throws APICommandConstructionException;

    long candlesOfMillisArchive();

    double stoplossValue(int index, boolean aLong);
    double takeProfitValue(int index, boolean aLong);
}
