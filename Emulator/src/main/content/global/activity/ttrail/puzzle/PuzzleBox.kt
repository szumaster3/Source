package content.global.activity.ttrail.puzzle

import core.api.getAttribute
import core.game.node.entity.player.Player
import core.game.node.item.Item
import org.rs.consts.Items

/**
 * Represents a sliding puzzle boxes.
 *
 * @property key A puzzle type.
 * @property item The puzzle box item id.
 * @property solutionTiles The puzzle pieces.
 */
enum class PuzzleBox(
    val key: String,
    val item: Item,
    val solutionTiles: List<Int>
) {
    TROLL("troll", Item(Items.PUZZLE_BOX_3571), (3643..3666).toList()),
    CASTLE("castle", Item(Items.PUZZLE_BOX_2795), (2749..2772).toList()),
    TREE("tree", Item(Items.PUZZLE_BOX_3565), (3619..3642).toList());

    companion object {
        /**
         * Retrieves a [PuzzleBox] by its string key.
         *
         * @param key The puzzle type (e.g., "castle").
         * @return The corresponding [PuzzleBox], or `null` if not found.
         */
        @JvmStatic
        fun fromKey(key: String): PuzzleBox? = values().find { it.key == key }

        /**
         * Retrieves a [PuzzleBox] by the associated item id.
         *
         * @param itemId the puzzle box item id.
         * @return The corresponding [PuzzleBox], or `null` if not found.
         */
        @JvmStatic
        fun fromItemId(itemId: Int): PuzzleBox? = values().find { it.item.id == itemId }

        /**
         * Gets random puzzle item.
         */
        @JvmStatic
        fun getRandomPuzzleBox(): Int? {
            val randomPuzzleBox = PuzzleBox.values().random()
            return PuzzleBox.fromItemId(randomPuzzleBox.item.id)?.item?.id
        }

        /**
         * Checks if the player has completed the puzzle.
         *
         * @param player The player to check.
         * @param key The key identifying the puzzle type.
         * @return `true` if the puzzle is completed and player has a puzzle box item, `false` otherwise.
         */
        @JvmStatic
        fun hasCompletePuzzleBox(player: Player, key: String): Boolean {
            val box = PuzzleBox.fromKey(key) ?: return false
            return getAttribute(player, "$key:puzzle:done", false) && player.inventory.contains(box.item.id, 1)
        }
    }

    /**
     * The full solution of the puzzle, consisting of all solution tiles plus a blank tile (`-1`) at the end.
     */
    val fullSolution: List<Int> = solutionTiles + -1
}