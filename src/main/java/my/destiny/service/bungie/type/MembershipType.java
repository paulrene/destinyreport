package my.destiny.service.bungie.type;


public enum MembershipType {

    PSN(2);

    private int id;

    private MembershipType(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

     public static MembershipType valueOfId(int id) {
        for (MembershipType type : values()) {
            if(id == type.getId()) {
                return type;
            }
        }
        return null;
    }

}
