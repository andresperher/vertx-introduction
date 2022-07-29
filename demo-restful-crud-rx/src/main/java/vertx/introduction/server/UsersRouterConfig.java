package vertx.introduction.server;

import io.vertx.rxjava3.core.Vertx;
import io.vertx.rxjava3.ext.web.Router;
import io.vertx.rxjava3.ext.web.handler.BodyHandler;
import org.apache.http.entity.ContentType;


public class UsersRouterConfig {

    public static final String ENDPOINT_USERS = "/users";
    public static final String ENDPOINT_USERS_BY_ID = "/users/:id";


    private UsersRouterConfig() {}


    public static Router create(Vertx vertx, UsersHandler usersHandler) {
        final String JSON_TYPE = ContentType.APPLICATION_JSON.getMimeType();
        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());

        router.get(ENDPOINT_USERS)
              .produces(JSON_TYPE)
              .handler(usersHandler::getAll);

        router.get(ENDPOINT_USERS_BY_ID)
              .produces(JSON_TYPE)
              .handler(usersHandler::getById);


        router.post(ENDPOINT_USERS)
              .consumes(JSON_TYPE)
              .handler(usersHandler::save);


        router.put(ENDPOINT_USERS_BY_ID)
              .consumes(JSON_TYPE)
              .produces(JSON_TYPE)
              .handler(usersHandler::update);


        router.delete(ENDPOINT_USERS)
              .produces(JSON_TYPE)
              .handler(usersHandler::deleteAll);

        router.delete(ENDPOINT_USERS_BY_ID)
              .produces(JSON_TYPE)
              .handler(usersHandler::deleteById);

        return router;
    }

}
