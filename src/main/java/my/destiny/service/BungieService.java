package my.destiny.service;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import my.destiny.service.bungie.ApiException;
import my.destiny.service.bungie.DestinyClan;
import my.destiny.service.bungie.DestinyUser;
import my.destiny.service.bungie.StatValue;
import my.destiny.service.bungie.WeeklyRewardState;
import my.destiny.service.bungie.type.ClanMemberType;
import my.destiny.service.bungie.type.ComponentType;
import my.destiny.service.bungie.type.MembershipType;
import my.destiny.service.bungie.type.ModeType;
import my.destiny.service.bungie.type.StatType;
import org.json.JSONArray;
import org.json.JSONObject;

public class BungieService {

    private static final String API_BASE = "https://www.bungie.net/Platform";

    private String apiKey;

    public BungieService(String apiKey) {
        this.apiKey = apiKey;
    }

    public static void main(String[] args) throws UnirestException, ApiException {

        //BungieService bs = new BungieService();

        /*List<StatValue> list = bs.getAggregateClanStats("926258");
        for (StatValue s : list) {
            System.out.println(s.getModeType() + " " + s.getStatType() + " " + s.getDisplayValue());
        }*/

        //DestinyUser user = bs.getUser(MembershipType.PSN, "paulrenej");
        // JSONObject profile = bs.getProfile(user.getMembershipType(), user.getMembershipId(), ComponentType.Profiles);
        //JSONObject character = bs.getCharacterById(user, user.getCharacterIdList().get(0), ComponentType.Characters);
        //System.out.println(character.toString(2));

        // Clan clan = bs.getClan("926258");
        //WeeklyRewardState wrs = bs.getClanWeeklyRewardState("926258");

        // List<DestinyUser> members = bs.getClanMembers("926258");

        // System.out.println(members);

        // JSONObject clanLeaderboards = getClanLeaderboards("926258", 10, ActivityModeType.AllPvE);

        // https://www.bungie.net/Platform/GroupV2/help/
    }

    public List<StatValue> getAggregateClanStats(long clanId, ModeType... modes) throws ApiException {
        StringBuilder url = new StringBuilder(API_BASE);
        url.append("/Destiny2/Stats/AggregateClanStats/");
        url.append(clanId);
        url.append("/");
        if (modes != null && modes.length > 0) {
            url.append("?modes=");
            for (int n=0;n<modes.length;n++) {
                url.append(String.valueOf(modes[n].getId()));
                if ((n+1) < modes.length) {
                    url.append(",");
                }
            }
        }
        HttpResponse<JsonNode> req;
        try {
            req = Unirest.get(url.toString()).header("X-API-Key", apiKey).asJson();
        } catch (UnirestException e) {
            throw new ApiException("Could not talk with bungie.net!", e);
        }
        JSONArray responseArray = getResponseArray(req);
        List<StatValue> list = new ArrayList<>();
        for(int n=0;n<responseArray.length();n++) {
            list.add(createStatValue(responseArray.getJSONObject(n)));
        }
        return list;
    }

    private StatValue createStatValue(JSONObject statObj) {
        StatValue value = new StatValue();
        value.setModeType(ModeType.valueOfId(statObj.getInt("mode")));
        value.setStatType(StatType.valueOfId(statObj.getString("statId")));
        JSONObject valueObj = statObj.getJSONObject("value");
        JSONObject basicObj = valueObj.getJSONObject("basic");
        value.setDisplayValue(basicObj.getString("displayValue"));
        value.setValue(basicObj.getLong("value"));
        return value;
    }

    public WeeklyRewardState getClanWeeklyRewardState(String clanId) throws ApiException {
        StringBuilder url = new StringBuilder(API_BASE);
        url.append("/Destiny2/Clan/");
        url.append(clanId);
        url.append("/WeeklyRewardState/");
        HttpResponse<JsonNode> req;
        try {
            req = Unirest.get(url.toString()).header("X-API-Key", apiKey).asJson();
        } catch (UnirestException e) {
            throw new ApiException("Could not talk with bungie.net!", e);
        }
        JSONObject response = getFirstResponse(req);

        WeeklyRewardState wrs = new WeeklyRewardState();
        JSONArray rewardsArray = response.getJSONArray("rewards");
        for(int n=0;n<rewardsArray.length();n++) {
            JSONObject entriesObj = rewardsArray.getJSONObject(n);
            if (entriesObj.getLong("rewardCategoryHash") == 1064137897L) {
                JSONArray entryArray = entriesObj.getJSONArray("entries");
                for (int t=0;t<entryArray.length();t++) {
                    JSONObject entry = entryArray.getJSONObject(t);
                    long rewardEntryHash = entry.getLong("rewardEntryHash");
                    if (rewardEntryHash == 2043403989L) { // VERIFIED!
                        wrs.setRaidEarned(entry.getBoolean("earned"));
                    } else if (rewardEntryHash == 964120289L) { // VERIFIED!
                        wrs.setCrucibleEarned(entry.getBoolean("earned"));
                    } else if (rewardEntryHash == 2112637710L) {
                        wrs.setTrialsEarned(entry.getBoolean("earned"));
                    } else if (rewardEntryHash == 3789021730L) {
                        wrs.setNightfallEarned(entry.getBoolean("earned"));
                    }
                }
            }
        }
        return wrs;
    }

    public DestinyClan getClan(String clanId) throws ApiException  {
        StringBuilder url = new StringBuilder(API_BASE);
        url.append("/GroupV2/");
        url.append(clanId);
        url.append("/");
        HttpResponse<JsonNode> req;
        try {
            req = Unirest.get(url.toString()).header("X-API-Key", apiKey).asJson();
        } catch (UnirestException e) {
            throw new ApiException("Could not talk with bungie.net!", e);
        }
        JSONObject response = getFirstResponse(req);

        DestinyClan clan = new DestinyClan();
        JSONObject founderObj = response.getJSONObject("founder");
        DestinyUser founderUser = createUserFromDestinyUserInfo(founderObj.getJSONObject("destinyUserInfo"));
        clan.setFounderUser(founderUser);
        founderUser.setOnline(founderObj.optBoolean("isOnline"));

        JSONObject detailObj = response.getJSONObject("detail");
        clan.setClanId(detailObj.getLong("groupId"));
        clan.setAbout(detailObj.getString("about").replace("\n", "<br>"));
        clan.setMotto(detailObj.getString("motto"));
        clan.setName(detailObj.getString("name"));
        clan.setMemberCount(detailObj.getInt("memberCount"));

        JSONObject clanInfoObj = detailObj.getJSONObject("clanInfo");
        clan.setCallsign(clanInfoObj.getString("clanCallsign"));
        JSONObject progressionsObj = clanInfoObj.getJSONObject("d2ClanProgressions");
        JSONObject clanXpObj = progressionsObj.getJSONObject("584850370"); // Clan XP hash
        clan.setClanXpWeeklyProgress(clanXpObj.getInt("weeklyProgress"));
        clan.setClanXpWeeklyLimit(clanXpObj.getInt("weeklyLimit"));
        clan.setClanXpLevel(clanXpObj.getInt("level"));
        clan.setClanXpLevelCap(clanXpObj.getInt("levelCap"));
        clan.setNextLevelAt(clanXpObj.getInt("nextLevelAt"));
        clan.setProgressToNextLevel(clanXpObj.getInt("progressToNextLevel"));

        JSONObject featuresObj = detailObj.getJSONObject("features");
        clan.setMaxMemberCount(featuresObj.getInt("maximumMembers"));

        clan.setCreation(ZonedDateTime.parse(detailObj.getString("creationDate")));
        clan.setModification(ZonedDateTime.parse(detailObj.getString("modificationDate")));
        clan.setBanExpire(ZonedDateTime.parse(detailObj.getString("banExpireDate")));

        return clan;
    }

    private DestinyUser createUserFromDestinyUserInfo(JSONObject obj) {
        DestinyUser user = new DestinyUser();
        user.setDisplayName(obj.getString("displayName"));
        user.setMembershipId(obj.getLong("membershipId"));
        user.setMembershipType(MembershipType.valueOfId(obj.getInt("membershipType")));
        return user;
    }

    public List<DestinyUser> getClanMembers(String clanId) throws UnirestException, ApiException {
        StringBuilder url = new StringBuilder(API_BASE);
        url.append("/GroupV2/");
        url.append(clanId);
        url.append("/Members/?currentPage=1");
        HttpResponse<JsonNode> req = Unirest.get(url.toString()).header("X-API-Key", apiKey).asJson();

        JSONObject response = getFirstResponse(req);
        JSONArray results = response.getJSONArray("results");
        List<DestinyUser> list = new ArrayList<>();

        for (int n=0;n<results.length();n++) {
            JSONObject result = results.getJSONObject(n);
            DestinyUser user = createUserFromDestinyUserInfo(result.getJSONObject("destinyUserInfo"));
            user.setJoinDate(ZonedDateTime.parse(result.getString("joinDate")));
            user.setOnline(result.getBoolean("isOnline"));
            user.setClanMemberType(ClanMemberType.valueOfId(result.getInt("memberType")));
            JSONObject bungieUserInfo = result.optJSONObject("bungieNetUserInfo");
            if (bungieUserInfo != null) {
                user.setIconPath(bungieUserInfo.getString("iconPath"));
            }
            list.add(user);
        }
        return list;
    }

    private DestinyClan getClanForUser(DestinyUser user) throws UnirestException, ApiException {
        // /GroupV2/User/2/4611686018448709371/0/1/
        StringBuilder url = new StringBuilder(API_BASE);
        url.append("/GroupV2/User/");
        url.append(user.getMembershipType().getId());
        url.append("/");
        url.append(user.getMembershipId());
        url.append("/0/1");
        HttpResponse<JsonNode> req = Unirest.get(url.toString()).header("X-API-Key", apiKey).asJson();
        JSONObject response = getFirstResponse(req);
        System.out.println(response.toString(2));



        return null;
    }

    public JSONObject getClanLeaderboards(String groupId, int limit, ModeType... modes) throws UnirestException, ApiException {
        // /Destiny2/Stats/Leaderboards/Clans/{groupId}/
        StringBuilder url = new StringBuilder(API_BASE);
        url.append("/Destiny2/Stats/Leaderboards/Clans/");
        url.append(groupId);
        url.append("/?maxtop=");
        url.append(String.valueOf(limit));
        if (modes != null && modes.length > 0) {
            url.append("&modes=");
            for (int n=0;n<modes.length;n++) {
                url.append(String.valueOf(modes[n].getId()));
                if ((n+1) < modes.length) {
                    url.append(",");
                }
            }
        }

        HttpResponse<JsonNode> req = Unirest.get(url.toString()).header("X-API-Key", apiKey).asJson();
        JSONObject response = getFirstResponse(req);

        System.out.println(response.toString(2));

        return null;
    }

    public JSONObject getCharacterById(DestinyUser user, String characterId, ComponentType... types) throws UnirestException, ApiException {
        StringBuilder url = new StringBuilder(API_BASE);
        url.append("/Destiny2/");
        url.append(user.getMembershipType().getId());
        url.append("/Profile/");
        url.append(user.getMembershipId());
        url.append("/Character/");
        url.append(characterId);
        url.append("/");
        if (types != null && types.length > 0) {
            url.append("?components=");
            for (int n=0;n<types.length;n++) {
                url.append(String.valueOf(types[n].getId()));
                if ((n+1) < types.length) {
                    url.append(",");
                }
            }
        }
        HttpResponse<JsonNode> req = Unirest.get(url.toString()).header("X-API-Key", apiKey).asJson();
        JSONObject response = getFirstResponse(req);
        return response.getJSONObject("character");
    }

    private String getMembershipIdByTypeAndDisplayName(MembershipType membershipType, String displayName) throws UnirestException, ApiException {
        StringBuilder url = new StringBuilder(API_BASE);
        url.append("/Destiny2/SearchDestinyPlayer/");
        url.append(membershipType.getId());
        url.append("/");
        url.append(displayName);
        url.append("/");
        HttpResponse<JsonNode> req = Unirest.get(url.toString()).header("X-API-Key", apiKey).asJson();
        JSONObject response = getFirstResponse(req);
        String membershipId = response.getString("membershipId");
        return membershipId;
    }

    public JSONObject getProfile(MembershipType membershipType, String membershipId, ComponentType... types) throws UnirestException, ApiException {
        StringBuilder url = new StringBuilder(API_BASE);
        url.append("/Destiny2/");
        url.append(membershipType.getId());
        url.append("/Profile/");
        url.append(membershipId);
        url.append("/");
        if (types != null && types.length > 0) {
            url.append("?components=");
            for (int n=0;n<types.length;n++) {
                url.append(String.valueOf(types[n].getId()));
                if ((n+1) < types.length) {
                    url.append(",");
                }
            }
        }
        HttpResponse<JsonNode> req = Unirest.get(url.toString()).header("X-API-Key", apiKey).asJson();
        JSONObject response = getFirstResponse(req);
        return response.getJSONObject("profile");
    }

    public DestinyUser getUser(MembershipType type, String displayName) throws UnirestException, ApiException {
        String membershipId = getMembershipIdByTypeAndDisplayName(MembershipType.PSN, "paulrenej");
        JSONObject profile = getProfile(MembershipType.PSN, membershipId, ComponentType.Profiles);
        JSONObject data = profile.getJSONObject("data");
        DestinyUser user = createUserFromDestinyUserInfo(data.getJSONObject("userInfo"));
        user.setLastPlayed(ZonedDateTime.parse(data.getString("dateLastPlayed")));
        List<String> list = new ArrayList<>();
        JSONArray characterIds = data.getJSONArray("characterIds");
        for (int n=0;n<characterIds.length();n++) {
            list.add(characterIds.getString(n));
        }
        user.setCharacterIdList(list);
        return user;
    }

    private static JSONObject getFirstResponse(HttpResponse<JsonNode> resp) throws ApiException {
        JsonNode json = resp.getBody();
        if (json == null) {
            throw new ApiException("No JSON in response from server!");
        }
        JSONObject obj = json.getObject();
        // System.out.println(obj.toString(2));
        int errorCode = obj.optInt("ErrorCode", 0);
        if (errorCode != 1) {
            throw new ApiException(obj.optString("ErrorStatus") + " - " + obj.optString("Message"));
        }
        JSONObject response = null;
        JSONArray responses = obj.optJSONArray("Response");
        if (responses == null || responses.length() == 0) {
            response = obj.optJSONObject("Response");
        } else {
            response = responses.getJSONObject(0);
        }
        if (response == null) {
            throw new ApiException("JSON did not contain any response!");
        }
        return response;
    }

    private static JSONArray getResponseArray(HttpResponse<JsonNode> resp) throws ApiException {
        JsonNode json = resp.getBody();
        if (json == null) {
            throw new ApiException("No JSON in response from server!");
        }
        JSONObject obj = json.getObject();
        // System.out.println(obj.toString(2));
        int errorCode = obj.optInt("ErrorCode", 0);
        if (errorCode != 1) {
            throw new ApiException(obj.optString("ErrorStatus") + " - " + obj.optString("Message"));
        }
        return obj.optJSONArray("Response");
    }

}
