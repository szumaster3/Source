package content.global.handlers.item.withnpc

import content.region.karamja.dialogue.apeatoll.dungeon.ZooknockDialogueFile
import core.api.openDialogue
import core.game.interaction.IntType
import core.game.interaction.InteractionListener
import org.rs.consts.Items
import org.rs.consts.NPCs

open class ItemsOnZooknockListener : InteractionListener {
    private val goldBar = Items.GOLD_BAR_2357
    private val monkeyAmuletMould = Items.MAMULET_MOULD_4020
    private val monkeyDentures = Items.MONKEY_DENTURES_4006
    val items = intArrayOf(goldBar, monkeyDentures, monkeyAmuletMould)

    val zooknockNPC = NPCs.ZOOKNOCK_1425

    override fun defineListeners() {
        onUseWith(IntType.NPC, items, zooknockNPC) { player, used, _ ->
            openDialogue(player, ZooknockDialogueFile(used.id))
            return@onUseWith false
        }
    }
}
