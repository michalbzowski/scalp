package pl.bzowski.archive;

import pl.bzowski.chart.ChartService;
import pro.xstore.api.message.codes.PERIOD_CODE;
import pro.xstore.api.message.error.APICommandConstructionException;
import pro.xstore.api.message.error.APICommunicationException;
import pro.xstore.api.message.error.APIReplyParseException;
import pro.xstore.api.message.response.APIErrorResponse;
import pro.xstore.api.message.response.ChartResponse;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ArchiveCandlesService {

    @Inject
    ChartService chartService;

    public ChartResponse getArchiveCandles(String symbol, PERIOD_CODE periodCode, long durationOfMillis)
            throws APIErrorResponse, APICommunicationException, APIReplyParseException,
            APICommandConstructionException {
        return chartService.getChartForPeriodFromNow(symbol, periodCode, durationOfMillis);

    }
}
