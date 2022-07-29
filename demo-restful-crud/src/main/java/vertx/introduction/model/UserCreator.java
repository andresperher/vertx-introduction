package vertx.introduction.model;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public class UserCreator {

    private final String username;

    private final String email;

    @JsonCreator
    public UserCreator(@JsonProperty("username") String username, @JsonProperty("email") String email) {
        this.username = username;
        this.email = email;
    }

    public User toUser() {
        return new User(null, this.username, this.email);
    }

    public User toUser(UUID id) {
        return new User(id, this.username, this.email);
    }


    @Override
    public String toString() {
        return "UserCreator{" +
                "username='" + username + '\'' +
                ", email='" + email + '\'' +
                '}';
    }

}
