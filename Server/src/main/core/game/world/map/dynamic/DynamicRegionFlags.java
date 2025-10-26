package core.game.world.map.dynamic;

/**
 * Represents dynamic clipping flags for a region.
 */
public class DynamicRegionFlags {
    /**
     * Stores the flags for 4 planes of a 64x64 region.
     */
    private final int[][][] flags = new int[4][64][64];

    /**
     * Adds a blocking flag to a specific tile on a plane.
     *
     * @param plane The plane (z-level) of the region (0-3).
     * @param x The x-coordinate within the region (0-63).
     * @param y The y-coordinate within the region (0-63).
     * @param mask The bitmask representing the blocking type.
     */
    public void addFlag(int plane, int x, int y, int mask) {
        flags[plane][x][y] |= mask;
    }

    /**
     * Removes a blocking flag from a specific tile on a plane.
     *
     * @param plane The plane (z-level) of the region (0-3).
     * @param x The x-coordinate within the region (0-63).
     * @param y The y-coordinate within the region (0-63).
     * @param mask The bitmask representing the blocking type to remove.
     */
    public void removeFlag(int plane, int x, int y, int mask) {
        flags[plane][x][y] &= ~mask;
    }

    /**
     * Checks if a specific tile on a plane is blocked by a given mask.
     *
     * @param plane The plane (z-level) of the region (0-3).
     * @param x The x-coordinate within the region (0-63).
     * @param y The y-coordinate within the region (0-63).
     * @param mask The bitmask to check for blocking.
     * @return {@code true} if the tile has the blocking flag, {@code false} otherwise.
     */
    public boolean isBlocked(int plane, int x, int y, int mask) {
        return (flags[plane][x][y] & mask) != 0;
    }

    /**
     * Clears all blocking flags for all tiles on all planes.
     */
    public void clear() {
        for (int p = 0; p < 4; p++)
            for (int x = 0; x < 64; x++)
                for (int y = 0; y < 64; y++)
                    flags[p][x][y] = 0;
    }
}
