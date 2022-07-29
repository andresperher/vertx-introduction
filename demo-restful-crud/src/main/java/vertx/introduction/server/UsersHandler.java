package vertx.introduction.server;


import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vertx.introduction.model.UserCreator;
import vertx.introduction.database.UsersDAO;

import java.util.Map;
import java.util.UUID;


public class UsersHandler {

    private static final Logger log = LoggerFactory.getLogger(UsersHandler.class);

    private final UsersDAO usersDAO;


    public UsersHandler(UsersDAO usersDAO) {
        this.usersDAO = usersDAO;
    }


    public void getAll(RoutingContext rc) {
        this.usersDAO.getAll()
                     .onSuccess(users -> rc.response().end(Json.encode(users)));
    }

    public void getById(RoutingContext rc) {
        UUID id = UUID.fromString(rc.pathParam("id"));
        this.usersDAO.getById(id)
                     .onSuccess(user -> rc.response().end(Json.encode(user)))
                     .onFailure(throwable -> rc.fail(HttpStatus.SC_NOT_FOUND, throwable));
    }


    public void save(RoutingContext rc) {
        UserCreator userCreator = rc.body().asPojo(UserCreator.class);
        log.debug("User created from request body: {}", userCreator);
        this.usersDAO.save(userCreator.toUser())
                     .onSuccess(uuid -> rc.response()
                                                 .putHeader("ID", uuid.toString())
                                                 .setStatusCode(HttpStatus.SC_CREATED).end())
                     .onFailure(throwable -> rc.fail(HttpStatus.SC_METHOD_FAILURE, throwable));
    }


    public void update(RoutingContext rc) {
        UUID selectedId = UUID.fromString(rc.pathParam("id"));
        UserCreator userCreator = rc.body().asPojo(UserCreator.class);
        log.debug("ID to update: [{}] -> New data: {}", selectedId, userCreator);
        this.usersDAO.update(userCreator.toUser(selectedId))
                     .onSuccess(res -> rc.response().end(Json.encode(Map.of("updated_users", res))))
                     .onFailure(throwable -> rc.fail(HttpStatus.SC_NOT_FOUND, throwable));
    }


    public void deleteAll(RoutingContext rc) {
        this.usersDAO.deleteAll()
                     .onSuccess(res -> rc.response().end(Json.encode(Map.of("deleted_users", res))))
                     .onFailure(throwable -> rc.fail(HttpStatus.SC_NOT_FOUND, throwable));
    }

    public void deleteById(RoutingContext rc) {
        UUID id = UUID.fromString(rc.pathParam("id"));
        this.usersDAO.deleteById(id)
                     .onSuccess(count -> rc.response().end(Json.encode(Map.of("deleted_users", count))))
                     .onFailure(throwable -> rc.fail(HttpStatus.SC_NOT_FOUND, throwable));
    }

}
