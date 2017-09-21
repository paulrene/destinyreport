package my.destiny.service;

import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.OAuth2Token;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterService {

    private Twitter twitter;

    public TwitterService() throws TwitterException {
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true);
        cb.setApplicationOnlyAuthEnabled(true);
        cb.setOAuthConsumerKey("m56hqW9nhy9WfxfVnTIIT7v5G");
        cb.setOAuthConsumerSecret("1rvkWynBcR7CrXIdUItdCoc7EVCfw8v97mAYDoZwzjGcOWAb6g");
        TwitterFactory tf = new TwitterFactory(cb.build());
        OAuth2Token token = tf.getInstance().getOAuth2Token();
        twitter = tf.getInstance();
        twitter.setOAuth2Token(token);
    }

    public ResponseList<Status> getUserTimeline(String screenName) throws TwitterException {
        return twitter.getUserTimeline(screenName);
    }

}
