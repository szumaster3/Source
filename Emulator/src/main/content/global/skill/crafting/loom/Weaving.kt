package content.global.skill.crafting.loom

import core.game.node.item.Item
import org.rs.consts.Items

enum class Weaving(val product: Item, val required: Item, val level: Int, val experience: Double, ) {
    SACK(Item(Items.EMPTY_SACK_5418), Item(Items.JUTE_FIBRE_5931, 4), 21, 38.0),
    BASKET(Item(Items.BASKET_5376), Item(Items.WILLOW_BRANCH_5933, 6), 36, 56.0),
    CLOTH(Item(Items.STRIP_OF_CLOTH_3224), Item(Items.BALL_OF_WOOL_1759, 4), 10, 12.0),
}
