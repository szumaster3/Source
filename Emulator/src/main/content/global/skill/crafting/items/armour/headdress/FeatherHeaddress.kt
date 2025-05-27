package content.global.skill.crafting.items.armour.headdress

import org.rs.consts.Items

/**
 * Represents the feather headdress.
 */
enum class FeatherHeaddress(val base: Int, val product: Int) {
    FEATHER_HEADDRESS_BLUE(Items.BLUE_FEATHER_10089, Items.FEATHER_HEADDRESS_12210),
    FEATHER_HEADDRESS_ORANGE(Items.ORANGE_FEATHER_10091, Items.FEATHER_HEADDRESS_12222),
    FEATHER_HEADDRESS_RED(Items.RED_FEATHER_10088, Items.FEATHER_HEADDRESS_12216),
    FEATHER_HEADDRESS_STRIPY(Items.STRIPY_FEATHER_10087, Items.FEATHER_HEADDRESS_12219),
    FEATHER_HEADDRESS_YELLOW(Items.YELLOW_FEATHER_10090, Items.FEATHER_HEADDRESS_12213);

    companion object {
        private val baseToHeaddressMap = HashMap<Int, FeatherHeaddress>()

        init {
            for (headdress in values()) {
                baseToHeaddressMap[headdress.base] = headdress
            }
        }

        /**
         * For base feather headdress.
         *
         * @param baseId the base id
         * @return the feather headdress
         */
        fun forBase(baseId: Int): FeatherHeaddress? {
            return baseToHeaddressMap[baseId]
        }
    }
}
