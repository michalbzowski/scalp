package pl.bzowski.tradingbot.strategies;

import org.ta4j.core.BarSeries;
import org.ta4j.core.Rule;
import org.ta4j.core.indicators.EMAIndicator;
import org.ta4j.core.indicators.ParabolicSarIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.indicators.helpers.CombineIndicator;
import org.ta4j.core.indicators.helpers.PreviousValueIndicator;
import org.ta4j.core.num.DecimalNum;
import org.ta4j.core.num.Num;
import org.ta4j.core.rules.*;
import pl.bzowski.tradingbot.positions.ClosePosition;
import pl.bzowski.tradingbot.positions.OpenPosition;
import pro.xstore.api.message.error.APICommandConstructionException;
import pro.xstore.api.sync.SyncAPIConnector;

import java.time.Duration;

public class SimpleSarEma200Strategy implements Strategy {

    private final String symbol;
    private OpenPosition openPosition;
    private ClosePosition closePosition;
    private BarSeries series;

    long HOW_MUCH_CANDLES_TO_BEGIN = Duration.ofMinutes(200).toMillis();
    private double stoplossValue = 0.0;
    ParabolicSarIndicator parabolicSarIndicator;
    ClosePriceIndicator cpi;
    EMAIndicator ema200;

    public SimpleSarEma200Strategy(String symbol, OpenPosition openPosition, ClosePosition closePosition, BarSeries series) {
        this.symbol = symbol;
        this.openPosition = openPosition;
        this.closePosition = closePosition;
        this.series = series;
        parabolicSarIndicator = new ParabolicSarIndicator(series);
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
    @Override
    public StrategyWithLifeCycle getLongStrategy() throws APICommandConstructionException {
        if (series == null) {
            throw new IllegalArgumentException("Series cannot be null");
        }
        Rule enterRule = new CrossedDownIndicatorRule(parabolicSarIndicator, cpi)
                .and(new OverIndicatorRule(cpi, ema200));
        Rule exitRule = new CrossedUpIndicatorRule(parabolicSarIndicator, cpi).or(new StopGainRule(cpi, DecimalNum.valueOf(1.5)));
        return new StrategyWithLifeCycle("SIMPLE-SAR+EMA200-LONG", symbol, enterRule, exitRule, openPosition, closePosition, this, parabolicSarIndicator, cpi,
                ema200);
    }

    @Override
    public StrategyWithLifeCycle getShortStrategy() throws APICommandConstructionException {
        if (series == null) {
            throw new IllegalArgumentException("Series cannot be null");
        }
        Rule enterRule = new CrossedUpIndicatorRule(parabolicSarIndicator, cpi)
                .and(new UnderIndicatorRule(cpi, ema200));
        Rule exitRule = new CrossedDownIndicatorRule(parabolicSarIndicator, cpi).or(new StopGainRule(cpi, DecimalNum.valueOf(1.5)));
        return new StrategyWithLifeCycle("SIMPLE-SAR+EMA200-SHORT", symbol, enterRule, exitRule, openPosition, closePosition, this, parabolicSarIndicator, cpi,
                ema200); // ONLY SHORT
    }

    @Override
    public long candlesOfMillisArchive() {
        return HOW_MUCH_CANDLES_TO_BEGIN;
    }

    @Override
    public double stoplossValue(int index) {
        return parabolicSarIndicator.getValue(index).doubleValue();
    }

}