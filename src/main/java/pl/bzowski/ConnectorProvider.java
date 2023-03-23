package pl.bzowski;

import pro.xstore.api.sync.Connector;
import pro.xstore.api.sync.ServerEnum;
import pro.xstore.api.sync.SyncAPIConnector;

import javax.inject.Singleton;
import java.io.IOException;

@Singleton
public class ConnectorProvider {

    private static final SyncAPIConnector connector;

    static {
        try {
            connector = new SyncAPIConnector(ServerEnum.REAL);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    SyncAPIConnector get() {
        return connector;
    }
}
