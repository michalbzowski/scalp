package api.message.command;

import org.json.simple.JSONObject;
import pro.xstore.api.message.error.APICommandConstructionException;
import pro.xstore.api.message.error.APICommunicationException;
import pro.xstore.api.message.error.APIReplyParseException;
import pro.xstore.api.message.records.ChartRangeInfoRecord;
import pro.xstore.api.message.response.APIErrorResponse;
import pro.xstore.api.message.response.ChartResponse;
import pro.xstore.api.sync.SyncAPIConnector;

public class ChartRangeCommand extends BaseCommand {

    private SyncAPIConnector connector;

    public ChartRangeCommand(JSONObject arguments, SyncAPIConnector connector) throws APICommandConstructionException {
        super(arguments);
        this.connector = connector;
    }

    @Override
    public String getCommandName() {
        return "getChartRangeRequest";
    }

    @Override
    public String[] getRequiredArguments() throws APICommandConstructionException {
        return new String[]{"info"};
    }

    public ChartResponse execute(ChartRangeInfoRecord record) throws APICommandConstructionException, APIReplyParseException, APIErrorResponse, APICommunicationException {
      return APICommandFactory.executeChartRangeCommand(connector, record);
    }
}
