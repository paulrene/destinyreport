package my.destiny;

import com.beust.jcommander.JCommander;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import my.destiny.db.Storage;
import my.destiny.service.BungieService;
import my.destiny.service.DatabaseService;
import my.destiny.service.PeriodicJobService;
import my.destiny.service.TwitterService;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import spark.Redirect;
import spark.Spark;
import spark.debug.DebugScreen;
import spark.route.RouteOverview;
import spark.template.velocity.VelocityTemplateEngine;
import twitter4j.TwitterException;

public class Main {

    public static Config config = new Config();

    public static void main(String[] args) throws InterruptedException, TwitterException {
        new JCommander(config).parse(args);

        VelocityEngine velocity = new VelocityEngine();
        velocity.setProperty(RuntimeConstants.RESOURCE_LOADER, "class");
        velocity.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        velocity.setProperty(RuntimeConstants.RUNTIME_REFERENCES_STRICT, "true");
        velocity.setProperty(RuntimeConstants.VM_LIBRARY_AUTORELOAD, "true");
        velocity.setProperty(RuntimeConstants.FILE_RESOURCE_LOADER_CACHE, "false");
        velocity.setProperty(RuntimeConstants.VM_PERM_ALLOW_INLINE_REPLACE_GLOBAL, true);
        velocity.setProperty("class.resource.loader.modificationCheckInterval", "5"); // -1 in prod
        VelocityTemplateEngine engine = new VelocityTemplateEngine(velocity);

        Storage storage = new Storage();
        DatabaseService databaseService = new DatabaseService(storage);
        TwitterService twitterService = new TwitterService();
        BungieService bungieService = new BungieService(config.getBungieApiKey());
        PeriodicJobService periodicJobService = new PeriodicJobService(databaseService, bungieService);

        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(periodicJobService, 2, 60 * 5, TimeUnit.SECONDS);

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                executor.shutdownNow();
                Spark.stop();
                storage.stop();
            }
        });

        Spark.port(config.getPort());
        Spark.staticFileLocation("/public");
        // Spark.staticFiles.expireTime(60 * 15); // 15 minutes
        DebugScreen.enableDebugScreen();
        RouteOverview.enableRouteOverview();
        /*Spark.after(new Filter() {
            @Override
            public void handle(Request request, Response response) throws Exception {
                response.header("Content-Encoding", "gzip");
            }
        });*/
        Spark.redirect.get("/", "/clan/926258", Redirect.Status.SEE_OTHER);
        Spark.get("/clan/:clanId", new ClanRoute(engine, twitterService, bungieService, databaseService));
        Spark.get("/api/:endpoint", new ApiRoute(databaseService));
        Spark.awaitInitialization();


    }

}
