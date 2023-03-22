package pro.xstore.api.message.command;

import org.json.simple.JSONObject;
import pro.xstore.api.message.error.APICommandConstructionException;
import pro.xstore.api.message.error.APICommunicationException;
import pro.xstore.api.message.error.APIReplyParseException;
import pro.xstore.api.message.records.TradeTransInfoRecord;
import pro.xstore.api.message.response.APIErrorResponse;
import pro.xstore.api.message.response.TradeTransactionResponse;
import pro.xstore.api.sync.SyncAPIConnector;

public class TradeTransactionCommand extends BaseCommand {

    private SyncAPIConnector connector;

    public TradeTransactionCommand(JSONObject arguments, SyncAPIConnector connector) throws APICommandConstructionException {
        super(arguments);
        this.connector = connector;
    }

    @Override
    public String getCommandName() {
        return "tradeTransaction";
    }

    @Override
    public String[] getRequiredArguments() throws APICommandConstructionException {
        return new String[]{"tradeTransInfo"};
    }

    public TradeTransactionResponse execute(TradeTransInfoRecord tradeRequest) throws APICommandConstructionException, APIReplyParseException, APICommunicationException, APIErrorResponse {
        return APICommandFactory.executeTradeTransactionCommand(connector, tradeRequest);
    }
}
