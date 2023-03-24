package pl.bzowski.tradingbot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ta4j.core.BarSeries;
import pl.bzowski.tradingbot.strategies.StrategyWithLifeCycle;
import pro.xstore.api.message.codes.PERIOD_CODE;
import pro.xstore.api.message.records.SCandleRecord;


public class TradingBotImpl extends TradingBot {

    private static final Logger logger = LoggerFactory.getLogger(TradingBotImpl.class);

    public TradingBotImpl(String symbol, BarSeries series, PERIOD_CODE periodCode, StrategyWithLifeCycle longStrategy, StrategyWithLifeCycle shortStrategy) {
        super(symbol, series, periodCode, longStrategy, shortStrategy);
    }

    @Override
    public void onTick(SCandleRecord candleRecord) {
        logger.info(symbol + ": Received candle from stream: " + candleRecord);
        var endIndex = updateSeriesWithOneMinuteCandle(candleRecord);
        logMemoryUsage();
        var openLong = longStrategy.shouldEnter(endIndex);
        logger.info("Open long:" + openLong);
        var openShort = shortStrategy.shouldEnter(endIndex);
        logger.info("Open short: " + openShort);
    }

}
