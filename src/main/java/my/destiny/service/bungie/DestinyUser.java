package my.destiny.service.bungie;

import java.time.ZonedDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import my.destiny.service.bungie.type.ClanMemberType;
import my.destiny.service.bungie.type.MembershipType;

@Getter
@Setter
public class DestinyUser {

    private Long membershipId;
    private MembershipType membershipType;
    private String displayName;
    private ZonedDateTime lastPlayed;
    private List<String> characterIdList;
    private boolean online;
    private ZonedDateTime joinDate;
    private ClanMemberType clanMemberType;
    private String iconPath;

}
