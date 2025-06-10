package content.region.misthalin.handlers.varrock

import core.api.movement.finishedMoving
import core.game.node.entity.npc.NPC
import core.game.node.entity.npc.NPCBehavior
import core.game.world.map.Location
import core.game.world.update.flag.context.Animation
import org.rs.consts.Animations
import org.rs.consts.NPCs

private val movementPath = arrayOf(
    Location.create(3316, 3506, 0),
    Location.create(3317, 3513, 0),
)

class SawmillWorkerNPC : NPCBehavior(NPCs.WILL_7737, NPCs.WILL_7738) {
    private var nextAction = 0L

    override fun onCreation(self: NPC) {
        self.configureMovementPath(*movementPath)
        self.isWalks = true
    }

    override fun tick(self: NPC): Boolean {
        val now = System.currentTimeMillis()
        if (now < nextAction) return true

        if (!finishedMoving(self)) {
            when (self.location) {
                movementPath[0] -> {
                    self.sendChat("Erf!", 1)
                    self.animate(Animation.create(Animations.MULTI_BEND_OVER_827))
                    self.transform(NPCs.WILL_7737)
                    self.resetWalk()
                    nextAction = now + 3000L
                }
                movementPath[1] -> {
                    self.sendChat("Oof!", 2)
                    self.animate(Animation.create(Animations.LUMBER_YARD_EMPLOYEE_9218))
                    self.reTransform()
                    self.canMove(movementPath[0])
                    nextAction = now + 3000L
                }
            }
        }

        return true
    }
}
