package vertx.introduction.database;

import io.vertx.core.Future;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.SqlResult;
import io.vertx.sqlclient.Tuple;
import vertx.introduction.model.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;


public class UsersDAO {

    public static final String USERS_TABLE = "users";

    private static final Function<Row, User> MAPPER = row -> new User(row.getUUID("id"),
                                                                      row.getString("username"),
                                                                      row.getString("email"));

    private final PgPool pgPool;


    public UsersDAO(PgPool pgPool) {
        this.pgPool = pgPool;
    }


    public Future<List<User>> getAll() {
        return this.pgPool.query("SELECT * FROM " + USERS_TABLE + " ORDER BY id ASC")
                          .execute()
                          .map(rows -> StreamSupport.stream(rows.spliterator(), false)
                                                    .map(MAPPER)
                                                    .collect(Collectors.toList()));
    }

    public Future<User> getById(UUID id) {
        return this.pgPool.preparedQuery("SELECT * FROM " + USERS_TABLE + " WHERE id = $1")
                          .execute(Tuple.of(id))
                          .map(RowSet::iterator)
                          .map(iterator -> iterator.hasNext() ? MAPPER.apply(iterator.next()) : null)
                          .map(Optional::ofNullable)
                          .map(p -> p.orElseThrow(() -> new UserNotFoundException(id)));
    }


    public Future<UUID> save(User user) {
        return this.pgPool.preparedQuery("INSERT INTO " + USERS_TABLE + " (username, email) VALUES ($1, $2) RETURNING (id)")
                          .execute(Tuple.of(user.getUsername(), user.getEmail()))
                          .map(rows -> rows.iterator().next().getUUID("id"));
    }


    public Future<Integer> update(User user) {
        return this.pgPool.preparedQuery("UPDATE " + USERS_TABLE + " SET username = $1, email = $2 WHERE id = $3")
                          .execute(Tuple.of(user.getUsername(), user.getEmail(), user.getId()))
                          .map(SqlResult::rowCount);
    }


    public Future<Integer> deleteAll() {
        return this.pgPool.query("DELETE FROM " + USERS_TABLE)
                          .execute()
                          .map(SqlResult::rowCount);
    }

    public Future<Integer> deleteById(UUID id) {
        return this.pgPool.preparedQuery("DELETE FROM " + USERS_TABLE + " WHERE id = $1")
                          .execute(Tuple.of(id))
                          .map(SqlResult::rowCount);
    }

}
