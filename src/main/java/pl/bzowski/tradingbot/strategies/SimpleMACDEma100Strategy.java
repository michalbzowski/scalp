package pl.bzowski.tradingbot.strategies;

import org.ta4j.core.BarSeries;
import org.ta4j.core.Rule;
import org.ta4j.core.indicators.EMAIndicator;
import org.ta4j.core.indicators.MACDIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.num.DecimalNum;
import org.ta4j.core.num.Num;
import org.ta4j.core.rules.CrossedDownIndicatorRule;
import org.ta4j.core.rules.CrossedUpIndicatorRule;
import org.ta4j.core.rules.OverIndicatorRule;
import org.ta4j.core.rules.UnderIndicatorRule;

public class SimpleMACDEma100Strategy implements StrategyBuilder {

    private final String symbol;

    public SimpleMACDEma100Strategy(String symbol) {
        this.symbol = symbol;
    }

    public StrategyWithLifeCycle getLongStrategy(BarSeries series) {

        ClosePriceIndicator cpi = new ClosePriceIndicator(series);
        MACDIndicator macdIndicator = new MACDIndicator(cpi);
        EMAIndicator macdLine = macdIndicator.getShortTermEma(); //FASTER - MACD LINE
        EMAIndicator signal = macdIndicator.getLongTermEma(); //SLOWER - SIGNALLIE

        EMAIndicator ema100 = new EMAIndicator(cpi, 100);

        Rule entryRule = new OverIndicatorRule(cpi, ema100).and(new CrossedUpIndicatorRule(macdLine, signal));
        Rule exitRule = new UnderIndicatorRule(cpi, ema100.getValue(series.getEndIndex()))
                .or(new OverIndicatorRule(cpi, takeLongProfit(series, cpi, ema100)));

        return new StrategyWithLifeCycle("SIMPLE-MACD+EMA100-LONG", symbol, entryRule, exitRule, macdIndicator, cpi, ema100);
    }

    @Override
    public StrategyWithLifeCycle getShortStrategy(BarSeries series) {

        ClosePriceIndicator cpi = new ClosePriceIndicator(series);
        MACDIndicator macdIndicator = new MACDIndicator(cpi);
        EMAIndicator macdLine = macdIndicator.getShortTermEma(); //FASTER - MACD LINE
        EMAIndicator signal = macdIndicator.getLongTermEma(); //SLOWER - SIGNALLIE

        EMAIndicator ema100 = new EMAIndicator(cpi, 100);

        Rule entryRule = new UnderIndicatorRule(cpi, ema100).and(new CrossedDownIndicatorRule(macdLine, signal));
        Rule exitRule = new OverIndicatorRule(cpi, ema100.getValue(series.getEndIndex()))
                .or(new UnderIndicatorRule(cpi, takeShortProfit(series, cpi, ema100)));

        return new StrategyWithLifeCycle("SIMPLE-MACD+EMA100-LONG", symbol, entryRule, exitRule, macdIndicator, cpi, ema100);
    }

    private Num takeShortProfit(BarSeries series, ClosePriceIndicator cpi, EMAIndicator ema100) {
        int endIndex = series.getEndIndex();
        Num price = cpi.getValue(endIndex);
        Num sl = ema100.getValue(endIndex);
        Num priceStopLossDistance = price.minus(sl);
        Num tp = priceStopLossDistance.multipliedBy(DecimalNum.valueOf(1.5));
        return price.plus(tp);
    }

    private Num takeLongProfit(BarSeries series, ClosePriceIndicator cpi, EMAIndicator ema100) {

        int endIndex = series.getEndIndex();
        Num price = cpi.getValue(endIndex);
        Num sl = ema100.getValue(endIndex);
        Num priceStopLossDistance = sl.minus(price);
        Num tp = priceStopLossDistance.multipliedBy(DecimalNum.valueOf(1.5));
        return price.minus(tp);
    }

}
