package my.destiny.service.bungie.type;

public enum ClanMemberType {

    None(0),
    Beginner(1),
    Member(2),
    Admin(3),
    ActingFounder(4),
    Founder(5);

    private int id;

    private ClanMemberType(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public static ClanMemberType valueOfId(int id) {
        for (ClanMemberType type : values()) {
            if(id == type.getId()) {
                return type;
            }
        }
        return null;
    }
}
