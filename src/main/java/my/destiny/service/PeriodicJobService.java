package my.destiny.service;

import java.util.ArrayList;
import java.util.List;
import my.destiny.db.model.Clan;
import my.destiny.service.bungie.ApiException;
import my.destiny.service.bungie.StatValue;
import my.destiny.service.bungie.type.ModeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class PeriodicJobService implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(PeriodicJobService.class);

    private DatabaseService database;
    private BungieService bungie;

    public PeriodicJobService(DatabaseService database, BungieService bungie) {
        this.database = database;
        this.bungie = bungie;
    }

    @Override
    public void run() {
        updateAggregatedClanStats();
    }

    private void updateAggregatedClanStats() {
        List<Clan> clanList = database.getAllClans();
        for (Clan clan : clanList) {
            updateAggregatedClanStats(clan);
        }
    }

    private void updateAggregatedClanStats(Clan clan) {
        List<StatValue> statList = null;
        try {
            statList = bungie.getAggregateClanStats(clan.getClanId(), getModesToUse());
        } catch (ApiException e) {
            log.warn("Error while fetching new aggregated clan stats for clan " + clan.getClanId(), e);
        }
        if (statList == null) {
            return;
        }
        for (StatValue statValue : statList) {
            database.addStatValueAsClanFact(clan, statValue);
        }
        log.info("Added " + statList.size() + " new aggregated clan facts for " + clan.getClanId());
    }

    private ModeType[] getModesToUse() {
        List<ModeType> modeList = new ArrayList<>();
        for (ModeType mode : ModeType.values()) {
            if (mode == ModeType.IronBanner) continue;
            modeList.add(mode);
        }
        return modeList.toArray(new ModeType[modeList.size()]);
    }

}
