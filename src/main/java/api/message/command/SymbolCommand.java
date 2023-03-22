package api.message.command;

import org.json.simple.JSONObject;
import pro.xstore.api.message.error.APICommandConstructionException;
import pro.xstore.api.message.error.APICommunicationException;
import pro.xstore.api.message.error.APIReplyParseException;
import pro.xstore.api.message.response.APIErrorResponse;
import pro.xstore.api.message.response.SymbolResponse;
import pro.xstore.api.sync.SyncAPIConnector;

public class SymbolCommand extends BaseCommand {

    private SyncAPIConnector connector;

    public SymbolCommand(JSONObject arguments, SyncAPIConnector connector) throws APICommandConstructionException {
        super(arguments);
        this.connector = connector;
    }

    @Override
    public String getCommandName() {
        return "getSymbol";
    }

    @Override
    public String[] getRequiredArguments() throws APICommandConstructionException {
        return new String[]{"symbol"};
    }

    public SymbolResponse execute(String symbol) throws APICommandConstructionException, APIReplyParseException, APIErrorResponse, APICommunicationException {
        return APICommandFactory.executeSymbolCommand(connector, symbol);
    }
}
