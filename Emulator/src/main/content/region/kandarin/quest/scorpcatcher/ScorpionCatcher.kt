package content.region.kandarin.quest.scorpcatcher

import core.api.*
import core.game.node.entity.player.Player
import core.game.node.entity.player.link.quest.Quest
import core.game.node.entity.skill.Skills
import core.plugin.Initializable
import org.rs.consts.Components
import org.rs.consts.Items
import org.rs.consts.Quests
import org.rs.consts.Vars

/**
 * Represents the Scorpion Catcher quest.
 */
@Initializable
class ScorpionCatcher :
    Quest(Quests.SCORPION_CATCHER, 108, 107, 1, Vars.VARP_QUEST_SCORPION_CATCHER_PROGRESS_76, 0, 1, 6) {
    companion object {
        const val ATTRIBUTE_MIRROR = "/save:scorpion_catcher:start-talk"
        const val ATTRIBUTE_SECRET = "/save:scorpion_catcher:secret-room"
        const val ATTRIBUTE_NPC = "/save:scorpion_catcher:dialogues"
        const val ATTRIBUTE_CAGE = "/save:scorpion_catcher:cage-dialogue"
        const val ATTRIBUTE_TAVERLEY = "/save:scorpion_catcher:caught_taverley"
        const val ATTRIBUTE_BARBARIAN = "/save:scorpion_catcher:caught_barb"
        const val ATTRIBUTE_MONK = "/save:scorpion_catcher:caught_monk"
        const val ATTRIBUTE_THORMAC_TALK = "/save:scorpion_catcher:thormac_talk"
    }

    override fun drawJournal(
        player: Player,
        stage: Int,
    ) {
        super.drawJournal(player, stage)
        var line = 11

        /*
         * Requirements.
         */
        if (stage == 0) {
            line(player, "I can start this quest by speaking to !!Thormac?? who is in the", line++)
            line(player, "!!Sorcerer's Tower??, south-west of the !!Catherby??.", line++)
            line++
            line(player, "Requirements:", line++)
            line(player, if (hasLevelStat(player, Skills.PRAYER, 31)) "---Level 31 Prayer/--" else "!!Level 31 Prayer??", line++)
            line++
        }

        /*
         * First stage.
         */

        if (stage == 10) {
            line(player, "I've spoken to !!Thormac in the Sorcerer's Tower?? south-west", line++, true)
            line(player, "of !!Catherby??. He's lost his pet !!Kharid Scorpions?? and needs", line++, true)
            line(player, "my help to find them.", line++, true)
            line++

            /*
             * Ask about clue - Thormac.
             */

            if (getAttribute(player, ATTRIBUTE_THORMAC_TALK, false)) {
                line(player, "Thormac told me that I should speak with the !!Seer??,", line++, getAttribute(player, ATTRIBUTE_SECRET, false))
                line(player, "who is located !!north?? of the !!Sorcerers' Tower??.", line++, getAttribute(player, ATTRIBUTE_SECRET, false))
                line++
            }

            /*
             * Speak with Seers.
             */

            if (getAttribute(player, ATTRIBUTE_SECRET, false)) {
                line(player, "I've spoken to a !!Seer?? and been given the location of", line++, true)
                line(player, "one of the !!Kharid Scorpions??.", line++, true)
                line++
                line(player, "The first !!Kharid Scorpion?? is in a secret room near some", line++, getAttribute(player, ATTRIBUTE_TAVERLEY, false))
                line(player, "!!nasty spiders?? with two !!coffins?? nearby.", line++, getAttribute(player, ATTRIBUTE_TAVERLEY, false))
                line++
                line(player, "I'll need to talk to a !!Seer?? again once I've caught the first", line++, getAttribute(player, ScorpionCatcher.ATTRIBUTE_NPC, false))
                line(player, "!!Kharid Scorpion??.", line++, getAttribute(player, ScorpionCatcher.ATTRIBUTE_NPC, false))
                line++
            }

            /*
             * Speak with Seers v2.
             */

            if (getAttribute(player, ATTRIBUTE_NPC, false) || getAttribute(player, ATTRIBUTE_BARBARIAN, false)) {
                line(player, "The second !!Kharid Scorpion?? has been in a !!village of??", line++, getAttribute(player, ATTRIBUTE_BARBARIAN, false))
                line(player, "!!uncivilised-looking warriors in the east??. It's been picked up", line++, getAttribute(player, ATTRIBUTE_BARBARIAN, false))
                line(player, "by some sort of !!merchant??.", line++, getAttribute(player, ATTRIBUTE_BARBARIAN, false))
                line++
            }

            if (getAttribute(player, ATTRIBUTE_NPC, false) || getAttribute(player, ATTRIBUTE_MONK, false)) {
                line(player, "The third !!Kharid Scorpion?? is in some sort of !!upstairs room??", line++, getAttribute(player, ATTRIBUTE_MONK, false))
                line(player, "with !!brown clothing on a table??.", line++, getAttribute(player, ATTRIBUTE_MONK, false))
                line++
            }

            /*
             * Catch all scorpions.
             */

            if (getAttribute(player, ATTRIBUTE_TAVERLEY, false) && getAttribute(player, ATTRIBUTE_BARBARIAN, false) && getAttribute(player, ATTRIBUTE_MONK, false)) {
                line(player, "I need to take the !!Kharid Scorpions?? to !!Thormac??.", line++)
                line++
            }
        }

        if (stage == 100) {
            line(player, "I've spoken to !!Thormac?? and he thanked me", line++, true)
            line(player, "for finding his pet !!Kharid Scorpions??.", line++, true)
            line++
            line(player, "<col=FF0000>QUEST COMPLETE!</col>", line, false)
        }
    }

    override fun finish(player: Player) {
        super.finish(player)
        var ln = 10
        sendItemZoomOnInterface(player, Components.QUEST_COMPLETE_SCROLL_277, 5, Items.SCORPION_CAGE_460)
        drawReward(player, "1 Quest Point", ln++)
        drawReward(player, "6625 Strength XP", ln)
        rewardXP(player, Skills.STRENGTH, 6625.00)
        removeAttributes(player, ATTRIBUTE_SECRET, ATTRIBUTE_NPC, ATTRIBUTE_MIRROR, ATTRIBUTE_CAGE)
    }

    override fun newInstance(`object`: Any?): Quest = this
}
