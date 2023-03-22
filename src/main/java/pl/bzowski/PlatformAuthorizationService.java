package pl.bzowski;

import pro.xstore.api.message.command.APICommandFactory;
import pro.xstore.api.message.error.APICommandConstructionException;
import pro.xstore.api.message.error.APICommunicationException;
import pro.xstore.api.message.error.APIReplyParseException;
import pro.xstore.api.message.response.APIErrorResponse;
import pro.xstore.api.message.response.LoginResponse;
import pro.xstore.api.sync.Credentials;
import pro.xstore.api.sync.ServerEnum;
import pro.xstore.api.sync.SyncAPIConnector;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.inject.Singleton;
import java.io.IOException;

@Singleton
public class PlatformAuthorizationService {

    @ConfigProperty(name = "platform.login")
    String login;

    @ConfigProperty(name = "platform.password")
    String password;

    @ConfigProperty(name = "platform.appId")
    String appId;

    @ConfigProperty(name = "platform.appName")
    String appName;

    public boolean authorize() throws IOException, APIErrorResponse, APICommunicationException, APIReplyParseException, APICommandConstructionException {
        Credentials credentials = new Credentials(login, password, appId, appName);
        SyncAPIConnector connector = new SyncAPIConnector(ServerEnum.REAL);
        LoginResponse loginResponse = APICommandFactory.executeLoginCommand(connector, credentials);
        return loginResponse.getStatus();
    }
}
