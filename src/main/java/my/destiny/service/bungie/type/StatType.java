package my.destiny.service.bungie.type;


public enum StatType {

    SingleGameKills("lbSingleGameKills"),
    PrecisionKills("lbPrecisionKills"),
    Assists("lbAssists"),
    Deaths("lbDeaths"),
    Kills("lbKills"),
    ObjectivesCompleted("lbObjectivesCompleted"),
    MostPrecisionKills("lbMostPrecisionKills"),
    LongestKillSpree("lbLongestKillSpree"),
    LongestKillDistance("lbLongestKillDistance"),
    FastestCompletionMs("lbFastestCompletionMs"),
    LongestSingleLife("lbLongestSingleLife"),
    SingleGameScore("lbSingleGameScore");

    private String id;

    private StatType(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    public static StatType valueOfId(String id) {
        for (StatType type : values()) {
            if(id.equals(type.getId())) {
                return type;
            }
        }
        return null;
    }

}
