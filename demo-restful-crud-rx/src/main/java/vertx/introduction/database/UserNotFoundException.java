package vertx.introduction.database;

import java.util.UUID;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(UUID id) {
        super("User not found with ID: [" + id + "]");
    }

}
