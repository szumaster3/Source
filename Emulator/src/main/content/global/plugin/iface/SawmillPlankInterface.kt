package content.global.plugin.iface

import content.global.skill.construction.items.Plank
import core.api.*
import core.game.component.Component
import core.game.component.ComponentDefinition
import core.game.component.ComponentPlugin
import core.game.node.entity.player.Player
import core.game.node.entity.player.link.diary.DiaryType
import core.game.node.item.Item
import core.plugin.Initializable
import core.plugin.Plugin
import org.rs.consts.Components
import org.rs.consts.Items

/**
 * Represents the sawmill plank interface.
 */
@Initializable
class SawmillPlankInterface : ComponentPlugin() {
    override fun newInstance(arg: Any?): Plugin<Any> {
        ComponentDefinition.put(Components.POH_SAWMILL_403, this)
        return this
    }

    override fun handle(
        player: Player,
        component: Component,
        opcode: Int,
        button: Int,
        slot: Int,
        itemId: Int,
    ): Boolean {
        val plank: Plank? =
            when (button) {
                in 102..107 -> Plank.WOOD
                in 109..113 -> Plank.OAK
                in 115..119 -> Plank.TEAK
                in 121..125 -> Plank.MAHOGANY
                else -> null
            }

        var amount = -1
        val fullIndex =
            if (plank == Plank.WOOD) {
                107
            } else if (plank == Plank.OAK) {
                113
            } else if (plank == Plank.TEAK) {
                119
            } else {
                125
            }

        val difference =
            if (plank != Plank.WOOD) {
                fullIndex - button
            } else {
                fullIndex - button - if (button != 107) 1 else 0
            }

        when (difference) {
            0 -> amount = 1
            1 -> amount = 5
            2 -> amount = 10
            3 -> amount = 69
            4 -> amount = player.inventory.getAmount(plank?.log!!)
        }
        if (amount == 69) {
            val planks = plank
            sendInputDialogue(player, true, "Enter the amount:") { value ->
                createPlank(player, planks!!, value as Int)
            }
            return true
        }
        if (plank != null) {
            createPlank(player, plank, amount)
        }
        return true
    }

    private fun createPlank(
        player: Player,
        plank: Plank,
        amount: Int,
    ) {
        closeInterface(player)
        var amount = amount
        if (amount > amountInInventory(player, plank.log)) {
            amount = amountInInventory(player, plank.log)
        }
        if (!inInventory(player, plank.log)) {
            sendMessage(player, "You are not carrying any logs to cut into planks.")
            return
        }
        if (!inInventory(player, Items.COINS_995, plank.price * amount)) {
            sendDialogue(player, "Sorry, I don't have enough coins to pay for that.")
            return
        }
        if (removeItem(player, Item(Items.COINS_995, plank.price * amount))) {
            if (plank == Plank.WOOD) {
                finishDiaryTask(player, DiaryType.VARROCK, 0, 3)
            }
            if (plank == Plank.MAHOGANY && amount >= 20) {
                finishDiaryTask(player, DiaryType.VARROCK, 1, 15)
            }
            val remove = plank.log.asItem()
            remove.amount = amount
            if (removeItem(player = player, item = remove)) {
                val planks = plank.plank.asItem()
                planks.amount = amount
                player.inventory.add(planks)
            }
        }
    }
}
