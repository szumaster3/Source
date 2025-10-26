import content.global.skill.construction.Decoration;
import content.global.skill.construction.HouseManager;
import core.game.node.scenery.Scenery;

/**
 * Adds the collision/blocking flags for a scenery object in this room.
 */
public void addObjectClipping(Scenery object, HouseManager house) {
    Decoration deco = Decoration.getDecoration(house.getOwner(), object);
    if (deco == null) return;

    int mask = deco.getBlockMask();
    if (mask == 0) return;

    int z = house.getRoomPlane(this);
    int x = object.getLocation().getChunkOffsetX();
    int y = object.getLocation().getChunkOffsetY();

    if (house.isInDungeon(house.getOwner())) {
        house.getDungeonFlags().addFlag(z, x, y, mask);
    } else {
        house.getHouseFlags().addFlag(z, x, y, mask);
    }
    house.getDynamicFlags().addFlag(z, x, y, mask);
}
