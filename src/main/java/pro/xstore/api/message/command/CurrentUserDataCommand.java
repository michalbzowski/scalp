package pro.xstore.api.message.command;

import org.json.simple.JSONObject;
import pro.xstore.api.message.error.APICommandConstructionException;

public class CurrentUserDataCommand extends BaseCommand {

    public CurrentUserDataCommand() throws APICommandConstructionException{
        super(new JSONObject());
    }

    @Override
    public String getCommandName() {
        return "getCurrentUserData";
    }

    @Override
    public String[] getRequiredArguments() throws APICommandConstructionException {
        return new String[]{};
    }
}
