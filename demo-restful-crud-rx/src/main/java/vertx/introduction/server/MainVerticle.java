package vertx.introduction.server;

import io.reactivex.rxjava3.core.Completable;
import io.vertx.rxjava3.core.AbstractVerticle;
import io.vertx.rxjava3.ext.web.Router;
import io.vertx.rxjava3.pgclient.PgPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vertx.introduction.UsersDataInitializer;
import vertx.introduction.database.PgPoolConfig;
import vertx.introduction.database.UsersDAO;


public class MainVerticle extends AbstractVerticle {

    private static final Logger log = LoggerFactory.getLogger(MainVerticle.class);

    public static final int HTTP_PORT = 8888;

    @Override
    public Completable rxStart() {
        log.info("Starting HTTP Server...");

        PgPool pgPool = PgPoolConfig.create(vertx, "localhost", 5432, "vertx-intro", "user", "password");
        UsersDAO usersDAO = new UsersDAO(pgPool);
        UsersHandler usersHandler = new UsersHandler(usersDAO);
        Router router = UsersRouterConfig.create(vertx, usersHandler);

        UsersDataInitializer.create(pgPool).run();

        return vertx.createHttpServer()
                    .requestHandler(router)
                    .rxListen(HTTP_PORT)
                    .ignoreElement();
    }

}
