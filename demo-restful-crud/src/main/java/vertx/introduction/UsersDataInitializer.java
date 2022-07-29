package vertx.introduction;

import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vertx.introduction.database.UsersDAO;

import java.util.List;
import java.util.stream.StreamSupport;


public class UsersDataInitializer {

    private static final Logger log = LoggerFactory.getLogger(UsersDataInitializer.class);

    private final PgPool pgPool;


    private UsersDataInitializer(PgPool pgPool) {
        this.pgPool = pgPool;
    }

    public static UsersDataInitializer create(PgPool pgPool) {
        return new UsersDataInitializer(pgPool);
    }


    public void run() {
        Tuple user1 = Tuple.of("Andres", "andres@email.com");
        Tuple user2 = Tuple.of("Pepito", "pepito@email.com");

        this.pgPool.withTransaction(c -> c.query("DELETE FROM " + UsersDAO.USERS_TABLE)
                                          .execute()
                                          .flatMap(rows -> c.preparedQuery("INSERT INTO " + UsersDAO.USERS_TABLE + " (username, email) VALUES ($1, $2)")
                                                            .executeBatch(List.of(user1, user2)))
                                          .flatMap(rows -> c.query("SELECT * FROM " + UsersDAO.USERS_TABLE)
                                                            .execute()))
                   .onSuccess(rows -> StreamSupport.stream(rows.spliterator(), true)
                                                   .forEach(row -> log.info("User obtained: {}", row.toJson())))
                   .onFailure(throwable -> log.error("Data initialization failed: {}", throwable.getMessage(), throwable))
                   .onComplete(r -> log.info("Data initialization done!"));
    }

}
