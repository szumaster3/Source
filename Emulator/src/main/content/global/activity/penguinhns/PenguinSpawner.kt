package content.global.activity.penguinhns

import core.game.node.entity.npc.NPC
import core.game.world.map.Location
import org.rs.consts.NPCs

/**
 * Represents the spawning penguin NPCs in the world.
 */
class PenguinSpawner {

    /**
     * Spawns a specified number of unique penguins randomly.
     *
     * @param amount The number of penguins to spawn.
     * @return A list of the ordinals of the spawned penguins.
     */
    fun spawnPenguins(amount: Int): ArrayList<Int> {
        var counter = 0
        val availableOrdinals = (0 until Penguin.values().size).toMutableList()
        val spawnedOrdinals = ArrayList<Int>()
        while (counter < amount) {
            val peng = Penguin.values()[availableOrdinals.random()]
            availableOrdinals.remove(peng.ordinal)
            spawnedOrdinals.add(peng.ordinal)
            NPC(peng.id, peng.location)
                .also {
                    PenguinManager.npcs.add(it)
                    it.isNeverWalks = true
                    it.isWalks = false
                }.init()
            counter++
        }
        return spawnedOrdinals
    }

    /**
     * Spawns penguins specified by their ordinals.
     *
     * @param ordinals List of penguin ordinals to spawn.
     */
    fun spawnPenguins(ordinals: List<Int>) {
        ordinals.forEach { it ->
            val peng = Penguin.values()[it]
            NPC(peng.id, peng.location)
                .also {
                    PenguinManager.npcs.add(it)
                    it.isNeverWalks = true
                    it.isWalks = false
                }.init()
        }
    }
}

/**
 * Represents all possible penguins for the Penguin Hunter activity.
 *
 * @property id The [NPC] id.
 * @property hint Text hint describing location of penguin.
 * @property location The [Location] of the penguin [NPC].
 */
enum class Penguin(
    val id: Int,
    val hint: String,
    val location: Location,
) {
    CACTUS_1(NPCs.CACTUS_8107, "located in the northern desert.", Location(3310, 3157, 0)),
    CACTUS_2(NPCs.CACTUS_8107, "located in the southern desert.", Location.create(3259, 3052, 0)),

    BUSH_1(NPCs.BUSH_8105, "located between Fremennik and barbarians.", Location.create(2532, 3588, 0)),
    BUSH_2(NPCs.BUSH_8105, "located where banana smugglers dwell.", Location.create(2740, 3233, 0)),
    BUSH_3(NPCs.BUSH_8105, "located south of Ardougne.", Location.create(2456, 3092, 0)),
    BUSH_4(NPCs.BUSH_8105, "located deep in the jungle.", Location.create(2832, 3053, 0)),
    BUSH_5(NPCs.BUSH_8105, "located where eagles fly.", Location.create(2326, 3516, 0)),
    BUSH_6(NPCs.BUSH_8105, "located south of Ardougne.", Location.create(2513, 3154, 0)),
    BUSH_7(NPCs.BUSH_8105, "located near a big tree surrounded by short people.", Location.create(2387, 3451, 0)),
    BUSH_8(NPCs.BUSH_8105, "located in the kingdom of Asgarnia.", Location.create(2951, 3511, 0)),
    BUSH_9(NPCs.BUSH_8105, "located in the northern desert.", Location.create(3350, 3311, 0)),
    BUSH_10(NPCs.BUSH_8105, "located somewhere in the kingdom of Kandarin.", Location.create(2633, 3501, 0)),
    BUSH_11(NPCs.BUSH_8105, "located south of Ardougne.", Location.create(2440, 3206, 0)),
    BUSH_12(NPCs.BUSH_8105, "located where wizards study.", Location.create(3112, 3149, 0)),
    BUSH_13(NPCs.BUSH_8105, "located where bloodsuckers rule.", Location.create(3457, 3387, 0)),
    BUSH_14(NPCs.BUSH_8105, "located where monkeys rule.", Location.create(2809, 2775, 0)),
    BUSH_15(NPCs.BUSH_8105, "located south of Ardougne.", Location.create(2482, 3126, 0)),
    BUSH_16(NPCs.BUSH_8105, "located deep in the jungle.", Location.create(2938, 2978, 0)),

    BUSH_17(NPCs.BUSH_8105, "located around Feldip Hills, near Gu'Tanoth.", Location.create(2606, 3005, 0)),
    BUSH_18(NPCs.BUSH_8105, "located around Feldip Hills, near Gu'Tanoth.", Location.create(2438, 3045, 0)),
    BUSH_19(NPCs.BUSH_8105, "located around Feldip Hills, near Gu'Tanoth.", Location.create(2340, 3064, 0)),
    BUSH_20(NPCs.BUSH_8105, "located on islands where brothers quarrel.", Location.create(2356, 3848, 0)),
    BUSH_21(NPCs.BUSH_8105, "located near a town of werewolves.", Location.create(3600, 3485, 0)),


    ROCK_1(NPCs.ROCK_8109, "located where the Imperial Guard train.", Location.create(2852, 3578, 0)),
    ROCK_2(NPCs.ROCK_8109, "located in the kingdom of Misthalin.", Location.create(3356, 3416, 0)),
    ROCK_3(NPCs.ROCK_8109, "located near some ogres.", Location.create(2631, 2980, 0)),
    ROCK_4(NPCs.ROCK_8109, "located in the Kingdom of Asgarnia.", Location.create(3013, 3501, 0)),
    ROCK_5(NPCs.ROCK_8109, "located between Fremennik and barbarians.", Location.create(2532, 3630, 0)),
    ROCK_6(NPCs.ROCK_8109, "located on islands where brothers quarrel.", Location.create(2312, 3814, 0)),
    ROCK_7(NPCs.ROCK_8109, "located on a large crescent island.", Location.create(2152, 3934, 0)),
    ROCK_8(NPCs.ROCK_8109, "located near the coast, east of Ardougne.", Location.create(2733, 3282, 0)),
    ROCK_9(NPCs.ROCK_8109, "located on islands where brothers quarrel.", Location.create(2413, 3846, 0)),

    CRATE_1(NPCs.CRATE_8108, "located in the kingdom of Misthalin.", Location.create(3112, 3332, 0)),
    CRATE_2(NPCs.CRATE_8108, "located in the Kingdom of Misthalin.", Location.create(3305, 3508, 0)),
    CRATE_3(NPCs.CRATE_8108, "located near a town of werewolves.", Location.create(3637, 3486, 0)),
    // CRATE_4(NPCs.CRATE_8108, "located near the island of Dragontooth.", Location.create(3824, 3562, 0)),


    BARREL_1(NPCs.CRATE_8108, "located where no weapons may go.", Location.create(2806, 3383, 0)),
    BARREL_2(NPCs.CRATE_8108, "located south of Ardougne.", Location.create(2662, 3152, 0)),
    BARREL_3(NPCs.CRATE_8108, "located near the city of ghosts.", Location.create(3654, 3491, 0)),
    BARREL_4(NPCs.CRATE_8108, "located where fishers colonise.", Location.create(2322, 3658, 0)),


    TOADSTOOL_1(NPCs.TOADSTOOL_8110, "located in the kingdom of Misthalin.", Location.create(3156, 3178, 0)),
    TOADSTOOL_2(NPCs.TOADSTOOL_8110, "located in the fairy realm.", Location.create(2409, 4462, 0)),

    TOADSTOOL_3(NPCs.TOADSTOOL_8110, "located near the pointy-eared ones.", Location.create(2311, 3177, 0)),
    TOADSTOOL_4(NPCs.TOADSTOOL_8110, "located near the pointy-eared ones.", Location.create(2219, 3227, 0)),
    TOADSTOOL_5(NPCs.TOADSTOOL_8110, "located near the pointy-eared ones.", Location.create(2296, 3271, 0));



    companion object {
        /**
         * Map to quickly find a Penguin by its location string.
         */
        private val locationMap = values().associateBy { it.location.toString() }

        /**
         * Gets the penguin corresponding to a given location.
         *
         * @param location The location to search for.
         * @return The matching Penguin if found, `null` otherwise.
         */
        fun forLocation(location: Location): Penguin? = locationMap[location.toString()]
    }
}
