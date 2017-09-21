package my.destiny.db.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "userfact")
public class UserFact {

    private @Id @GeneratedValue Long id;
    private int membershipType;
    private long membershipId;

    public UserFact() {
    }

}
