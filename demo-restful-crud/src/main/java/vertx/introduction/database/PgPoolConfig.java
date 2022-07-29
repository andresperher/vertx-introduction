package vertx.introduction.database;

import io.vertx.core.Vertx;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.PoolOptions;


public class PgPoolConfig {

    public static PgPool create(Vertx vertx, String host, int port, String database, String user, String password) {
        PoolOptions poolOptions = new PoolOptions().setMaxSize(5);
        PgConnectOptions connectOptions = new PgConnectOptions().setHost(host)
                                                                .setPort(port)
                                                                .setDatabase(database)
                                                                .setUser(user)
                                                                .setPassword(password);
        return PgPool.pool(vertx, connectOptions, poolOptions);
    }

}
