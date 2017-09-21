package my.destiny.service.bungie.type;

public enum ModeType {

    None(0),
    Story(2),
    Strike(3),
    Raid(4),
    AllPvP(5),
    Patrol(6),
    AllPvE(7),
    Control(10),
    Clash(12),
    Nightfall(16),
    HeroicNightfall(17),
    AllStrikes(18),
    IronBanner(19),
    Supremacy(31),
    Survival(37),
    Countdown(38),
    TrialsOfTheNine(39),
    Social(40);

    private int id;

    private ModeType(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public static ModeType valueOfId(int id) {
        for (ModeType type : values()) {
            if(id == type.getId()) {
                return type;
            }
        }
        return null;
    }

}
