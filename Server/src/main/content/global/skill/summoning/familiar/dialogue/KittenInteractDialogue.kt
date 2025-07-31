package content.global.skill.summoning.familiar.dialogue

import core.api.*
import core.game.dialogue.Dialogue
import core.game.node.entity.npc.NPC
import core.game.node.entity.player.Player
import core.game.system.task.Pulse
import core.game.world.GameWorld.Pulser
import core.game.world.map.RegionManager.getLocalNpcs
import core.game.world.map.path.Pathfinder
import core.game.world.update.flag.context.Animation
import core.plugin.Initializable
import core.tools.END_DIALOGUE
import org.rs.consts.Animations

@Initializable
class KittenInteractDialogue(player: Player? = null) : Dialogue(player) {
    override fun open(vararg args: Any?): Boolean {
        setTitle(player, 3)
        sendDialogueOptions(
            player,
            "Interact with Kitten",
            "Stroke",
            "Chase vermin",
            "Shoo away"
        )
        return true
    }

    override fun handle(interfaceId: Int, buttonId: Int, ): Boolean {
        when (stage) {
            0 -> when (buttonId) {
                1 -> {
                    player.familiarManager.familiar.face(player)
                    animate(player, PLAYER_STROKE_ANIMATION)
                    playAudio(player, 340)
                    player.familiarManager.familiar.animate(KITTEN_STROKE_ANIMATION)
                    player.familiarManager.familiar.sendChat("Purr...purr...")
                    interpreter.sendDialogues(player, null, "That cat sure loves to be stroked.")
                    stage = 5
                }

                2 -> {
                    end()
                    player.sendChat("Go on puss...kill that rat!")
                    var cant = true
                    var rat: NPC? = null
                    for (n in getLocalNpcs(player.location, 10)) {
                        if (!n.name.contains("rat")) {
                            cant = false
                            continue
                        }
                        if (n.location.getDistance(player.familiarManager.familiar.location) < 8) {
                            cant = true
                            rat = n
                            break
                        } else {
                            cant = false
                        }
                    }
                    if (!cant) {
                        sendMessage(player, "Your cat cannot get to its prey.")
                    } else {
                        playAudio(player, 339)
                        player.familiarManager.familiar.sendChat("Meeeoooooowwww!")
                        val path = Pathfinder.find(player.familiarManager.familiar, rat)
                        path.walk(player.familiarManager.familiar)
                        rat!!.sendChat("Eeek!")
                        Pulser.submit(
                            object : Pulse(5) {
                                override fun pulse(): Boolean {
                                    player.familiarManager.familiar.call()
                                    sendMessage(player, "The rat manages to get away!")
                                    return true
                                }
                            },
                        )
                    }
                }
                3 -> {
                    sendDialogueOptions(player, "Are you sure?", "Yes I am.", "No I'm not.")
                    stage = 4
                }
            }
            4 -> when (buttonId) {
                1 -> {
                    end()
                    if (player.familiarManager.hasFamiliar()) {
                        player.sendChat("Shoo cat!")
                        val currentPet = player.familiarManager.familiar as content.global.skill.summoning.pet.Pet
                        player.familiarManager.familiar.sendChat("Miaow!")
                        player.familiarManager.removeDetails(currentPet.itemIdHash)
                        player.familiarManager.familiar.dismiss()
                        player.packetDispatch.sendMessage("The cat has run away.")
                    }
                }

                2 -> {
                    end()
                    stage = END_DIALOGUE
                }
            }

            5 -> {
                if (player.familiarManager.hasFamiliar()) {
                    player.familiarManager.familiar.sendChat("Miaow!")
                }
                end()
            }
        }
        return true
    }

    override fun getIds(): IntArray = intArrayOf(343823)

    companion object {
        private val PLAYER_STROKE_ANIMATION = Animation(Animations.KITTEN_STROKE_9224)
        private val KITTEN_STROKE_ANIMATION = Animation(Animations.KITTEN_STROKE_ANIMATION_9173)
    }
}
