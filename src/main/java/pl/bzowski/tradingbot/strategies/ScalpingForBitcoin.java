package pl.bzowski.tradingbot.strategies;

import org.ta4j.core.BarSeries;
import org.ta4j.core.Rule;
import org.ta4j.core.indicators.EMAIndicator;
import org.ta4j.core.indicators.ParabolicSarIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.rules.*;

public class ScalpingForBitcoin implements StrategyBuilder {

    private final String symbol;

    public ScalpingForBitcoin(String symbol) {
        this.symbol = symbol;
    }

    @Override
    public StrategyWithLifeCycle getLongStrategy(BarSeries series) {
        if (series == null) {
            throw new IllegalArgumentException("Series cannot be null");
        }
        ParabolicSarIndicator parabolicSarIndicator = new ParabolicSarIndicator(series);
        ClosePriceIndicator cpi = new ClosePriceIndicator(series);
        EMAIndicator ema5 = new EMAIndicator(cpi, 5);
        EMAIndicator ema8 = new EMAIndicator(cpi, 8);
        EMAIndicator ema13 = new EMAIndicator(cpi, 13);

        Rule enterRule = new CrossedDownIndicatorRule(parabolicSarIndicator, ema5)
                .and(new OverIndicatorRule(cpi, ema5))
                .and(new OverIndicatorRule(ema5, ema8))
                .and(new OverIndicatorRule(ema8, ema13));

        // Rule exitRule = new CrossedUpIndicatorRule(parabolicSarIndicator, ema5)
        //   .or(new UnderIndicatorRule(ema5, ema8))
        //   .or(new UnderIndicatorRule(ema8, ema13))
        //   .or(new UnderIndicatorRule(cpi, ema5));
        Rule exitRule = new StopLossRule(cpi, 0.5).or(new StopGainRule(cpi, 2));
        return new StrategyWithLifeCycle("BITCOIN-SAR+EMA5+EMA8+EMA13-LONG", symbol, enterRule, exitRule, parabolicSarIndicator, cpi,
                ema5, ema8, ema13);
    }

    @Override
    public StrategyWithLifeCycle getShortStrategy(BarSeries series) {
        if (series == null) {
            throw new IllegalArgumentException("Series cannot be null");
        }
        ParabolicSarIndicator parabolicSarIndicator = new ParabolicSarIndicator(series);
        ClosePriceIndicator cpi = new ClosePriceIndicator(series);
        EMAIndicator ema5 = new EMAIndicator(cpi, 5);
        EMAIndicator ema8 = new EMAIndicator(cpi, 8);
        EMAIndicator ema13 = new EMAIndicator(cpi, 13);

        Rule enterRule = new CrossedUpIndicatorRule(parabolicSarIndicator, ema5)
                .and(new UnderIndicatorRule(cpi, ema5))
                .and(new UnderIndicatorRule(ema5, ema8))
                .and(new UnderIndicatorRule(ema8, ema13));

        // Rule exitRule = new CrossedDownIndicatorRule(parabolicSarIndicator, ema5)
        //         .or(new OverIndicatorRule(ema5, ema8))
        //         .or(new OverIndicatorRule(ema8, ema13))
        //         .or(new OverIndicatorRule(cpi, ema5));
        Rule exitRule = new StopLossRule(cpi, 0.5).or(new StopGainRule(cpi, 2));
        return new StrategyWithLifeCycle("BITCOIN-SAR+EMA5+EMA8+EMA13-SHORT", symbol, enterRule, exitRule, parabolicSarIndicator, cpi,
                ema5, ema8, ema13);
    }

}