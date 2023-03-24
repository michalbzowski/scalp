package pl.bzowski.tradingbot;

import org.ta4j.core.BarSeries;
import pl.bzowski.chart.ChartService;
import pl.bzowski.series.SeriesHandler;
import pl.bzowski.tradingbot.strategies.StrategyBuilder;
import pro.xstore.api.message.codes.PERIOD_CODE;
import pro.xstore.api.message.error.APICommandConstructionException;
import pro.xstore.api.message.error.APICommunicationException;
import pro.xstore.api.message.error.APIReplyParseException;
import pro.xstore.api.message.records.ChartRangeInfoRecord;
import pro.xstore.api.message.records.SymbolRecord;
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

    public TradingBot createBotInstance(String symbol, StrategyBuilder strategyBuilder) {
        try {
            TradingBotBuilder tradingBotBuilder = new TradingBotBuilder();
            BarSeries minuteSeries = seriesHandler.createSeries(symbol);
            ChartResponse chartResponse = getArchiveCandles(symbol);
            seriesHandler.fillSeries(chartResponse.getRateInfos(), chartResponse.getDigits(), minuteSeries);
            return tradingBotBuilder.symbol(symbol).series(minuteSeries).strategy(strategyBuilder).build();
        } catch (APIErrorResponse | APICommunicationException | APIReplyParseException
                 | APICommandConstructionException apiErrorResponse) {
            apiErrorResponse.printStackTrace();
        }
        return null;
    }

    private ChartResponse getArchiveCandles(String symbol)
            throws APIErrorResponse, APICommunicationException, APIReplyParseException,
            APICommandConstructionException {

        var SIXTY_MINUTES = Duration.ofMinutes(60).toMillis();
        return chartService.getChartForPeriodFromNow(symbol, PERIOD_CODE.PERIOD_H4, SIXTY_MINUTES);

    }
}
