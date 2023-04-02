package pl.bzowski.tradingbot.strategies;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ta4j.core.BarSeries;
import org.ta4j.core.Rule;
import org.ta4j.core.indicators.EMAIndicator;
import org.ta4j.core.indicators.ParabolicSarIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.num.DecimalNum;
import org.ta4j.core.rules.*;
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
    @Override
    public StrategyWithLifeCycle getLongStrategy() throws APICommandConstructionException {
        if (series == null) {
            throw new IllegalArgumentException("Series cannot be null");
        }
        Rule enterRule = new OverIndicatorRule(cpi, ema200);
        Rule exitRule = new StopGainRule(cpi, DecimalNum.valueOf(1.5)).or(new StopLossRule(cpi, DecimalNum.valueOf(0.5)));
        return new StrategyWithLifeCycle("SIMPLE-SAR+EMA200-LONG", symbol, enterRule, exitRule, openPosition, closePosition, this, cpi,
                ema200);
    }

    @Override
    public StrategyWithLifeCycle getShortStrategy() throws APICommandConstructionException {
        if (series == null) {
            throw new IllegalArgumentException("Series cannot be null");
        }
        Rule enterRule = new UnderIndicatorRule(cpi, ema200);
        Rule exitRule = new StopGainRule(cpi, DecimalNum.valueOf(1.5)).or(new StopLossRule(cpi, DecimalNum.valueOf(0.5)));
        return new StrategyWithLifeCycle("SIMPLE-SAR+EMA200-SHORT", symbol, enterRule, exitRule, openPosition, closePosition, this, cpi,
                ema200); // ONLY SHORT
    }

    @Override
    public long candlesOfMillisArchive() {
        return HOW_MUCH_CANDLES_TO_BEGIN;
    }

    @Override
    public double stoplossValue(int index) {
        return 0;
    }

}