package my.destiny.service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import my.destiny.db.Storage;
import my.destiny.db.model.Clan;
import my.destiny.db.model.ClanFact;
import my.destiny.service.bungie.DestinyClan;
import my.destiny.service.bungie.StatValue;
import my.destiny.service.bungie.type.ModeType;
import my.destiny.service.bungie.type.StatType;

public class DatabaseService {

    private Storage storage;

    public DatabaseService(Storage storage) {
        this.storage = storage;
    }

    public void recordClanVisit(DestinyClan visitedClan) {
        TypedQuery<Clan> clanQuery = (TypedQuery<Clan>) storage.createQuery("from Clan where clanId=:clanId", Clan.class);
        clanQuery.setParameter("clanId", visitedClan.getClanId());
        Date now = new Date();
        Clan clan = null;
        try {
            clan = clanQuery.getSingleResult();
            clan.setUpdated(now);
            clan.setLastVisit(now);
        } catch (NoResultException e) {
            clan = new Clan();
            clan.setClanId(visitedClan.getClanId());
            clan.setCreated(now);
            clan.setLastVisit(now);
            clan.setUpdated(now);
        }
        storage.begin();
        storage.persist(clan);
        storage.commit();
        storage.clear();
    }

    public List<Clan> getAllClans() {
        TypedQuery<Clan> clanQuery = (TypedQuery<Clan>) storage.createQuery("from Clan", Clan.class);
        List<Clan> list = clanQuery.getResultList();
        storage.clear();
        return list;
    }

    public void addStatValueAsClanFact(Clan clan, StatValue stat) {
        ClanFact fact = new ClanFact();
        fact.setClanId(clan.getClanId());
        fact.setCreated(new Date());
        fact.setDisplayValue(stat.getDisplayValue());
        fact.setModeId(stat.getModeType().getId());
        fact.setStatId(stat.getStatType().getId());
        fact.setValue(stat.getValue());
        storage.begin();
        storage.persist(fact);
        storage.commit();
        storage.clear();
    }

    @SuppressWarnings("unchecked")
    public List<Object[]> getModeClanFacts(long clanId, ModeType mode, List<StatType> statTypes) {
        StringBuilder sql = new StringBuilder();
        sql.append("select created, ");
        StringBuilder listOfTypes = new StringBuilder();
        for (int n=0;n<statTypes.size();n++) {
            StatType stat = statTypes.get(n);
            sql.append("round(avg(case when statId='" + stat.getId() + "' then displayValue else 0 end)) as " + stat.getId().substring(2));
            listOfTypes.append("'" + stat.getId() + "'");
            if ((n+1) < statTypes.size()) {
                sql.append(",");
                listOfTypes.append(",");
            }
            sql.append(" ");
        }
        sql.append("from ClanFact where statId in (" + listOfTypes.toString() + ") ");
        sql.append("and modeId=" + mode.getId() + " and clanId=:clanId and created >= :cutOffDate ");
        sql.append("group by year(created), month(created), dayofmonth(created), hour(created) order by created desc");

        Calendar now = Calendar.getInstance();
        now.add(Calendar.DAY_OF_MONTH, -1);
        Date cutOffDate = now.getTime();

        Query q = storage.createQuery(sql.toString());
        q.setParameter("clanId", clanId);
        q.setParameter("cutOffDate", cutOffDate);
        q.setMaxResults(100);
        return q.getResultList();
    }

}
