package pro.xstore.api.message.command;

import org.json.simple.JSONObject;
import pro.xstore.api.message.error.APICommandConstructionException;
import pro.xstore.api.message.error.APICommunicationException;
import pro.xstore.api.message.error.APIReplyParseException;
import pro.xstore.api.message.response.APIErrorResponse;
import pro.xstore.api.message.response.TradeTransactionStatusResponse;
import pro.xstore.api.sync.SyncAPIConnector;

public class TradeTransactionStatusCommand extends BaseCommand {

    private SyncAPIConnector connector;

    public TradeTransactionStatusCommand(JSONObject arguments, SyncAPIConnector connector) throws APICommandConstructionException {
        super(arguments);
        this.connector = connector;
    }

    @Override
    public String getCommandName() {
        return "tradeTransactionStatus";
    }

    @Override
    public String[] getRequiredArguments() throws APICommandConstructionException {
        return new String[]{"order"};
    }

    public TradeTransactionStatusResponse execute(long orderId) throws APICommandConstructionException, APIReplyParseException, APICommunicationException, APIErrorResponse {
        return APICommandFactory.executeTradeTransactionStatusCommand(connector, orderId);
    }
}
