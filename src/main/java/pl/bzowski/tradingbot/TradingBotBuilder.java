package pl.bzowski.tradingbot;

import org.ta4j.core.BarSeries;
import pl.bzowski.tradingbot.strategies.StrategyBuilder;
import pro.xstore.api.message.codes.PERIOD_CODE;

public class TradingBotBuilder {


    private String symbol;
    private BarSeries series;
    private StrategyBuilder strategyBuilder;
    private PERIOD_CODE periodCode;

    public TradingBotBuilder symbol(String symbol) {
        this.symbol = symbol;
        return this;
    }

    public TradingBotBuilder series(BarSeries series) {
        this.series = series;
        return this;
    }

    public TradingBotBuilder strategy(StrategyBuilder strategyBuilder) {
        this.strategyBuilder = strategyBuilder;
        return this;
    }

    public TradingBotBuilder periodCode(PERIOD_CODE periodCode) {
        this.periodCode = periodCode;
        return this;
    }

    public TradingBot build() {
        return new TradingBotImpl(symbol, series, periodCode, strategyBuilder.getLongStrategy(series), strategyBuilder.getShortStrategy(series));
    }
}
