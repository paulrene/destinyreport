package my.destiny.api;

import com.google.visualization.datasource.Capabilities;
import com.google.visualization.datasource.DataSourceServlet;
import com.google.visualization.datasource.base.DataSourceException;
import com.google.visualization.datasource.datatable.ColumnDescription;
import com.google.visualization.datasource.datatable.DataTable;
import com.google.visualization.datasource.datatable.TableRow;
import com.google.visualization.datasource.datatable.value.DateTimeValue;
import com.google.visualization.datasource.datatable.value.ValueType;
import com.google.visualization.datasource.query.Query;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import my.destiny.service.DatabaseService;
import my.destiny.service.bungie.type.ModeType;
import my.destiny.service.bungie.type.StatType;

public class ClanFactsDataSource extends DataSourceServlet {
    private static final long serialVersionUID = -5140171544129689221L;

    private DatabaseService database;

    public ClanFactsDataSource(DatabaseService database) {
        this.database = database;
    }

    @Override
    public DataTable generateDataTable(Query query, HttpServletRequest request) throws DataSourceException {
        ModeType mode = ModeType.AllPvE;
        List<StatType> statList = Arrays.asList(StatType.Assists, StatType.Kills, StatType.PrecisionKills, StatType.Deaths);

        DataTable table  = new DataTable();
        ArrayList<ColumnDescription> cd = new ArrayList<>();
        cd.add(new ColumnDescription("date", ValueType.DATETIME, "Date"));
        for (int n=0;n<statList.size();n++) {
            cd.add(new ColumnDescription("value" + n, ValueType.NUMBER, statList.get(n).getId().substring(2)));
        }
        table.addColumns(cd);

        long clanId = getClanIdFromRequest(request);
        List<Object[]> list = database.getModeClanFacts(clanId, mode, statList);
        for (Object[] fact : list) {
            TableRow row = new TableRow();
            ZonedDateTime dt = ZonedDateTime.ofInstant(((Date)fact[0]).toInstant(), ZoneId.of("UTC"));
            row.addCell(new DateTimeValue(dt.getYear(), dt.getMonthValue() - 1, dt.getDayOfMonth(), dt.getHour(), dt.getMinute(), 0, 0));
            for (int n=0;n<statList.size();n++) {
                row.addCell((Double) fact[n + 1]);
            }
            table.addRow(row);
        }
        return table;
    }

    private long getClanIdFromRequest(HttpServletRequest request) {
        try {
            return Long.parseLong(request.getParameter("clanId"));
        } catch (NumberFormatException e) {
            return 926258; // 4th
        }
    }

    @Override
    protected boolean isRestrictedAccessMode() {
        return false;
    }

    @Override
    public Capabilities getCapabilities() {
        return Capabilities.NONE;
    }

}
