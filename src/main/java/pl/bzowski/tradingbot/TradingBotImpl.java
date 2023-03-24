package pl.bzowski.tradingbot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ta4j.core.Bar;
import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseBar;
import org.ta4j.core.num.DecimalNum;
import pl.bzowski.tradingbot.strategies.StrategyWithLifeCycle;
import pro.xstore.api.message.codes.PERIOD_CODE;
import pro.xstore.api.message.records.SCandleRecord;

import java.time.*;

public class TradingBotImpl extends TradingBot {

    private static final Logger logger = LoggerFactory.getLogger(TradingBotImpl.class);
    private final String symbol;
    private final BarSeries series;
    private PERIOD_CODE periodCode;
    private final StrategyWithLifeCycle longStrategy;
    private final StrategyWithLifeCycle shortStrategy;

    private static final long MINUTE_IN_MILLISECONDS = 60_000L;

    public TradingBotImpl(String symbol, BarSeries series, PERIOD_CODE periodCode, StrategyWithLifeCycle longStrategy, StrategyWithLifeCycle shortStrategy) {
        this.symbol = symbol;
        this.series = series;
        this.periodCode = periodCode;
        this.longStrategy = longStrategy;
        this.shortStrategy = shortStrategy;
    }

    @Override
    public void receiveCandleRecord(SCandleRecord candleRecord) {
        logger.info(symbol + ": Received candle from stream: " + candleRecord);
        var endIndex = updateSeriesWithOneMinuteCandle(candleRecord);
        logMemoryUsage();
        var openLong = longStrategy.shouldEnter(endIndex);
        logger.info("Open long:" + openLong);
        var openShort = shortStrategy.shouldEnter(endIndex);
        logger.info("Open short: " + openShort);

    }

    public int updateSeriesWithOneMinuteCandle(SCandleRecord record) {
        //Ostatni bar z serii może być jeszcze w trakcie rysowania
        Bar lastBar = series.getLastBar();

        //jeśli tak jest to jego czas początkowy plus wybrany okres powinien być większy niż czas rekordu, który wpadł w tej chwili

        long ctm = record.getCtm();//Candle start time in CET time zone (Central European Time)
        long periodDurationInMilliseconds = periodCode.getCode() * MINUTE_IN_MILLISECONDS;
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(ctm),
                ZoneId.systemDefault());
        ZonedDateTime currentOneMinuteBarBeginTime = localDateTime.atZone(ZoneId.systemDefault());
        BaseBar newBar = getBaseBar(PERIOD_CODE.PERIOD_M1.getCode(), ctm, record.getClose(), record.getOpen(), record.getHigh(), record.getLow());//Zawsze jednominutwa, bo taki tik dostajemy z appi

        ZonedDateTime lastBarBeginTime = lastBar.getBeginTime();
        ZonedDateTime plus = lastBarBeginTime.plus(Duration.ofMillis(periodDurationInMilliseconds));
        boolean isEquals = plus.isEqual(currentOneMinuteBarBeginTime);
        if (!isEquals) {
            series.addPrice(newBar.getClosePrice());
            logger.info("Series updated by another one minute candle");
            return -1; //It means that there is no new bar
        } else {
            logger.info("New bar added");
            series.addBar(newBar);
        }

        int endIndex = series.getEndIndex();
        return endIndex;
    }

    private BaseBar getBaseBar(long code, long ctm, double close, double open, double high, double low) {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(ctm + code * 60_000),
                ZoneId.systemDefault());
        ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.systemDefault());
        return BaseBar.builder()
                .closePrice(DecimalNum.valueOf(close))
                .openPrice(DecimalNum.valueOf(open))
                .highPrice(DecimalNum.valueOf(high))
                .lowPrice(DecimalNum.valueOf(low))
                .endTime(zonedDateTime)
                .timePeriod(Duration.ofMinutes(code))
                .build();
    }

    private void logMemoryUsage() {
        Runtime rt = Runtime.getRuntime();
        long usedMB = (rt.totalMemory() - rt.freeMemory()) / 1024 / 1024;
        logger.info("Memory usage {} MB", usedMB);
    }

}
