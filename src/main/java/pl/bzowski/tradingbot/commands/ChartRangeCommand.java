package pl.bzowski.tradingbot.commands;

import pro.xstore.api.message.command.APICommandFactory;
import pro.xstore.api.message.error.APICommandConstructionException;
import pro.xstore.api.message.error.APICommunicationException;
import pro.xstore.api.message.error.APIReplyParseException;
import pro.xstore.api.message.records.ChartRangeInfoRecord;
import pro.xstore.api.message.response.APIErrorResponse;
import pro.xstore.api.message.response.ChartResponse;
import pro.xstore.api.sync.SyncAPIConnector;

public class ChartRangeCommand {

    private final SyncAPIConnector connector;

    public ChartRangeCommand(SyncAPIConnector connector) {
        this.connector = connector;
    }

    public ChartResponse execute(ChartRangeInfoRecord record) throws APICommandConstructionException, APIReplyParseException, APIErrorResponse, APICommunicationException {
        return APICommandFactory.executeChartRangeCommand(connector, record);
    }

}
