package content.region.karamja.handlers.shilo

import content.region.karamja.dialogue.shilovillage.BlackPrismDialogue
import content.region.karamja.handlers.shilo.ShiloVillageListener.Companion
import core.api.*
import core.api.interaction.getNPCName
import core.api.quest.hasRequirement
import core.api.ui.closeDialogue
import core.game.dialogue.DialogueFile
import core.game.global.action.DoorActionHandler
import core.game.interaction.IntType
import core.game.interaction.InteractionListener
import core.game.interaction.QueueStrength
import core.game.node.entity.npc.NPC
import core.game.node.entity.player.Player
import core.game.node.item.Item
import core.game.system.task.Pulse
import core.game.world.map.Location
import core.tools.END_DIALOGUE
import org.rs.consts.*

val ANTIQUE_ITEMS = AntiqueItem.values().map { it.antique }.toIntArray()

class ShiloVillageListener : InteractionListener {
    override fun defineListeners() {

        /*
         * Handles drop of notes.
         */

        on(NOTES, IntType.ITEM, "drop") { player, node ->
            removeItem(player, node.asItem())
            sendMessage(player, "As you drop the delicate scrolls onto the floor, they disintegrate immediately.")
            return@on true
        }

        /*
         * Handles furnace doors.
         */

        on(BLACKSMITH_DOOR, IntType.SCENERY, "open") { player, node ->
            if (!getAttribute(player, "shilo-village:blacksmith-doors", false)) {
                sendNPCDialogue(
                    player,
                    NPCs.YOHNUS_513,
                    "Sorry but the blacksmiths is closed. But I can let you use the furnace at the cost of 20 gold pieces."
                )
            } else {
                DoorActionHandler.handleAutowalkDoor(player, node.asScenery())
            }
            return@on true
        }

        /*
         * Handles enter the shilo village.
         */

        on(Scenery.BROKEN_CART_2216, IntType.SCENERY, "look-at") { player, _ ->
            if (!hasRequirement(player, Quests.SHILO_VILLAGE)) return@on true

            val playerX = player.location.x
            val location = if (playerX > 2878) Location(2876, 2952) else Location(2880, 2952)

            lock(player, 1)
            sendMessage(player, "You climb up onto the cart.")
            sendMessage(player, "You nimbly jump from one side of the cart...")
            player.impactHandler.disabledTicks = 4
            queueScript(player, 1, QueueStrength.WEAK) {
                unlock(player)
                teleport(player, location)
                sendMessage(player, "...to the other and climb down again.")
                resetAnimator(player)
                return@queueScript stopExecuting(player)

            }
            return@on true
        }

        /*
         * Handles interaction with NPCs & Scenery related
         * to travel between Shilo Village & Brimhaven.
         */

        on(Scenery.TRAVEL_CART_2230, IntType.SCENERY, "board") { player, _ ->
            openDialogue(player, CartTravelDialogue(), NPC(NPCs.HAJEDY_510))
            return@on true
        }

        on(NPCs.HAJEDY_510, IntType.NPC, "talk-to") { player, _ ->
            openDialogue(player, CartTravelDialogue(), NPC(NPCs.HAJEDY_510))
            return@on true
        }

        on(Scenery.TRAVEL_CART_2230, IntType.SCENERY, "pay-fare") { player, _ ->
            quickTravel(player, NPCs.HAJEDY_510)
            return@on true
        }

        on(NPCs.HAJEDY_510, IntType.NPC, "pay-fare") { player, _ ->
            quickTravel(player, NPCs.HAJEDY_510)
            return@on true
        }

        on(Scenery.TRAVEL_CART_2265, IntType.SCENERY, "board") { player, _ ->
            openDialogue(player, CartTravelDialogue(), NPC(NPCs.VIGROY_511))
            return@on true
        }

        on(Scenery.TRAVEL_CART_2265, IntType.SCENERY, "pay-fare") { player, _ ->
            quickTravel(player, NPCs.VIGROY_511)
            return@on true
        }

        on(NPCs.VIGROY_511, IntType.NPC, "talk-to") { player, npc ->
            openDialogue(player, CartTravelDialogue(), npc)
            return@on true
        }

        on(NPCs.VIGROY_511, IntType.NPC, "pay-fare") { player, npc ->
            quickTravel(player, npc.id)
            return@on true
        }

        /*
         * Handles antique items exchange with Yanni NPCs.
         */

        onUseWith(IntType.NPC, ANTIQUE_ITEMS, YANNI) { player, used, _ ->
            val antique = used.id

            if (!inInventory(player, antique, 1)) {
                sendNPCDialogue(player, NPCs.YANNI_SALIKA_515, "Sorry Bwana, you have nothing I am interested in.")
                return@onUseWith true
            }

            when (antique) {
                Items.BLACK_PRISM_4808 -> openDialogue(player, BlackPrismDialogue())
                else -> openDialogue(player, YanniDialogue(antique))
            }
            return@onUseWith true
        }

        on(WOODEN_GATE, IntType.SCENERY, "open") { player, node ->
            val nextDoorId = if (player.location.y == 2952) node.id - 1 else node.id + 1
            DoorActionHandler.autowalkFence(player, node.asScenery(), node.id, nextDoorId)
            if (player.location.x == 2867) sendMessage(player, "You make your way out of Shilo Village.")
            return@on true
        }

        on(METAL_GATE, IntType.SCENERY, "open") { player, node ->
            DoorActionHandler.handleAutowalkDoor(player, node.asScenery())
            if (player.location.x == 2874) {
                sendMessage(player, "You open the gates and make your way back out of the village.")
            }
            return@on true
        }
    }

    companion object {
        private const val YANNI = NPCs.YANNI_SALIKA_515
        private const val NOTES = Items.BERVIRIUS_NOTES_624
        private const val BLACKSMITH_DOOR = Scenery.BLACKSMITH_S_DOOR_2266
        private val WOODEN_GATE = intArrayOf(Scenery.WOODEN_GATE_2261, Scenery.WOODEN_GATE_2262)
        private val METAL_GATE = intArrayOf(Scenery.METAL_GATE_2259, Scenery.METAL_GATE_2260)

        /**
         * Handles quick travel between shilo village and brimhaven.
         *
         * @param player The player who is traveling.
         * @param npc The NPC id that the player is interacting with.
         */
        fun quickTravel(player: Player, npc: Int) {
            if (!hasRequirement(player, Quests.SHILO_VILLAGE, true)) return
            val isShilo = npc == NPCs.HAJEDY_510
            val npcName = getNPCName(npc)

            if (!removeItem(player, Item(Items.COINS_995, 10))) {
                sendMessage(player, "You don't have enough coins.")
                return
            }

            lock(player, 3)
            closeDialogue(player)
            sendMessage(player, "You pay the fare and hand 10 gold coins to $npcName.")
            openOverlay(player, Components.FADE_TO_BLACK_120)
            queueScript(player, 3, QueueStrength.SOFT) {
                val destination = if (isShilo) Location.create(2834, 2951, 0) else Location.create(2780, 3212, 0)
                teleport(player, destination)
                closeOverlay(player)
                openOverlay(player, Components.FADE_FROM_BLACK_170)
                sendDialogueLines(player, "You feel tired from the journey, but at least you didn't have to walk", "all that distance.")
                return@queueScript stopExecuting(player)
            }
        }
    }
}

class CartTravelDialogue : DialogueFile() {
    override fun handle(
        componentID: Int,
        buttonID: Int,
    ) {
        if (!hasRequirement(player!!, Quests.SHILO_VILLAGE)) return
        val shilo = npc?.id == NPCs.HAJEDY_510
        when (stage) {
            0 -> npcl("I am offering a cart ride to " + (if (shilo) "Shilo Village" else "Brimhaven") + " if you're interested? It will cost 10 gold coins. Is that Ok?").also { stage++ }
            1 -> {
                if (!inInventory(player!!, Items.COINS_995, 10)) {
                    playerl("Sorry, I don't seem to have enough coins.").also { stage = END_DIALOGUE }
                } else {
                    playerl("Yes please, I'd like to go to " + (if (shilo) "Shilo Village" else "Brimhaven") + ".").also { stage++ }
                }
            }
            2 -> npcl("Great! Just hop into the cart then and we'll go!").also { stage++ }
            3 -> sendDialogue(player!!, "You hop into the cart and the driver urges the horses on. You take a taxing journey through the jungle to " + (if (shilo) "Shilo Village" else "Brimhaven") + ".").also { stage++ }
            4 -> {
                end()
                ShiloVillageListener.quickTravel(player!!, npc!!.id)
            }
        }
    }
}
