package org.ta4j.core.rules;

import org.junit.jupiter.api.Test;
import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseBar;
import org.ta4j.core.BaseBarSeries;
import org.ta4j.core.num.DecimalNum;
import pro.xstore.api.message.codes.PERIOD_CODE;

import java.time.*;


public class BetweenAngleRuleTest {

    @Test
    public void doit() {
        BarSeries barSeries = new BaseBarSeries();
        for (int i = 1; i <= 150; i++) {
            barSeries.addBar(getBaseBar(PERIOD_CODE.PERIOD_M1.getCode(), i, 1.0, 2.0, 2.0, 1.0));
        }
        for (int i = 151; i <= 300; i++) {
            barSeries.addBar(getBaseBar(PERIOD_CODE.PERIOD_M1.getCode(), i, 2.0, 3.0, 3.0, 2.0));
        }
        BetweenAngleRule betweenAngleRule = new BetweenAngleRule(barSeries);
        var satisfied = betweenAngleRule.isSatisfied(150);
//        assertThat(satisfied, is(false));
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