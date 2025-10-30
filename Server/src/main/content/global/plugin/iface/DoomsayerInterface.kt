package content.global.plugin.iface

import core.api.getVarbit
import core.api.sendMessage
import core.api.sendMessages
import core.game.interaction.InterfaceListener
import core.game.node.entity.player.link.WarningManager
import core.game.node.entity.player.link.Warnings
import shared.consts.Components

/**
 * Represents the warning interface.
 * @author szu
 */
class DoomsayerInterface : InterfaceListener {

    override fun defineInterfaceListeners() {
        /*
         * Handles toggle of warnings.
         */

        on(Components.CWS_DOOMSAYER_583) { player, _, _, buttonID, _, _ ->
            val warning = Warnings.values().find { it.buttonId == buttonID } ?: return@on true
            if (buttonID == 81) {
                sendMessage(player, "This option is only available on PvP worlds.")
                return@on true
            }

            val timesSeen = getVarbit(player, warning.varbit)
            if (timesSeen < 6) {
                sendMessages(
                    player,
                    "You cannot toggle this warning screen on or off. You need to go to the area it is linked to enough times to have the option to do so."
                )
                return@on true
            }

            val wasDisabled = WarningManager.isWarningDisabled(player, warning)
            WarningManager.toggleWarning(player, warning)

            val status = if (wasDisabled) "enabled" else "disabled"
            sendMessage(player, "You have $status the ${warning.name.lowercase().replace('_', ' ')} warning.")
            return@on true
        }
    }
}
