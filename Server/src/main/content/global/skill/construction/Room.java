package content.global.skill.construction;

import core.cache.def.impl.SceneryDefinition;
import core.game.node.entity.player.Player;
import core.game.node.scenery.Constructed;
import core.game.node.scenery.Scenery;
import core.game.node.scenery.SceneryBuilder;
import core.game.world.map.*;
import core.game.world.map.build.RegionFlags;
import core.tools.Log;

import static core.api.ContentAPIKt.log;

/**
 * Represents a room.
 *
 * @author Emperor
 */
public final class Room {

    /**
     * The default room type.
     */
    public static final int CHAMBER = 0x0;

    /**
     * The rooftop room type.
     */
    public static final int ROOF = 0x1;

    /**
     * The dungeon room type.
     */
    public static final int DUNGEON = 0x2;

    /**
     * The dungeon room type.
     */
    public static final int LAND = 0x4;

    /**
     * The room properties.
     */
    private RoomProperties properties;

    /**
     * The region chunk.
     */
    private RegionChunk chunk;

    /**
     * The hotspots.
     */
    private Hotspot[] hotspots;

    /**
     * The current rotation of the room.
     */
    private Direction rotation = Direction.NORTH;

    /**
     * Constructs a new {@code Room} {@code Object}.
     *
     * @param properties The room properties.
     */
    public Room(RoomProperties properties) {
        this.properties = properties;
    }

    /**
     * Creates a new room.
     *
     * @param player     The player.
     * @param properties The room properties.
     * @return The room.
     */
    public static Room create(Player player, RoomProperties properties) {
        Room room = new Room(properties);
        room.configure(player.getHouseManager().getStyle());
        return room;
    }

    /**
     * Configures the room.
     */
    public void configure(HousingStyle style) {
        this.hotspots = new Hotspot[properties.getHotspots().length];
        for (int i = 0; i < hotspots.length; i++) {
            hotspots[i] = properties.getHotspots()[i].copy();
        }
        decorate(style);
    }

    /**
     * Redecorates the room.
     *
     * @param style The house style.
     */
    public void decorate(HousingStyle style) {
        Region region = RegionManager.forId(style.getRegionId());
        Region.load(region, true);
        chunk = region.getPlanes()[style.getPlane()].getRegionChunk(properties.getChunkX(), properties.getChunkY());
    }

    /**
     * Gets the hotspot object for the given hotspot type.
     *
     * @param hotspot The hotspot type.
     * @return The hotspot.
     */
    public Hotspot getHotspot(BuildHotspot hotspot) {
        for (Hotspot h : hotspots) {
            if (h.getHotspot() == hotspot) {
                return h;
            }
        }
        return null;
    }

    /**
     * Checks if the building hotspot has been built.
     *
     * @param hotspot The building hotspot.
     * @return {@code True} if so.
     */
    public boolean isBuilt(BuildHotspot hotspot) {
        Hotspot h = getHotspot(hotspot);
        return h != null && h.getDecorationIndex() > -1;
    }

    /**
     * Loads all the decorations.
     *
     * @param housePlane The plane.
     * @param chunk      The chunk used in the dynamic region.
     */
    public void loadDecorations(int housePlane, BuildRegionChunk chunk, HouseManager house) {
        DynamicRegionFlags flags = house.getDynamicFlags();
        for (int i = 0; i < hotspots.length; i++) {
            Hotspot spot = hotspots[i];
            int x = spot.getChunkX();
            int y = spot.getChunkY();

            if (spot.getHotspot() == null) {
                continue;
            }

            int index = chunk.getIndex(x, y, spot.getHotspot().getObjectId(house.getStyle()));
            Scenery[][] objects = chunk.getObjects(index);
            Scenery object = objects[x][y];

            if (object != null && object.getId() == spot.getHotspot().getObjectId(house.getStyle())) {
                if (spot.getDecorationIndex() > -1 && spot.getDecorationIndex() < spot.getHotspot().getDecorations().length) {
                    int id = spot.getHotspot().getDecorations()[spot.getDecorationIndex()].getObjectId(house.getStyle());
                    if (spot.getHotspot().getType() == BuildHotspotType.CREST) {
                        id += house.getCrest().ordinal();
                    }

                    Scenery newObj = object.transform(id, object.getRotation(), chunk.getCurrentBase().transform(x, y, 0));
                    SceneryBuilder.replace(object, newObj);

                    SceneryDefinition def = SceneryDefinition.forId(id);
                    if (def != null && def.isSecondBool()) {
                        addObjectClipping(flags, newObj);
                    }

                } else if (object.getId() == BuildHotspot.WINDOW.getObjectId(house.getStyle()) || (!house.isBuildingMode() && object.getId() == BuildHotspot.CHAPEL_WINDOW.getObjectId(house.getStyle()))) {Scenery newObj = object.transform(house.getStyle().getWindow().getObjectId(house.getStyle()), object.getRotation(), object.getType());
                    chunk.add(newObj);
                    SceneryDefinition def = SceneryDefinition.forId(newObj.getId());
                    if (def != null && def.isSecondBool()) {
                        addObjectClipping(flags, newObj);
                    }
                }

                int[] pos = RegionChunk.getRotatedPosition(x, y, object.getSizeX(), object.getSizeY(), 0, rotation.toInteger());
                spot.setCurrentX(pos[0]);
                spot.setCurrentY(pos[1]);
            }
        }

        if (rotation != Direction.NORTH && chunk.getRotation() == 0) {
            chunk.rotate(rotation);
        }

        if (!house.isBuildingMode()) {
            placeDoors(housePlane, house, chunk);
            removeHotspots(housePlane, house, chunk);
        }
    }


    /**
     * Loads the room's decorations into the given chunk, setting all relevant flags automatically.
     *
     * @param housePlane The plane of the house (z-level).
     * @param chunk      The build region chunk.
     * @param house      The house manager.
     */
    public void loadDecorations(int housePlane, BuildRegionChunk chunk, HouseManager house) {
        for (int i = 0; i < hotspots.length; i++) {
            Hotspot spot = hotspots[i];
            if (spot.getHotspot() == null) continue;

            int x = spot.getChunkX();
            int y = spot.getChunkY();
            int index = chunk.getIndex(x, y, spot.getHotspot().getObjectId(house.getStyle()));
            Scenery[][] objects = chunk.getObjects(index);
            Scenery object = objects[x][y];

            if (object != null && object.getId() == spot.getHotspot().getObjectId(house.getStyle())) {

                // Apply the chosen decoration
                if (spot.getDecorationIndex() > -1 && spot.getDecorationIndex() < spot.getHotspot().getDecorations().length) {
                    Decoration deco = spot.getHotspot().getDecorations()[spot.getDecorationIndex()];
                    int decoId = deco.getObjectId(house.getStyle());
                    if (spot.getHotspot().getType() == BuildHotspotType.CREST) {
                        decoId += house.getCrest().ordinal();
                    }
                    SceneryBuilder.replace(object, object.transform(decoId, object.getRotation(), chunk.getCurrentBase().transform(x, y, 0)));

                    // Add flags for this decoration
                    house.getHouseFlags().addFlag(housePlane, x, y, deco.getBlockMask());
                    house.getDynamicFlags().addFlag(housePlane, x, y, deco.getBlockMask());
                }

                // Special handling for windows in non-building mode
                else if (object.getId() == BuildHotspot.WINDOW.getObjectId(house.getStyle()) ||
                        (!house.isBuildingMode() && object.getId() == BuildHotspot.CHAPEL_WINDOW.getObjectId(house.getStyle()))) {
                    chunk.add(object.transform(house.getStyle().getWindow().getObjectId(house.getStyle()), object.getRotation(), object.getType()));

                    // Block the window tile
                    house.getHouseFlags().addFlag(housePlane, x, y, RegionFlags.OBJECT_BLOCK);
                }

                // Update hotspot runtime position after rotation
                int[] pos = RegionChunk.getRotatedPosition(x, y, object.getSizeX(), object.getSizeY(), 0, rotation.toInteger());
                spot.setCurrentX(pos[0]);
                spot.setCurrentY(pos[1]);
            }
        }

        // Rotate the chunk if the room is rotated
        if (rotation != Direction.NORTH && chunk.getRotation() == 0) {
            chunk.rotate(rotation);
        }

        // Place doors and remove temporary hotspots only in non-building mode
        if (!house.isBuildingMode()) {
            placeDoors(housePlane, house, chunk);

            // Add door flags
            for (Hotspot door : hotspots) {
                if (door.getHotspot() != null && door.getHotspot().getType() == BuildHotspotType.DOOR) {
                    house.getHouseFlags().addFlag(housePlane, door.getCurrentX(), door.getCurrentY(), RegionFlags.DOOR_BLOCK);
                }
            }

            removeHotspots(housePlane, house, chunk);
        }
    }

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

    /**
     * Removes the building hotspots from the room.
     *
     * @param housePlane The room's plane in house.
     * @param house      The house manager.
     * @param chunk      The region chunk used.
     */
    private void removeHotspots(int housePlane, HouseManager house, BuildRegionChunk chunk) {
        if (properties.isRoof()) return;
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                for (int i = 0; i < BuildRegionChunk.ARRAY_SIZE; i++) {
                    Scenery object = chunk.get(x, y, i);
                    if (object != null) {
                        boolean isBuilt = object instanceof Constructed;
                        boolean isWall = object.getId() == 13065 || object.getId() == house.getStyle().getWallId();
                        boolean isDoor = object.getId() == house.getStyle().getDoorId() || object.getId() == house.getStyle().getSecondDoorId();
                        if (!isBuilt && !isWall && !isDoor) {
                            SceneryBuilder.remove(object);
                            chunk.remove(object);
                        }
                    }
                }
            }
        }
    }

    /**
     * Replaces the door hotspots with doors, walls, or passageways as needed.
     * TODO: it is believed that doors authentically remember their open/closed state for the usual duration (see e.g. https://www.youtube.com/watch?v=nRGux739h8s 1:00 vs 1:55), but this is not possible with the current HouseManager approach, which deallocates the instance as soon as the player leaves.
     *
     * @param housePlane The room's plane in house.
     * @param house      The house manager.
     * @param chunk      The region chunk used.
     */
    private void placeDoors(int housePlane, HouseManager house, BuildRegionChunk chunk) {
        Room[][][] rooms = house.getRooms();
        int rx = chunk.getCurrentBase().getChunkX();
        int ry = chunk.getCurrentBase().getChunkY();
        for (int i = 0; i < BuildRegionChunk.ARRAY_SIZE; i++) {
            for (int x = 0; x < 8; x++) {
                for (int y = 0; y < 8; y++) {
                    Scenery object = chunk.get(x, y, i);
                    if (object != null && BuildingUtils.isDoorHotspot(object)) {
                        boolean edge = false;
                        Room otherRoom = null;
                        switch (object.getRotation()) {
                            case 0: //east
                                edge = rx == 0;
                                otherRoom = edge ? null : rooms[housePlane][rx - 1][ry];
                                break;
                            case 1: //south
                                edge = ry == 7;
                                otherRoom = edge ? null : rooms[housePlane][rx][ry + 1];
                                break;
                            case 2: //west
                                edge = rx == 7;
                                otherRoom = edge ? null : rooms[housePlane][rx + 1][ry];
                                break;
                            case 3: //north
                                edge = ry == 0;
                                otherRoom = edge ? null : rooms[housePlane][rx][ry - 1];
                                break;
                            default:
                                log(this.getClass(), Log.ERR, "Impossible rotation when placing doors??");
                        }
                        int replaceId = getReplaceId(housePlane, house, this, edge, otherRoom, object);
                        if (replaceId == -1) {
                            continue;
                        }
                        SceneryBuilder.replace(object, object.transform(replaceId));
                    }
                }
            }
        }
    }

    /**
     * Checks if rooms transition between inside<>outside the house and returns the appropriate door replacement.
     *
     * @param housePlane The room's plane in house.
     * @param house      The house manager.
     * @param room       The room the door is in.
     * @param edge       Whether the door is adjacent to an edge.
     * @param otherRoom  The room the door is adjacent to.
     * @param object     The door object itself.
     */
    private int getReplaceId(int housePlane, HouseManager house, Room room, boolean edge, Room otherRoom, Scenery object) {
        boolean thisOutside = !room.getProperties().isChamber();
        if (edge && thisOutside) {
            // No door or wall
            return -1;
        }
        if (!edge) {
            boolean otherOutside = otherRoom == null || !otherRoom.getProperties().isChamber();
            if (thisOutside == otherOutside) {
                // Free passage, unless the other room has a blind wall here
                if (otherRoom == null) {
                    return -1;
                }
                boolean exit = otherRoom.getExits()[object.getRotation()];
                if (exit) {
                    return -1;
                }
            }
            if (thisOutside != otherOutside && housePlane == 0) {
                // Door if we are the inside room only
                if (thisOutside) {
                    return -1;
                }
                return object.getId() % 2 != 0 ? house.getStyle().getDoorId() : house.getStyle().getSecondDoorId();
            }
        }
        return room.getProperties().isDungeon() ? 13065 : house.getStyle().getWallId();
    }

    /**
     * Sets the decoration index for a group of object ids
     *
     * @param index The index.
     * @param hs    The building hotspot.
     */
    public void setAllDecorationIndex(int index, BuildHotspot hs) {
        for (int i = 0; i < hotspots.length; i++) {
            Hotspot h = hotspots[i];
            if (h.getHotspot() == hs) {
                h.setDecorationIndex(index);
            }
        }
    }

    /**
     * Gets the stairs hotspot for this room (or null if no stairs are available).
     *
     * @return The stairs.
     */
    public Hotspot getStairs() {
        for (Hotspot h : hotspots) {
            if (h.getHotspot().getType() == BuildHotspotType.STAIRCASE
                    || h.getHotspot() == BuildHotspot.LADDER || h.getHotspot() == BuildHotspot.TRAPDOOR
                    || (h.getHotspot() == BuildHotspot.CENTREPIECE_1 && h.getDecorationIndex() == 4)
                    || (h.getHotspot() == BuildHotspot.CENTREPIECE_2 && h.getDecorationIndex() == 2)) {
                return h;
            }
        }
        return null;
    }

    /**
     * Gets the exit directions
     *
     * @return The exits information.
     */
    public boolean[] getExits() {
        return getExits(rotation);
    }

    /**
     * Gets the exit directions.
     *
     * @return The directions at which you can exit the room (0=east, 1=south, 2=west, 3=north).
     */
    public boolean[] getExits(Direction rotation) {
        boolean[] exits = properties.getExits();
        if (chunk.getRotation() != rotation.toInteger()) {
            boolean[] exit = new boolean[exits.length];
            int offset = rotation.toInteger() - chunk.getRotation();
            for (int i = 0; i < 4; i++) {
                exit[(i + offset) % 4] = exits[i];
            }
            return exit;
        }
        return exits;
    }

    /**
     * Gets the hotspot for the given coordinates.
     *
     * @param build The build hotspot.
     * @param x     The x-coordinate.
     * @param y     The y-coordinate.
     * @return The hotspot.
     */
    public Hotspot getHotspot(BuildHotspot build, int x, int y) {
        for (int i = 0; i < getHotspots().length; i++) {
            Hotspot h = getHotspots()[i];
            if (h.getCurrentX() == x && h.getCurrentY() == y && h.getHotspot() == build) {
                return h;
            }
        }
        return null;
    }

    /**
     * Sets the properties.
     *
     * @param properties The properties.
     */
    public void updateProperties(Player player, RoomProperties properties) {
        this.properties = properties;
        decorate(player.getHouseManager().getStyle());
        if (hotspots.length != properties.getHotspots().length) {
            return;
        }
        for (int i = 0; i < hotspots.length; i++) {
            Hotspot h = hotspots[i];
            Hotspot hs = hotspots[i] = properties.getHotspots()[i].copy();
            hs.setCurrentX(h.getCurrentX());
            hs.setCurrentY(h.getCurrentY());
            hs.setDecorationIndex(h.getDecorationIndex());
        }
    }

    /**
     * Gets the chunk.
     *
     * @return The chunk.
     */
    public RegionChunk getChunk() {
        return chunk;
    }

    /**
     * Sets the chunk.
     *
     * @param chunk The chunk to set.
     */
    public void setChunk(RegionChunk chunk) {
        this.chunk = chunk;
    }

    /**
     * Gets the hotspots.
     *
     * @return The hotspots.
     */
    public Hotspot[] getHotspots() {
        return hotspots;
    }

    /**
     * Sets the hotspots.
     *
     * @param hotspots The hotspots to set.
     */
    public void setHotspots(Hotspot[] hotspots) {
        this.hotspots = hotspots;
    }

    /**
     * Gets the properties.
     *
     * @return The properties.
     */
    public RoomProperties getProperties() {
        return properties;
    }

    /**
     * Sets the room rotation.
     *
     * @param rotation The rotation.
     */
    public void setRotation(Direction rotation) {
        this.rotation = rotation;
    }

    /**
     * Gets the rotation.
     *
     * @return The rotation.
     */
    public Direction getRotation() {
        return rotation;
    }

}
