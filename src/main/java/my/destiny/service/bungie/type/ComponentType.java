package my.destiny.service.bungie.type;

/**
 * @author paulrene
 *
 * {@link link https://bungie-net.github.io/multi/schema_Destiny-DestinyComponentType.html}
 *
 */
public enum ComponentType {

    None(0),
    Profiles(100), VendorReceipts(101), ProfileInventories(102), ProfileCurrencies(103),
    Characters(200), CharacterInventories(201), CharacterProgressions(202), CharacterRenderData(203), CharacterActivities(204), CharacterEquipment(205),
    ItemInstances(300), ItemObjectives(301), ItemPerks(302), ItemRenderData(303), ItemStats(304), ItemSockets(305), ItemTalentGrids(306), ItemCommonData(307), ItemPlugStates(308),
    Vendors(400), VendorCategories(401), VendorSales(402),
    Kiosks(500);

    private int id;

    private ComponentType(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

}
