package content.global.skill.summoning.item

import core.api.*
import core.game.container.Container
import core.game.node.entity.player.Player
import core.game.node.item.Item
import org.json.simple.JSONArray
import org.json.simple.JSONObject

class EnchantedHeadgearManager(private val player: Player) {

    val enchantedGear: MutableMap<Int, ChargedHeadgear> = mutableMapOf()

    /**
     * Adds scrolls to the enchanted headgear if valid.
     */
    fun addScrolls(chargedItemId: Int, scrollId: Int, amount: Int) {
        if (scrollId !in EnchantedHeadgearPlugin.allowedScrolls) {
            sendMessage(player, "You cannot add this scroll to the headgear.")
            return
        }

        val headgearData = EnchantedHeadgear.byCharged[chargedItemId] ?: return

        val capacity = headgearData.scrollCapacity ?: 50
        val chargedGear = enchantedGear.getOrPut(chargedItemId) { ChargedHeadgear(chargedItemId, Container(capacity)) }
        val container = chargedGear.container

        val existingScrollIds = container.toArray().filterNotNull().map { it.id }.toSet()
        if (existingScrollIds.any { it != scrollId }) {
            sendMessage(player, "Your headgear already contains different scrolls. Remove them first.")
            return
        }

        val freeSpace = container.freeSlots()
        if (freeSpace <= 0) {
            sendMessage(player, "Your headgear is already fully charged.")
            return
        }

        val inventoryCount = player.inventory.getAmount(scrollId)
        val toAdd = amount.coerceAtMost(freeSpace).coerceAtMost(inventoryCount)

        if (toAdd <= 0) {
            sendMessage(player, "You do not have any scrolls to add.")
            return
        }

        val scrollItem = Item(scrollId, toAdd)
        if (!player.inventory.remove(scrollItem)) {
            player.debug("Failed to remove scrolls from your inventory.")
            return
        }

        container.add(scrollItem)
        sendMessage(player, "You charge the headgear with $toAdd scroll${if (toAdd > 1) "s" else ""}.")
    }

    /**
     * Withdraw a specified amount of scrolls from the enchanted headgear.
     */
    fun withdrawScrolls(chargedItemId: Int, scrollId: Int, amount: Int): Boolean {
        val chargedGear = enchantedGear[chargedItemId] ?: return false
        val container = chargedGear.container
        val containedAmount = container.getAmount(scrollId)
        if (containedAmount < amount) return false

        container.remove(Item(scrollId, amount))
        container.shift()
        addItem(player, scrollId, amount)

        val isEmpty = container.toArray().all { it == null }
        if (isEmpty) {
            val headgear = EnchantedHeadgear.byCharged[chargedItemId] ?: return false

            val slot = player.inventory.getSlot(chargedItemId.asItem())
            if (slot == -1) {
                player.debug("Charged headgear not found in inventory.")
                return false
            }

            val newItem = Item(headgear.enchantedItem.id, 1)
            player.inventory.replace(newItem, slot)
            enchantedGear.remove(chargedItemId)
        }

        return true
    }

    /**
     * Withdraw all scrolls from the enchanted headgear.
     */
    fun withdrawAllScrolls(chargedItemId: Int) {
        val chargedGear = enchantedGear[chargedItemId] ?: return
        val container = chargedGear.container
        val scrolls = container.toArray().filterNotNull()

        scrolls.forEach { item ->
            container.remove(item)
            addItem(player, item.id, item.amount)
        }
        container.shift()

        sendMessages(
            player,
            "You remove the scrolls. You will need to use a Summoning scroll",
            "on it to charge the headgear up once more."
        )
    }

    /**
     * Checks if enchanted headgear contains any scrolls.
     */
    fun hasScrolls(chargedItemId: Int): Boolean {
        return enchantedGear[chargedItemId]?.container?.toArray()?.any { it != null } == true
    }

    /**
     * Gets the current amount of specific scrolls in the enchanted headgear.
     */
    fun getCurrentScrollCount(chargedItemId: Int, scrollId: Int): Int {
        return enchantedGear[chargedItemId]?.container?.getAmount(scrollId) ?: 0
    }

    /**
     * Gets total amount of scrolls player has (inventory + equipped enchanted headgear).
     */
    fun getTotalScrollCount(scrollId: Int): Int {
        val inventoryCount = player.inventory.getAmount(scrollId)
        val equippedHeadgearId = getFromEquipment()
        val headgearCount = equippedHeadgearId?.let { getCurrentScrollCount(it, scrollId) } ?: 0
        return inventoryCount + headgearCount
    }

    /**
     * Gets enchanted headgear item ID from player's equipment (head slot).
     */
    fun getFromEquipment(): Int? {
        val headSlotItem = player.equipment[EquipmentSlot.HEAD.ordinal]
        return headSlotItem?.takeIf { EnchantedHeadgear.byCharged.containsKey(it.id) }?.id
    }

    /**
     * Removes a single scroll from enchanted headgear.
     */
    fun removeScroll(chargedItemId: Int, scrollId: Int): Boolean {
        val chargedGear = enchantedGear[chargedItemId] ?: return false
        val container = chargedGear.container
        val amount = container.getAmount(scrollId)
        if (amount <= 0) return false
        container.remove(Item(scrollId, 1))
        container.shift()
        return true
    }

    /**
     * Sends player a message about current scrolls stored in the enchanted headgear.
     */
    fun checkHeadgear(chargedItemId: Int) {
        val chargedGear = enchantedGear[chargedItemId]
        if (chargedGear == null || chargedGear.container.toArray().all { it == null }) {
            sendMessage(player, "Your headgear holds no scrolls.")
            return
        }
        val message = chargedGear.container.toArray()
            .filterNotNull()
            .joinToString("\n") { item ->
                val scrollName = getItemName(item.id)
                val plural = if (item.amount == 1) "charge" else "charges"
                "The item contains ${item.amount} $plural of the $scrollName."
            }
        sendMessage(player, message)
    }

    /**
     * Clears all enchanted headgear data for the player.
     */
    fun clear() {
        enchantedGear.clear()
    }

    /**
     * Saves enchanted headgear scroll data into a JSON root object.
     */
    fun save(root: JSONObject) {
        val arr = JSONArray()
        enchantedGear.forEach { (_, chargedGear) ->
            val obj = JSONObject().apply {
                put("chargedItemId", chargedGear.chargedItemId.toString())
                put("scrolls", JSONArray().apply {
                    chargedGear.container.toArray().forEach { item ->
                        if (item != null) {
                            add(JSONObject().apply {
                                put("id", item.id.toString())
                                put("amount", item.amount.toString())
                            })
                        }
                    }
                })
            }
            arr.add(obj)
        }
        root["enchanted_headgear"] = arr
    }

    /**
     * Parses enchanted headgear scroll data from JSON array.
     */
    fun parse(data: JSONArray) {
        enchantedGear.clear()
        data.forEach { element ->
            val obj = element as JSONObject
            val chargedItemId = obj["chargedItemId"].toString().toInt()
            val scrollsArr = obj["scrolls"] as JSONArray
            val capacity = EnchantedHeadgear.byCharged[chargedItemId]?.scrollCapacity ?: 50
            val container = Container(capacity)

            scrollsArr.forEach { s ->
                val scrollObj = s as JSONObject
                val id = scrollObj["id"].toString().toInt()
                val amount = scrollObj["amount"].toString().toInt()
                container.add(Item(id, amount))
            }
            enchantedGear[chargedItemId] = ChargedHeadgear(chargedItemId, container)
        }
    }

    data class ChargedHeadgear(
        val chargedItemId: Int,
        val container: Container,
    )
}
