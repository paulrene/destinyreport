package my.destiny.db.model;

import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "clanfact")
public class ClanFact {

    private @Id @GeneratedValue Long id;
    private Date created;
    private long clanId;
    private int modeId;
    private String statId;
    private double value;
    private String displayValue;

    public ClanFact() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getCreated() {
        return this.created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public long getClanId() {
        return this.clanId;
    }

    public void setClanId(long clanId) {
        this.clanId = clanId;
    }

    public int getModeId() {
        return this.modeId;
    }

    public void setModeId(int modeId) {
        this.modeId = modeId;
    }

    public String getStatId() {
        return this.statId;
    }

    public void setStatId(String statId) {
        this.statId = statId;
    }

    public double getValue() {
        return this.value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getDisplayValue() {
        return this.displayValue;
    }

    public void setDisplayValue(String displayValue) {
        this.displayValue = displayValue;
    }

}
