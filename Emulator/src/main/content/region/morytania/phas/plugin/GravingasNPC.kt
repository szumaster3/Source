package content.region.morytania.phas.plugin

import core.api.sendChat
import core.game.node.entity.npc.NPC
import core.game.node.entity.npc.NPCBehavior
import core.tools.RandomFunction
import org.rs.consts.NPCs

class GravingasNPC : NPCBehavior(NPCs.GRAVINGAS_1685) {
    private val forceChat =
        arrayOf(
            "Down with Necrovaus!!",
            "Rise up my fellow ghosts, and we shall be victorious!",
            "Power to the Ghosts!!",
            "Rise together, Ghosts without a cause!!",
            "United we conquer - divided we fall!!",
            "We shall overcome!!",
            "Let Necrovarus know we want out!!",
            "Don't stay silent - victory in numbers!!",
        )

    override fun tick(self: NPC): Boolean {
        if (RandomFunction.random(15, 100) == 5) {
            sendChat(self, forceChat.random())
        }
        return true
    }
}
