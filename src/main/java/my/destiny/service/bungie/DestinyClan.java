package my.destiny.service.bungie;

import java.time.ZonedDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DestinyClan {

    private Long clanId;
    private String name;
    private String callsign;
    private String motto;
    private String about;
    private DestinyUser founderUser;
    private int memberCount;
    private int maxMemberCount;
    private ZonedDateTime creation;
    private ZonedDateTime modification;
    private ZonedDateTime banExpire;

    private int clanXpWeeklyProgress;
    private int clanXpWeeklyLimit;
    private int clanXpLevel;
    private int clanXpLevelCap;
    private int nextLevelAt;
    private int progressToNextLevel;
}
