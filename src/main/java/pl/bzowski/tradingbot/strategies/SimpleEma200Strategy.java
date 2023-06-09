package pl.bzowski.tradingbot.strategies;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ta4j.core.BarSeries;
import org.ta4j.core.Rule;
import org.ta4j.core.indicators.EMAIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.rules.*;
import pl.bzowski.indicators.NeverRule;
import pl.bzowski.tradingbot.positions.ClosePosition;
import pl.bzowski.tradingbot.positions.OpenPosition;
import pro.xstore.api.message.error.APICommandConstructionException;

import java.time.Duration;

public class SimpleEma200Strategy implements Strategy {

    private Logger logger = LoggerFactory.getLogger(SimpleEma200Strategy.class);
    private final String symbol;
    private OpenPosition openPosition;
    private ClosePosition closePosition;
    private BarSeries series;

    long HOW_MUCH_CANDLES_TO_BEGIN = Duration.ofMinutes(200).toMillis();
    private double stoplossValue = 0.0;
    ClosePriceIndicator cpi;
    EMAIndicator ema200;

    public SimpleEma200Strategy(String symbol, OpenPosition openPosition, ClosePosition closePosition, BarSeries series) {
        this.symbol = symbol;
        this.openPosition = openPosition;
        this.closePosition = closePosition;
        this.series = series;
        cpi = new ClosePriceIndicator(series);
        this.ema200 = new EMAIndicator(cpi, 200);

    }

    // dodac stop loss na poziomie parabolicSar oraz take Profit 1.5
    // dopasuj SL do r/r, bo czasem SAR jest bardzo daleko od ceny
    // Parabolic SAR Divergence - kiedy cena podąża w przeciwnym kierunku niż
    // indykator
    // Wtedy istnieje szansa, że rysowany przez cenę trend będzie kontynuowany
    // //TODO
    // Cena idzie do góry = bullish divergence
    // Cena idzie w dół = bearish divergence
    // Sygnałem jest przejście SAR na drugą stronę wykresu
    // buy otwiera się po cenie ask a ja w seirach mam ceny close - bid

    //Żeby uniknąć handlu w konsolidacji należy obserowwać ceny zamknięcia i dopiero gdy cena zamknie się powyżej poprzedniego maksimum
    //po tym zawroci i zamknie sie powyzej ostatniego minimum i wybije to ostatnie maksimum mozemy mowic o trendzie
    @Override
    public StrategyWithLifeCycle getLongStrategy() throws APICommandConstructionException {
        if (series == null) {
            throw new IllegalArgumentException("Series cannot be null");
        }
        Rule enterRule = new OverIndicatorRule(cpi, ema200);
        Rule exitRule = new NeverRule();
        return new StrategyWithLifeCycle("SIMPLE-SAR+EMA200-LONG", symbol, enterRule, exitRule, openPosition, closePosition, this, cpi,
                ema200);
    }

    @Override
    public StrategyWithLifeCycle getShortStrategy() throws APICommandConstructionException {
        if (series == null) {
            throw new IllegalArgumentException("Series cannot be null");
        }
        Rule enterRule = new UnderIndicatorRule(cpi, ema200);
        Rule exitRule = new NeverRule();
        return new StrategyWithLifeCycle("SIMPLE-SAR+EMA200-SHORT", symbol, enterRule, exitRule, openPosition, closePosition, this, cpi,
                ema200); // ONLY SHORT
    }

    @Override
    public long candlesOfMillisArchive() {
        return HOW_MUCH_CANDLES_TO_BEGIN;
    }

    @Override
    public double stoplossValue(int index, boolean aLong) {
        var v = (double) Math.round(ema200.getValue(index).doubleValue() * 100000d) / 100000d;
        logger.info("Stop loss will be set to:" + v);
        return v;
    }

    @Override
    public double takeProfitValue(int index, boolean aLong) {
        var cpiV = cpi.getValue(index);
        var emaV = ema200.getValue(index);
        var diff = cpiV.minus(emaV);
        var diff2 = diff.doubleValue() * 2;
        var v = cpiV.doubleValue() + diff2;
        return (double) Math.round(v * 100000d) / 100000d;
    }

}