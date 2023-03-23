package pl.bzowski;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pro.xstore.api.sync.Connector;
import pro.xstore.api.sync.ServerEnum;
import pro.xstore.api.sync.SyncAPIConnector;

import javax.enterprise.context.BeforeDestroyed;
import javax.inject.Singleton;
import java.io.IOException;

@Singleton
public class ConnectorProvider {

    Logger logger = LoggerFactory.getLogger(ConnectorProvider.class);
    private static final SyncAPIConnector connector;

    static {
        try {
            connector = new SyncAPIConnector(ServerEnum.REAL);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    SyncAPIConnector get() {
        logger.info("connector gotten");
        return connector;
    }

    @BeforeDestroyed(value = Singleton.class)
    void cleanUp() {
        logger.info("Attempt to disconnect stream");
        connector.disconnectStream();
        logger.info("Stream disconnected");
    }
}
