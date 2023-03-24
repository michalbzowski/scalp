package pl.bzowski.trader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.bzowski.series.SeriesHandler;
import pl.bzowski.tradingbot.TradingBot;
import pro.xstore.api.message.records.SCandleRecord;
import pro.xstore.api.streaming.StreamingListener;

import java.util.Map;

public class TradeBotStreamListener extends StreamingListener {

    private final Map<String, TradingBot> strategies;
    Logger logger = LoggerFactory.getLogger(TradeBotStreamListener.class);

    public TradeBotStreamListener(Map<String, TradingBot> strategies) {
        this.strategies = strategies;
    }

    @Override
    public void receiveCandleRecord(SCandleRecord candleRecord) {
        logger.info("Stream candle record: " + candleRecord);
        TradingBot tradingBot = strategies.get(candleRecord.getSymbol());

        tradingBot.onTick(candleRecord);
    }


}
