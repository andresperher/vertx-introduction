package vertx.introduction;


import io.vertx.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vertx.introduction.server.MainVerticle;


public class VertXLauncher {

    private static final Logger log = LoggerFactory.getLogger(VertXLauncher.class);


    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();

        vertx.deployVerticle(new MainVerticle())
             .onSuccess(res -> log.info("HTTP Server verticle deployed successfully"))
             .onFailure(throwable -> log.error("Error deploying HTTP Server verticle: {}", throwable.getMessage(), throwable));
    }

}
