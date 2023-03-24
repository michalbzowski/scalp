package pl.bzowski.tradingbot.strategies;

import org.ta4j.core.BarSeries;
import org.ta4j.core.Rule;
import org.ta4j.core.indicators.EMAIndicator;
import org.ta4j.core.indicators.ParabolicSarIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.rules.CrossedDownIndicatorRule;
import org.ta4j.core.rules.CrossedUpIndicatorRule;
import org.ta4j.core.rules.OverIndicatorRule;
import org.ta4j.core.rules.UnderIndicatorRule;

public class SimpleSarEma100Ema150Ema200Strategy implements StrategyBuilder {

    private final String symbol;

    public SimpleSarEma100Ema150Ema200Strategy(String symbol) {
        this.symbol = symbol;
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
    public StrategyWithLifeCycle getLongStrategy(BarSeries series) {
        if (series == null) {
            throw new IllegalArgumentException("Series cannot be null");
        }
        ParabolicSarIndicator parabolicSarIndicator = new ParabolicSarIndicator(series);
        ClosePriceIndicator cpi = new ClosePriceIndicator(series);
        EMAIndicator ema100 = new EMAIndicator(cpi, 100);
        EMAIndicator ema150 = new EMAIndicator(cpi, 150);
        EMAIndicator ema200 = new EMAIndicator(cpi, 200);

        Rule enterRule = new CrossedDownIndicatorRule(parabolicSarIndicator, cpi)
                .and(new OverIndicatorRule(cpi, ema100))
                .and(new OverIndicatorRule(ema100, ema150))
                .and(new OverIndicatorRule(ema150, ema200));

        Rule exitRule = new CrossedUpIndicatorRule(parabolicSarIndicator, cpi)
                .or(new UnderIndicatorRule(cpi, ema100))
                .or(new UnderIndicatorRule(ema100, ema150))
                .or(new UnderIndicatorRule(ema150, ema200));
        return new StrategyWithLifeCycle("SIMPLE-SAR+EMA100+EMA150++EMA200-LONG", symbol, enterRule, exitRule, parabolicSarIndicator, cpi,
                ema200);
    }

    @Override
    public StrategyWithLifeCycle getShortStrategy(BarSeries series) {
        if (series == null) {
            throw new IllegalArgumentException("Series cannot be null");
        }
        ParabolicSarIndicator parabolicSarIndicator = new ParabolicSarIndicator(series);
        ClosePriceIndicator cpi = new ClosePriceIndicator(series);
        EMAIndicator ema100 = new EMAIndicator(cpi, 100);
        EMAIndicator ema150 = new EMAIndicator(cpi, 150);
        EMAIndicator ema200 = new EMAIndicator(cpi, 200);

        Rule firstEnterRule = new UnderIndicatorRule(parabolicSarIndicator, ema100);

        Rule enterRule = new CrossedDownIndicatorRule(parabolicSarIndicator, cpi)
                .and(new UnderIndicatorRule(cpi, ema100))
                .and(new UnderIndicatorRule(ema100, ema150))
                .and(new UnderIndicatorRule(ema150, ema200));

        Rule exitRule = new CrossedDownIndicatorRule(parabolicSarIndicator, cpi)
                .or(new OverIndicatorRule(cpi, ema100))
                .or(new OverIndicatorRule(ema100, ema150))
                .or(new OverIndicatorRule(ema150, ema200));
        return new StrategyWithLifeCycle("SIMPLE-SAR+EMA100+EMA150+EMA200-SHORT", symbol, enterRule, exitRule, parabolicSarIndicator, cpi,
                ema200);
    }

}