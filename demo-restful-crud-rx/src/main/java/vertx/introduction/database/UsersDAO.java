package vertx.introduction.database;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;
import io.vertx.rxjava3.pgclient.PgPool;
import io.vertx.rxjava3.sqlclient.Row;
import io.vertx.rxjava3.sqlclient.RowSet;
import io.vertx.rxjava3.sqlclient.SqlResult;
import io.vertx.rxjava3.sqlclient.Tuple;
import vertx.introduction.model.User;

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


    public Flowable<User> getAll() {
        return this.pgPool.query("SELECT * FROM " + USERS_TABLE + " ORDER BY id ASC")
                          .rxExecute()
                          .flattenAsFlowable(rows -> StreamSupport.stream(rows.spliterator(), false)
                                                                  .map(MAPPER)
                                                                  .collect(Collectors.toList()));
    }

    public Single<User> getById(UUID id) {
        return this.pgPool.preparedQuery("SELECT * FROM " + USERS_TABLE + " WHERE id = $1")
                          .rxExecute(Tuple.of(id))
                          .map(RowSet::iterator)
                          .flatMap(iterator -> iterator.hasNext() ? Single.just(MAPPER.apply(iterator.next()))
                                                                  : Single.error(new UserNotFoundException(id)));
    }


    public Single<UUID> save(User user) {
        return this.pgPool.preparedQuery("INSERT INTO " + USERS_TABLE + " (username, email) VALUES ($1, $2) RETURNING (id)")
                          .rxExecute(Tuple.of(user.getUsername(), user.getEmail()))
                          .map(rows -> rows.iterator().next().getUUID("id"));
    }


    public Single<Integer> update(User user) {
        return this.pgPool.preparedQuery("UPDATE " + USERS_TABLE + " SET username = $1, email = $2 WHERE id = $3")
                          .rxExecute(Tuple.of(user.getUsername(), user.getEmail(), user.getId()))
                          .map(SqlResult::rowCount);
    }


    public Single<Integer> deleteAll() {
        return this.pgPool.query("DELETE FROM " + USERS_TABLE)
                          .rxExecute()
                          .map(SqlResult::rowCount);
    }

    public Single<Integer> deleteById(UUID id) {
        return this.pgPool.preparedQuery("DELETE FROM " + USERS_TABLE + " WHERE id = $1")
                          .rxExecute(Tuple.of(id))
                          .map(SqlResult::rowCount);
    }

}
