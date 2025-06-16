package content.global.plugin.npc

import core.api.animate
import core.api.sendChat
import core.api.stopWalk
import core.game.node.entity.npc.NPC
import core.game.node.entity.npc.NPCBehavior
import core.tools.RandomFunction
import org.rs.consts.Animations
import org.rs.consts.NPCs

class TownCrierNPC : NPCBehavior(NPCs.TOWN_CRIER_6135, NPCs.TOWN_CRIER_6136, NPCs.TOWN_CRIER_6137, NPCs.TOWN_CRIER_6138, NPCs.TOWN_CRIER_6139) {
    private val forceChatAnimation = mapOf(
        "The Duke of Lumbridge needs a hand." to Animations.TOWN_CRIER_RING_BELL_6865,
        "The squirrels! The squirrels are coming! Noooo, get them out of my head!" to Animations.TOWN_CRIER_SCRATCHES_HEAD_6863,
    )

    override fun tick(self: NPC): Boolean {
        if (RandomFunction.random(150) == 0) {
            stopWalk(self)
            val (chat, animation) = forceChatAnimation.entries.random()
            animate(self, animation)
            sendChat(self, chat)
        }
        return super.tick(self)
    }
}
