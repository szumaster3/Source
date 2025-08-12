package content.region.fremennik.rellekka.dialogue

import content.region.fremennik.plugin.FremennikShipHelper.sail
import content.region.fremennik.plugin.Travel
import core.api.*
import core.api.isQuestComplete
import core.game.dialogue.Dialogue
import core.game.dialogue.FaceAnim
import core.game.dialogue.Topic
import core.game.node.entity.npc.NPC
import core.game.node.entity.player.Player
import core.game.node.item.Item
import core.plugin.Initializable
import shared.consts.Items
import shared.consts.NPCs
import shared.consts.Quests

/**
 * Represents the Jarvald dialogue.
 * (Varbit 814, Wrapper 2345)
 */
@Initializable
class JarvaldDialogue(player: Player? = null) : Dialogue(player) {

    override fun open(vararg args: Any?): Boolean {
        npc = args[0] as NPC
        if(npc.id == NPCs.JARVALD_2438) {
            npcl(FaceAnim.HALF_ASKING, "Ah, you live yet, outerlander! Have you had your fill of the hunt and wish to return, or are you still feeling the joy of the cull?").also { stage = 27 }
        } else {
            npc(FaceAnim.ANNOYED, "What do you want from me outerlander?", "It is our policy not to associate with those not of our tribe.")
        }
        return true
    }

    override fun handle(interfaceId: Int, buttonId: Int): Boolean {
        val firstInteraction = getVarbit(player, 814)
        when (stage) {
            0 -> showTopics(
                Topic("Where is your chieftain?", 1, true),
                Topic("What Jarvald is doing.", 5, true),
                Topic("Nothing.", 30, true),
            )
            1 -> playerl(FaceAnim.HALF_ASKING,"Where is your chieftain? I find it highly discriminatory to refuse to talk to someone on the grounds that they are not part of your tribe.").also { stage++ }
            2 -> npcl(FaceAnim.NEUTRAL, "I don't rightly understand your speech outerlander, but my loyalty is with Chieftain Brundt.").also { stage++ }
            3 -> npcl(FaceAnim.NEUTRAL, "He resides in our long hall; it is the large building over there, you should speak to him for he speaks for us all.").also { stage++ }
            4 -> end()

            5 -> player(FaceAnim.HALF_ASKING, "So what are you doing here?").also { stage++ }
            6 -> npcl(FaceAnim.NEUTRAL, "This should not concern you, outerlander. I am awaiting other Fremenniks to join me on an expedition to Waterbirth Island.").also { stage++ }
            7 -> showTopics(
                Topic("Waterbirth Island?", 8),
                Topic("Can I come?", if(firstInteraction == 1) 19 else 11),
                Topic("Nice hat!", 24, true),
                Topic("Ok, 'bye.", 31, true),
            )
            8 -> npcl(FaceAnim.NEUTRAL,"It is a small crescent-shaped island just north-west of here, outerlander.").also {stage++ }
            9 -> npcl(FaceAnim.FRIENDLY, "We have many legends about it, such as the tale of the broken sky, and the day of the green seas. The reason I am travelling there is more serious Fremennik business, however.").also {stage++ }
            10 -> npcl(FaceAnim.NEUTRAL, "I doubt an outerlander would be interested.").also { stage++ }
            11 -> playerl(FaceAnim.HALF_THINKING,"Hey, scary barbarian type guy, think I can join you on this expedition?").also {
                setVarbit(player, 814, 1, true)
                stage++
            }
            12 -> npcl(FaceAnim.THINKING,"An outerlander join us on an honoured hunt??? Well.... I guess... I might be able to allow you to join us, although it is a breach of many of our customs...").also { stage++ }
            13 -> player(FaceAnim.SAD,"Oh, pleeeeease? I really LOVE killing stuff!").also { stage++ }
            14 -> npcl(FaceAnim.FRIENDLY,"Well... I remain unconvinced that it would be wise to allow an outerlander to join us in such dangerous battle, but your enthusiasm seems genuine enough...").also { stage++ }
            15 -> npcl(FaceAnim.NEUTRAL,"I will allow you to escort us, but you must pay me a sum of money first.").also { stage++ }
            16 -> playerl(FaceAnim.HALF_ASKING,"What? That's outrageous, why charge me money? And, uh, how much does it cost me?").also { stage++ }
            17 -> npcl(FaceAnim.NEUTRAL,"Ah, the outerlanders have stolen from my people for many years, in this way you can help my community with a small amount of money... Let us say... 1000 coins. Payable in advance, of course.").also { stage++ }
            18 -> npcl(FaceAnim.NEUTRAL,"For this I will take you to Waterbirth Island on my boat, and will bring you back here when you have had your fill of the hunt. Assuming you are still alive to wish to leave, of course.").also { stage++ }
            19 -> npc(FaceAnim.HALF_ASKING, "So do you have the 1000 coins for my service, and are you ready to leave now?").also { stage++ }
            20 -> options("YES", "NO").also { stage++ }
            21 -> when(buttonId){
                1 -> {
                    if (!player!!.inventory.contains(Items.COINS_995, 1000)) {
                        sendMessage(player, "You cannot afford that.")
                        npcl(FaceAnim.NEUTRAL, "Ah, outerlander... I go beyond my duty to allow you to accompany me, and you attempt to swindle me out of a reasonable fee? This is no way even for an outerlander to behave...")
                        stage = 100
                    } else {
                        player.inventory.remove(Item(Items.COINS_995, 1000))
                        sail(player, Travel.RELLEKKA_TO_WATERBIRTH)
                        if (!isQuestComplete(player, Quests.THE_FREMENNIK_TRIALS)) {
                            npcl(FaceAnim.NEUTRAL, "I suggest you head to the cave with some urgency outerlander, the cold air out here might be too much for the likes of you...")
                        } else {
                            npcl(FaceAnim.NEUTRAL, "I would head straight for the cave and not tarry too long ${player.username}, the cold winds on this island can cut right through you should you spend too long in them.")
                        }
                        end()
                    }
                }
                2 -> playerl(FaceAnim.HALF_GUILTY, "No, actually I have some stuff to do here first.").also { stage++ }
            }
            22 -> npcl(FaceAnim.HALF_GUILTY,"As you wish. Come and see me when your bloodlust needs sating.").also { stage++ }
            23 -> end()

            24 -> playerl(FaceAnim.FRIENDLY, "Hey, I have to say, that's a fine looking hat you are wearing there.").also { stage++ }
            25 -> npcl(FaceAnim.NEUTRAL, "It is actually a helm, outerlander, but the sentiment is appreciated nonetheless.").also { stage++ }
            26 -> end()

            27 -> npc(FaceAnim.NEUTRAL, "Have you had your fill of the hunt and wish to return,", "or are you still feeling the joy of the cull?").also { stage++ }
            28 -> sendDialogueOptions(player, "Leave island?", "YES", "NO").also { stage++ }
            29 -> when (buttonId) {
                1 -> {
                    sail(player, Travel.WATERBIRTH_TO_RELLEKKA)
                    npcl(FaceAnim.FRIENDLY, if (isQuestComplete(player, Quests.THE_FREMENNIK_TRIALS)) {
                        "Of course, " + player.username + "! Your presence is more than welcome on this cull! You wish to leave now?"
                    } else {
                        "So do you have the 1000 coins for my service, and are you ready to leave?"
                    })
                }
                2 -> end()
            }
            30 -> playerl(FaceAnim.HALF_GUILTY, "Actually, I don't think I have anything to speak to you about...").also { stage = 31 }
            31 -> playerl(FaceAnim.HALF_GUILTY, "Wow, you Fremenniks sure know how to party. Well, see ya around.").also { stage++ }
            32 -> end()
        }
        return true
    }

    override fun newInstance(player: Player?): Dialogue = JarvaldDialogue(player)

    override fun getIds(): IntArray = intArrayOf(2435, NPCs.JARVALD_2438)
}