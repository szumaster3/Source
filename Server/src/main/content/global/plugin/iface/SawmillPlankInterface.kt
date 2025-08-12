package content.global.plugin.iface

import content.global.skill.construction.items.PlankType
import core.api.*
import core.game.component.Component
import core.game.component.ComponentDefinition
import core.game.component.ComponentPlugin
import core.game.node.entity.player.Player
import core.game.node.entity.player.link.diary.DiaryType
import core.game.node.item.Item
import core.plugin.Initializable
import core.plugin.Plugin
import shared.consts.Components
import shared.consts.Items

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
        val planks: PlankType? =
            when (button) {
                in 102..107 -> PlankType.WOOD
                in 109..113 -> PlankType.OAK
                in 115..119 -> PlankType.TEAK
                in 121..125 -> PlankType.MAHOGANY
                else -> null
            }

        var amount = -1
        val fullIndex =
            if (planks == PlankType.WOOD) {
                107
            } else if (planks == PlankType.OAK) {
                113
            } else if (planks == PlankType.TEAK) {
                119
            } else {
                125
            }

        val difference =
            if (planks != PlankType.WOOD) {
                fullIndex - button
            } else {
                fullIndex - button - if (button != 107) 1 else 0
            }

        when (difference) {
            0 -> amount = 1
            1 -> amount = 5
            2 -> amount = 10
            3 -> amount = 69
            4 -> amount = player.inventory.getAmount(planks?.log!!)
        }
        if (amount == 69) {
            val planks = planks
            sendInputDialogue(player, true, "Enter the amount:") { value ->
                createPlank(player, planks!!, value as Int)
            }
            return true
        }
        if (planks != null) {
            createPlank(player, planks, amount)
        }
        return true
    }

    private fun createPlank(
        player: Player,
        plank: PlankType,
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
            if (plank == PlankType.WOOD) {
                finishDiaryTask(player, DiaryType.VARROCK, 0, 3)
            }
            if (plank == PlankType.MAHOGANY && amount >= 20) {
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
