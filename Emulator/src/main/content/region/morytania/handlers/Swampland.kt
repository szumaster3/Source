package content.region.morytania.handlers

import core.api.getRegionBorders
import core.game.world.map.zone.MapZone
import core.game.world.map.zone.ZoneRestriction

class Swampland : MapZone("Mort Myre Swamp", true) {
    override fun configure() {
        register(getRegionBorders(13621))
        register(getRegionBorders(13877))
        register(getRegionBorders(13876))
        register(getRegionBorders(13875))
    }

    fun getRestrictions(): Array<ZoneRestriction> = arrayOf(ZoneRestriction.CANNON)

    companion object {
        val instance = Swampland()
    }
}
