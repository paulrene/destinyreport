package my.destiny;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import my.destiny.service.bungie.DestinyUser;


public class VelocityUtils {

    public static final Map<String, Long> times = new LinkedHashMap<>();

    static {
        times.put("year", TimeUnit.DAYS.toMillis(365));
        times.put("month", TimeUnit.DAYS.toMillis(30));
        times.put("week", TimeUnit.DAYS.toMillis(7));
        times.put("day", TimeUnit.DAYS.toMillis(1));
        times.put("hour", TimeUnit.HOURS.toMillis(1));
        times.put("minute", TimeUnit.MINUTES.toMillis(1));
        times.put("second", TimeUnit.SECONDS.toMillis(1));
    }

    public static String toRelative(long duration, int maxLevel, boolean ago) {
        StringBuilder res = new StringBuilder();
        int level = 0;
        for (Map.Entry<String, Long> time : times.entrySet()){
            long timeDelta = duration / time.getValue();
            if (timeDelta > 0){
                res.append(timeDelta)
                        .append(" ")
                        .append(time.getKey())
                        .append(timeDelta > 1 ? "s" : "")
                        .append(", ");
                duration -= time.getValue() * timeDelta;
                level++;
            }
            if (level == maxLevel){
                break;
            }
        }
        if ("".equals(res.toString())) {
            if (ago) {
                return "0 seconds ago";
            } else {
                return "right now";
            }
        } else {
            res.setLength(res.length() - 2);
            if (ago) {
                res.append(" ago");
            }
            return res.toString();
        }
    }

    public static String toRelative(ZonedDateTime start, int level, boolean ago) {
        return toRelative(new Date(start.toInstant().toEpochMilli()), level, ago);
    }

    public static String toRelative(ZonedDateTime start) {
        return toRelative(new Date(start.toInstant().toEpochMilli()));
    }

    public static String toRelative(Date start) {
        return toRelative(start, new Date());
    }

    public static String toRelative(Date start, int level, boolean ago) {
        return toRelative(start, new Date(), level, ago);
    }

    public static String toRelative(long duration) {
        return toRelative(duration, times.size(), true);
    }

    public static String toRelative(Date start, Date end) {
        assert start.after(end);
        return toRelative(end.getTime() - start.getTime());
    }

    public static String toRelative(Date start, Date end, int level, boolean ago) {
        assert start.after(end);
        return toRelative(end.getTime() - start.getTime(), level, ago);
    }

    public static String toPercent(int v1, int v2) {
        return String.valueOf((v1 * 100) / v2);
    }

    public static boolean isNew(ZonedDateTime dateTime) {
        return dateTime.isAfter(ZonedDateTime.now().minusMonths(1));
    }

    public static int getOnlineCount(List<DestinyUser> members) {
        int count = 0;
        for (DestinyUser destinyUser : members) {
            if (destinyUser.isOnline()) count++;
        }
        return count;
    }

}