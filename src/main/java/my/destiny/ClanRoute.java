package my.destiny;

import com.google.common.base.Strings;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import my.destiny.service.BungieService;
import my.destiny.service.DatabaseService;
import my.destiny.service.TwitterService;
import my.destiny.service.bungie.DestinyClan;
import my.destiny.service.bungie.DestinyUser;
import my.destiny.service.bungie.WeeklyRewardState;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.template.velocity.VelocityTemplateEngine;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.TwitterException;

public class ClanRoute implements Route {

    private VelocityTemplateEngine engine;
    private TwitterService twitter;
    private BungieService bungie;
    private DatabaseService database;

    public ClanRoute(VelocityTemplateEngine engine, TwitterService twitter, BungieService bungie, DatabaseService database) {
        this.engine = engine;
        this.twitter = twitter;
        this.bungie = bungie;
        this.database = database;
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {
        String clanId = request.params("clanId");
        if (Strings.isNullOrEmpty(clanId)) {
            response.status(400);
            return "Missing required Clan ID";
        }

        List<Status> tweetList = prepareTweets();

        DestinyClan clan = bungie.getClan(clanId);
        WeeklyRewardState wrs = bungie.getClanWeeklyRewardState(clanId);
        List<DestinyUser> members = bungie.getClanMembers(clanId);

        Collections.sort(members, new Comparator<DestinyUser>() {
            @Override
            public int compare(DestinyUser o1, DestinyUser o2) {
                return o1.getDisplayName().compareToIgnoreCase(o2.getDisplayName());
            }
        });


        Map<String, Object> model = new HashMap<>();
        model.put("u", VelocityUtils.class);
        model.put("tweetList", tweetList);
        model.put("clanId", clanId);
        model.put("clan", clan);
        model.put("wrs", wrs);
        model.put("members", members);

        database.recordClanVisit(clan);
        return engine.render(new ModelAndView(model, "/velocity/clan.vm"));
    }

    private List<Status> prepareTweets() throws TwitterException {
        ResponseList<Status> bungieHelpStatusList = twitter.getUserTimeline("BungieHelp");
        ResponseList<Status> bungieStatusList = twitter.getUserTimeline("Bungie");
        ResponseList<Status> lukeStatusList = twitter.getUserTimeline("thislukesmith");

        List<Status> tweetList = new ArrayList<Status>();
        tweetList.addAll(bungieHelpStatusList);
        tweetList.addAll(bungieStatusList);
        tweetList.addAll(lukeStatusList);
        Collections.sort(tweetList, new Comparator<Status>() {
            @Override
            public int compare(Status s1, Status s2) {
                return s2.getCreatedAt().compareTo(s1.getCreatedAt());
            }
        });
        tweetList.removeIf(new Predicate<Status>() {
            @Override
            public boolean test(Status s) {
                long cutOff = ZonedDateTime.now().minusDays(2).toEpochSecond() * 1000;
                return s.isRetweet() || s.getCreatedAt().getTime() < cutOff;
            }
        });
        return tweetList;
    }

}
