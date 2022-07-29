package vertx.introduction.server;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.ext.web.Router;
import io.vertx.pgclient.PgPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vertx.introduction.UsersDataInitializer;
import vertx.introduction.database.PgPoolConfig;
import vertx.introduction.database.UsersDAO;


public class MainVerticle extends AbstractVerticle {

    private static final Logger log = LoggerFactory.getLogger(MainVerticle.class);

    public static final int HTTP_PORT = 8888;


    @Override
    public void start(Promise<Void> startPromise) {
        log.info("Starting HTTP Server...");

        PgPool pgPool = PgPoolConfig.create(vertx, "localhost", 5432, "vertx-intro", "user", "password");
        UsersDAO usersDAO = new UsersDAO(pgPool);
        UsersHandler usersHandler = new UsersHandler(usersDAO);
        Router router = UsersRouterConfig.create(vertx, usersHandler);

        UsersDataInitializer.create(pgPool).run();

        vertx.createHttpServer()
             .requestHandler(router)
             .listen(HTTP_PORT)
             .onSuccess(server -> {
                 startPromise.complete();
                 log.info("HTTP Server started on port {}", server.actualPort());
             })
             .onFailure(throwable -> {
                 log.error("Failed to start HTTP Server: {}", throwable.getMessage(), throwable);
                 startPromise.fail(throwable);
             });
    }

}
