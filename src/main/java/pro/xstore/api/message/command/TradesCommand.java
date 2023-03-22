package pro.xstore.api.message.command;

import org.json.simple.JSONObject;
import pro.xstore.api.message.error.APICommandConstructionException;
import pro.xstore.api.message.error.APICommunicationException;
import pro.xstore.api.message.error.APIReplyParseException;
import pro.xstore.api.message.response.APIErrorResponse;
import pro.xstore.api.message.response.TradesResponse;
import pro.xstore.api.sync.SyncAPIConnector;

public class TradesCommand extends BaseCommand {

    private SyncAPIConnector connector;

    public TradesCommand(JSONObject argument, SyncAPIConnector connector) throws APICommandConstructionException {
        super(argument);
        this.connector = connector;
    }

    @Override
    public String getCommandName() {
        return "getTrades";
    }

    @Override
    public String[] getRequiredArguments() throws APICommandConstructionException {
        return new String[] {"openedOnly"};
    }

    public TradesResponse execute(boolean openedOnly) throws APICommandConstructionException, APIReplyParseException, APICommunicationException, APIErrorResponse {
        return APICommandFactory.executeTradesCommand(connector, openedOnly);
    }
}
