package pl.bzowski.tradingbot;

import org.ta4j.core.BarSeries;
import pl.bzowski.chart.ChartService;
import pl.bzowski.series.SeriesHandler;
import pl.bzowski.tradingbot.strategies.StrategyBuilder;
import pro.xstore.api.message.codes.PERIOD_CODE;
import pro.xstore.api.message.error.APICommandConstructionException;
import pro.xstore.api.message.error.APICommunicationException;
import pro.xstore.api.message.error.APIReplyParseException;
import pro.xstore.api.message.response.APIErrorResponse;
import pro.xstore.api.message.response.ChartResponse;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.time.Duration;

@Singleton
public class BotService {

    @Inject
    SeriesHandler seriesHandler;

    @Inject
    ChartService chartService;

    public TradingBot createBotInstance(String symbol, StrategyBuilder strategyBuilder, PERIOD_CODE periodCode) {
        try {
            TradingBotBuilder tradingBotBuilder = new TradingBotBuilder();
            ChartResponse chartResponse = getArchiveCandles(symbol, periodCode, strategyBuilder.candlesOfMillisArchive());
            BarSeries minuteSeries = seriesHandler.createSeries(symbol, periodCode, chartResponse.getRateInfos(), chartResponse.getDigits());
            return tradingBotBuilder.symbol(symbol).series(minuteSeries).periodCode(periodCode).strategy(strategyBuilder).build();
        } catch (APIErrorResponse | APICommunicationException | APIReplyParseException
                 | APICommandConstructionException apiErrorResponse) {
            apiErrorResponse.printStackTrace();
        }
        return null;
    }

    private ChartResponse getArchiveCandles(String symbol, PERIOD_CODE periodCode, long durationOfMillis)
            throws APIErrorResponse, APICommunicationException, APIReplyParseException,
            APICommandConstructionException {
        return chartService.getChartForPeriodFromNow(symbol, periodCode, durationOfMillis);

    }
}
