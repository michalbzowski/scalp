package org.ta4j.core.rules;

import org.ta4j.core.BarSeries;
import org.ta4j.core.TradingRecord;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.indicators.helpers.CombineIndicator;
import org.ta4j.core.indicators.helpers.PreviousValueIndicator;
import org.ta4j.core.num.Num;

public class BetweenAngleRule extends AbstractRule {

    private ClosePriceIndicator cpi;

    public BetweenAngleRule(BarSeries series) {
        cpi = new ClosePriceIndicator(series);
    }

    @Override
    public boolean isSatisfied(int index) {
        CombineIndicator diff = CombineIndicator.minus(cpi, new PreviousValueIndicator(cpi, 1));
        Num val = diff.getValue(index);
        Num currVal = cpi.getValue(index);
        //(x - 1, val     )
        //(x    , currVal)

        var tanA = val.doubleValue();
        //m = tan a
        var degrees = Math.toDegrees(tanA);
        return degrees > 30;
    }

    @Override
    public boolean isSatisfied(int index, TradingRecord tradingRecord) {
        return false;
    }
}
