package pl.bzowski.chart;

import pl.bzowski.ConnectorProvider;
import pro.xstore.api.message.codes.PERIOD_CODE;
import pro.xstore.api.message.error.APICommandConstructionException;
import pro.xstore.api.message.error.APICommunicationException;
import pro.xstore.api.message.error.APIReplyParseException;
import pro.xstore.api.message.records.ChartRangeInfoRecord;
import pro.xstore.api.message.response.APIErrorResponse;
import pro.xstore.api.message.response.ChartResponse;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ChartService {

    @Inject
    ConnectorProvider connectorProvider;

    public ChartResponse getChartForPeriodFromNow(String symbol, PERIOD_CODE periodCode, long period) throws APICommandConstructionException, APIErrorResponse, APICommunicationException, APIReplyParseException {
        long NOW = System.currentTimeMillis();
        ChartRangeInfoRecord record = new ChartRangeInfoRecord(symbol, periodCode, NOW - period, NOW);//tu minimum 52 TYGODNIE!!!
        ChartRangeCommand chartRangeCommand = new ChartRangeCommand(connectorProvider.get());
        return chartRangeCommand.execute(record);
    }

}
