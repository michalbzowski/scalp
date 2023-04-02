package pl.bzowski.tradingbot;

import org.ta4j.core.BarSeries;
import org.ta4j.core.Rule;
import org.ta4j.core.indicators.EMAIndicator;
import org.ta4j.core.indicators.ParabolicSarIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.num.DecimalNum;
import org.ta4j.core.rules.*;
import pl.bzowski.tradingbot.positions.ClosePosition;
import pl.bzowski.tradingbot.positions.OpenPosition;
import pl.bzowski.tradingbot.strategies.Strategy;
import pl.bzowski.tradingbot.strategies.StrategyWithLifeCycle;
import pro.xstore.api.message.error.APICommandConstructionException;

public class SimpleTestStrategy implements Strategy {

    private final String symbol;
    private final OpenPosition openPosition;
    private final ClosePosition closePosition;
    private final BarSeries series;
    private final ClosePriceIndicator cpi;
    private final EMAIndicator ema200;

    public SimpleTestStrategy(String symbol, OpenPosition openPosition, ClosePosition closePosition, BarSeries series) {
        this.symbol = symbol;
        this.openPosition = openPosition;
        this.closePosition = closePosition;
        this.series = series;
        this.cpi = new ClosePriceIndicator(series);
        this.ema200 = new EMAIndicator(cpi, 200);

    }

    @Override
    public StrategyWithLifeCycle getLongStrategy() throws APICommandConstructionException {
        if (series == null) {
            throw new IllegalArgumentException("Series cannot be null");
        }
        Rule enterRule = new OverIndicatorRule(cpi, ema200);
        Rule exitRule = new StopGainRule(cpi, DecimalNum.valueOf(1.5)).or(new StopLossRule(cpi, DecimalNum.valueOf(0.5)));
        return new StrategyWithLifeCycle("SIMPLE-TEST-LONG", symbol, enterRule, exitRule, openPosition, closePosition, this, cpi, ema200);
    }

    @Override
    public StrategyWithLifeCycle getShortStrategy() throws APICommandConstructionException {
        if (series == null) {
            throw new IllegalArgumentException("Series cannot be null");
        }
        Rule enterRule = new UnderIndicatorRule(cpi, ema200);
        Rule exitRule = new StopGainRule(cpi, DecimalNum.valueOf(1.5)).or(new StopLossRule(cpi, DecimalNum.valueOf(0.5)));
        return new StrategyWithLifeCycle("SIMPLE-TEST-LONG", symbol, enterRule, exitRule, openPosition, closePosition, this, cpi, ema200);
    }

    @Override
    public long candlesOfMillisArchive() {
        return 200;
    }

    @Override
    public double stoplossValue(int index) {
        return 0;
    }
}
