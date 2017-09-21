package my.destiny;

import javax.servlet.ServletException;
import my.destiny.api.ClanFactsDataSource;
import my.destiny.service.DatabaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;
import spark.Route;

public class ApiRoute implements Route {
    private static final Logger log = LoggerFactory.getLogger(ApiRoute.class);

    private ClanFactsDataSource clanFactsDataSource;

    public ApiRoute(DatabaseService databaseService) {
        clanFactsDataSource = new ClanFactsDataSource(databaseService);
        try {
            clanFactsDataSource.init();
        } catch (ServletException e) {
            log.error("Could not initialize servlet!", e);
        }
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {
        String endpoint = request.params("endpoint");
        if ("clanfacts".equals(endpoint)) {
            try {
                clanFactsDataSource.service(request.raw(), response.raw());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "";
        }
        response.type("text/html");
        return  "<html><head><title>API</title></head><body><h2>Destiny Report APIs</h2>" +
                    "<a href=\"/api/clanfacts?tqx=out:html\">Clan Facts Table</a><br>" +
                        "</body></html>";
    }

}
