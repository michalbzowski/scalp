package pl.bzowski.platform.xstation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.bzowski.ConnectorProvider;
import pro.xstore.api.message.command.APICommandFactory;
import pro.xstore.api.message.error.APICommandConstructionException;
import pro.xstore.api.message.error.APICommunicationException;
import pro.xstore.api.message.error.APIReplyParseException;
import pro.xstore.api.message.response.APIErrorResponse;
import pro.xstore.api.message.response.LoginResponse;
import pro.xstore.api.sync.Credentials;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.inject.Inject;
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

    @Inject
    ConnectorProvider connectorProvider;

    Logger logger = LoggerFactory.getLogger(PlatformAuthorizationService.class);

    public boolean authorize() throws IOException, APICommunicationException, APIReplyParseException, APICommandConstructionException {
        logger.info("Attempt to authorize");
        Credentials credentials = new Credentials(login, password, appId, appName);
        var connector = connectorProvider.get();
        try {
            LoginResponse loginResponse = APICommandFactory.executeLoginCommand(connector, credentials);
            logger.info("Login status: " + loginResponse.getStatus());
            return loginResponse.getStatus();
        } catch (APIErrorResponse e) {
            logger.info("User already logged");
            return true;
        }


    }
}
