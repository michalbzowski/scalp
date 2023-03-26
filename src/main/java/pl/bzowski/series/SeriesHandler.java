package pl.bzowski.series;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseBar;
import org.ta4j.core.BaseBarSeries;
import org.ta4j.core.aggregator.BarAggregator;
import org.ta4j.core.aggregator.BaseBarSeriesAggregator;
import org.ta4j.core.aggregator.DurationBarAggregator;
import org.ta4j.core.num.DecimalNum;
import pro.xstore.api.message.codes.PERIOD_CODE;
import pro.xstore.api.message.records.RateInfoRecord;

import javax.inject.Singleton;
import java.time.*;
import java.util.HashMap;
import java.util.List;

@Singleton
public class SeriesHandler {
    private static final Logger logger = LoggerFactory.getLogger(SeriesHandler.class);

//    private final PERIOD_CODE periodCode = PERIOD_CODE.PERIOD_M1;
    private final HashMap<String, BarSeries> series = new HashMap<>();

    public BarSeries createSeries(String symbol) {
        if (series.containsKey(symbol)) {
            return series.get(symbol);
        }
        BarSeries series = new BaseBarSeries(symbol);
        this.series.put(symbol, series);

        return this.series.get(symbol);
    }

    public void fillSeries(List<RateInfoRecord> archiveCandles, int digits, BarSeries series, PERIOD_CODE periodCode) {
        double divider = Math.pow(10, digits);
        archiveCandles.forEach(record -> {
            long ctm = record.getCtm();
            long code = periodCode.getCode();
            double close = (record.getOpen() + record.getClose()) / divider;
            double open = record.getOpen() / divider;
            double high = (record.getOpen() + record.getHigh()) / divider;
            double low = (record.getOpen() + record.getLow()) / divider;
            BaseBar bar = getBaseBar(code, ctm, close, open, high, low);
            series.addBar(bar);
        });
    }



    public BarSeries convertToPeriod(String symbol, PERIOD_CODE periodCode) {
        BarAggregator barAggregator = new DurationBarAggregator(Duration.ofMinutes(periodCode.getCode()));
        BaseBarSeriesAggregator baseBarSeriesAggregator = new BaseBarSeriesAggregator(barAggregator);
        BarSeries barSeries = series.get(symbol);
        return baseBarSeriesAggregator.aggregate(barSeries);
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
}
