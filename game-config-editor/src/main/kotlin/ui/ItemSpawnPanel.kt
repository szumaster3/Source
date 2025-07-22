package ui

import const.Image
import misc.ImgButton
import java.awt.BorderLayout
import java.awt.Color
import java.awt.Dimension
import java.awt.FlowLayout
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.BoxLayout
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.border.MatteBorder

class ItemSpawnPanel : JPanel() {
    val rowBorder = MatteBorder(1, 1, 1, 1, Color.WHITE)
    init {
        layout = BoxLayout(this, BoxLayout.PAGE_AXIS)

        add(ItemSpawnerPanel())

        MapEditor.items!!.filter { it.location.z == MapEditor.plane }.forEach {
            val row = ItemRow(it, this)
            row.border = rowBorder
            MapEditor.itemRows.add(row)
            add(row)
        }
    }

    fun redrawRows() {
        MapEditor.itemRows.forEach {
            remove(it)
        }
        MapEditor.itemRows.clear()
        MapEditor.items!!.filter { it.location.z == MapEditor.plane }.forEach {
            val row = ItemRow(it, this)
            row.border = rowBorder
            MapEditor.itemRows.add(row)
            add(row)
        }
        repaint()
    }

    class ItemSpawnerPanel : JPanel() {
        init {
            val topPanel = JPanel(FlowLayout())
            val bottomPanel = JPanel(FlowLayout())
            val addButton = ImgButton(Image.ADD_HI, Image.ADD_LO)
            addButton.onClick {
                MapEditor.state = EditorState.ADD_GROUNDITEM
            }
            MapEditor.itemIdInput.minimumSize = Dimension(200, 25)
            MapEditor.itemIdInput.maximumSize = Dimension(200, 25)
            MapEditor.itemIdInput.preferredSize = Dimension(200, 25)

            MapEditor.itemRespawnInput.minimumSize = Dimension(50, 25)
            MapEditor.itemRespawnInput.maximumSize = Dimension(50, 25)
            MapEditor.itemRespawnInput.preferredSize = Dimension(50, 25)
            MapEditor.itemRespawnInput.text = "1"

            MapEditor.itemAmountInput.minimumSize = Dimension(75, 25)
            MapEditor.itemAmountInput.maximumSize = Dimension(75, 25)
            MapEditor.itemAmountInput.preferredSize = Dimension(75, 25)
            MapEditor.itemAmountInput.text = "1"

            val timeLabel = JLabel("\uD83D\uDD64")
            timeLabel.toolTipText = "Respawn Time (ticks)"

            minimumSize = Dimension(300, 68)
            preferredSize = Dimension(300, 68)
            maximumSize = Dimension(300, 68)

	    MapEditor.itemIdInput.addMouseListener (object : MouseAdapter() {
		override fun mouseClicked (e: MouseEvent) {
		    ItemMenu.caller = {id, name -> 
			MapEditor.itemIdInput.text = id.toString()
		    }
		    ItemMenu.open()
		}
	    })

            layout = BorderLayout()
            topPanel.add(MapEditor.itemIdInput)
            topPanel.add(addButton)
            bottomPanel.add(timeLabel)
            bottomPanel.add(MapEditor.itemRespawnInput)
            bottomPanel.add(JLabel("AMT:"))
            bottomPanel.add(MapEditor.itemAmountInput)
            add(topPanel,BorderLayout.NORTH)
            add(bottomPanel,BorderLayout.SOUTH)
            border = MatteBorder(1, 1, 1, 1, Color.GREEN)
        }
    }

    class ItemRow(val item: TableData.ItemSpawn, val parent: JPanel) : JPanel(){
        init {
            layout = BorderLayout()
	    val topPanel = JPanel(FlowLayout())
	    val midPanel = JPanel(FlowLayout())
	    val botPanel = JPanel(FlowLayout())

            val deleteButton = ImgButton(Image.DELETE_HI, Image.DELTE_LO)
	    topPanel.add(JLabel("${TableData.getItemName(item.id, false)} [${item.id}]"))
	    topPanel.add(deleteButton)
	    midPanel.add(JLabel("{${item.location.x},${item.location.y},${item.location.z}}"))
	    botPanel.add(JLabel("AMT: ${item.amount} \uD83D\uDD64 ${item.respawnTicks}"))

	    add(topPanel, BorderLayout.NORTH)
	    add(midPanel, BorderLayout.CENTER)
	    add(botPanel, BorderLayout.SOUTH)

            minimumSize = Dimension(300, 80)
            preferredSize = Dimension(300, 80)
            maximumSize = Dimension(300, 80)

            deleteButton.onClick {
                MapEditor.items!!.remove(item)
                parent.remove(this)
                parent.repaint()
                MapEditor.itemRows.remove(this)
                if(MapEditor.items!!.filter { it.location == item.location }.isEmpty()){
                    MapEditor.componentPointMap.filter { it.key.x == item.location.localCoords[0] && it.key.y == item.location.localCoords[1] }.forEach { (_,cell) ->
                        cell.components.forEach { c ->
                            if(c is JLabel && c.icon == Image.RED_DOT) cell.remove(c)
                        }
                        cell.repaint()
                        MapEditor.itemsUpdated = true
                    }
                }
            }

            addMouseListener(object : MouseAdapter(){
                val border = MatteBorder(1, 1, 1, 1, Color.YELLOW)
                val defaultBorder = MatteBorder(1, 1, 1, 1, Color.GRAY)
                override fun mouseEntered(e: MouseEvent?) {
                    super.mouseEntered(e)
                    MapEditor.componentPointMap.filter { it.key.x == item.location.localCoords[0] && it.key.y == item.location.localCoords[1] }.forEach { it.value.border = border }
                }

                override fun mouseExited(e: MouseEvent?) {
                    super.mouseExited(e)
                    MapEditor.componentPointMap.filter { it.key.x == item.location.localCoords[0] && it.key.y == item.location.localCoords[1] }.forEach{ it.value.border = defaultBorder }
                }
            })
        }
    }
}
