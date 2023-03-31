package pl.bzowski.tradingbot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ta4j.core.Bar;
import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseBar;
import org.ta4j.core.num.DoubleNum;
import pl.bzowski.tradingbot.strategies.StrategyWithLifeCycle;
import pro.xstore.api.message.codes.PERIOD_CODE;
import pro.xstore.api.message.records.SCandleRecord;

import java.time.*;
import java.util.HashMap;
import java.util.Map;

public abstract class TradingBot {
    private static final long MINUTE_IN_MILLISECONDS = 60_000L;
    private static final Logger logger = LoggerFactory.getLogger(TradingBot.class);

    private final BarSeries series;
    private final PERIOD_CODE periodCode;

    protected final String symbol;
    protected final StrategyWithLifeCycle longStrategy;
    protected final StrategyWithLifeCycle shortStrategy;

    public TradingBot(String symbol, BarSeries series, PERIOD_CODE periodCode, StrategyWithLifeCycle longStrategy, StrategyWithLifeCycle shortStrategy) {
        this.symbol = symbol;
        this.series = series;
        this.periodCode = periodCode;
        this.longStrategy = longStrategy;
        this.shortStrategy = shortStrategy;
    }

    protected int updateSeriesWithOneMinuteCandle(SCandleRecord record) {
        //Ostatni bar z serii może być jeszcze w trakcie rysowania
        long ctm = record.getCtm();//Candle start time in CET time zone (Central European Time)
        BaseBar newBar = getBaseBar(PERIOD_CODE.PERIOD_M1.getCode(), ctm, record.getClose(), record.getOpen(), record.getHigh(), record.getLow());//Zawsze jednominutwa, bo taki tik dostajemy z appi
        if (series.getEndIndex() < 0) {
            series.addBar(newBar);
            return series.getEndIndex();
        }
        Bar lastBar = series.getLastBar();

        //jeśli tak jest to jego czas początkowy plus wybrany okres powinien być większy niż czas rekordu, który wpadł w tej chwili


        long periodDurationInMilliseconds = periodCode.getCode() * MINUTE_IN_MILLISECONDS;
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(ctm),
                ZoneId.systemDefault());
        ZonedDateTime currentOneMinuteBarBeginTime = localDateTime.atZone(ZoneId.systemDefault());


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
                .closePrice(DoubleNum.valueOf(close))
                .openPrice(DoubleNum.valueOf(open))
                .highPrice(DoubleNum.valueOf(high))
                .lowPrice(DoubleNum.valueOf(low))
                .endTime(zonedDateTime)
                .timePeriod(Duration.ofMinutes(code))
                .build();
    }

    protected void logMemoryUsage() {
        Runtime rt = Runtime.getRuntime();
        long usedMB = (rt.totalMemory() - rt.freeMemory()) / 1024 / 1024;
        logger.info("Memory usage {} MB", usedMB);
    }

    public abstract void onTick(SCandleRecord candleRecord);

    public Object getChartAsJson() {
        Map<String, Object> jsonChart = new HashMap<>();
        jsonChart.put("candlesticks", series.getBarData());
        jsonChart.put("symbol", symbol);
        jsonChart.put("periodCode", periodCode);
        jsonChart.put("longTradingRecord", longStrategy.getTradingRecord());
        jsonChart.put("shortTradingRecord", shortStrategy.getTradingRecord());
        jsonChart.put("indicators", shortStrategy.getIndicators());
        return jsonChart;
    }
}
