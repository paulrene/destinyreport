package my.destiny.service.bungie;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WeeklyRewardState {

    private boolean crucibleEarned;
    private boolean trialsEarned;
    private boolean raidEarned;
    private boolean nightfallEarned;

}
