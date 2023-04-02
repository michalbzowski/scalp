package pl.bzowski.tradingbot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ta4j.core.BarSeries;
import pl.bzowski.tradingbot.positions.OpenPosition;
import pl.bzowski.tradingbot.strategies.StrategyWithLifeCycle;
import pro.xstore.api.message.codes.PERIOD_CODE;
import pro.xstore.api.message.records.SCandleRecord;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;


public class TradingBotImpl extends TradingBot {

    private static final Logger logger = LoggerFactory.getLogger(TradingBotImpl.class);

    TradingBotImpl(String symbol, BarSeries series, PERIOD_CODE periodCode, StrategyWithLifeCycle longStrategy, StrategyWithLifeCycle shortStrategy) {
        super(symbol, series, periodCode, longStrategy, shortStrategy);
    }

    @Override
    public void onTick(SCandleRecord candleRecord) {
        logger.info(symbol + ": Received candle " + LocalDateTime.ofInstant(Instant.ofEpochMilli(candleRecord.getCtm()), ZoneId.systemDefault()) + " from stream: " + candleRecord);
        var endIndex = updateSeriesWithOneMinuteCandle(candleRecord);
        logMemoryUsage();
        longStrategy.manage(endIndex);
        shortStrategy.manage(endIndex);
    }

}
