package ui

import EditorState
import MapEditor
import TableData
import cacheops.cache.definition.data.MapTile
import cacheops.cache.definition.decoder.MapTileParser
import cacheops.cache.definition.decoder.Scenery
import const.Image
import tools.Util
import java.awt.*
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.SwingUtilities

class MapCell : JPanel() {
    var defaultBackground: Color = background
    val scenery = ArrayList<Scenery>()
    override fun getPreferredSize(): Dimension {
        return Dimension(MapEditor.cellX, MapEditor.cellY)
    }
    init {

        //add(underlayLabel)
        addMouseListener(object : MouseAdapter() {
            override fun mouseEntered(e: MouseEvent) {
                MapEditor.selectedPointX = e.component.bounds.location.x / MapEditor.cellX
                MapEditor.selectedPointY = e.component.bounds.location.y / MapEditor.cellY
                MapEditor.statusLabel.foreground = Color.YELLOW
                MapEditor.statusLabel.text = "<html>Region: ${MapEditor.region} | " +
                        "Local Coordinates: [${MapEditor.selectedPointX}, ${MapEditor.selectedPointY}] | " +
                        "Global Coordinates: [${MapTileParser.coordinateX(MapEditor.selectedPointX)}, ${
                            MapTileParser.coordinateX(
                                MapEditor.selectedPointY
                            )
                        }]"
                defaultBackground = background
                if(MapEditor.state == EditorState.SET_UNDERLAY || MapEditor.state == EditorState.NONE) {
                    background = Color.BLUE
                } else if (MapEditor.state == EditorState.ADD_NPC){
                    background = Color.YELLOW
                } else if (MapEditor.state == EditorState.ADD_GROUNDITEM){
                    background = Color.RED
                }
            }

            override fun mouseExited(e: MouseEvent) {
                background = defaultBackground
            }

            override fun mouseClicked(e: MouseEvent) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    if (MapEditor.selectedUnderlayId != null && MapEditor.state == EditorState.SET_UNDERLAY) {
                        val originalTile = MapTileParser.definition.getTile(
                            MapEditor.selectedPointX,
                            MapEditor.selectedPointY, 0)
                        val underlayID = MapEditor.selectedUnderlayId
                        val point = Point(MapEditor.selectedPointX, MapEditor.selectedPointY)
                        val oldValue = MapEditor.colorPointMap[point]
                        MapEditor.colorPointMap[point] = underlayID!!
                        println("UPDATED UNDERLAY VALUES @ [${point.x}, ${point.y}] [$oldValue >> ${MapEditor.colorPointMap[point]}]")
                        defaultBackground = MapEditor.underlayMap[MapEditor.selectedUnderlayId]?.getRGB()!!
                        background = MapEditor.underlayMap[MapEditor.selectedUnderlayId]?.getRGB()

                        // Update map tile cacheops.definitions with new tile information
                        MapTileParser.definition.setTile(point.x, point.y, MapEditor.plane,
                            MapTile(
                                originalTile.height,
                                originalTile.attrOpcode,
                                originalTile.overlayId,
                                originalTile.overlayPath,
                                originalTile.overlayRotation,
                                originalTile.settings,
                                underlayID
                            )
                        )
                    }
                    else if (MapEditor.npcIdInput.text.isNotEmpty() && MapEditor.state == EditorState.ADD_NPC){
                        val id = MapEditor.npcIdInput.text.toIntOrNull()
                        if(id == null){
                            MapEditor.state = EditorState.NONE
                            return
                        }

			val canWalk = MapEditor.npcCanWalkCheckbox.isSelected()
			var spawnDirection = 0
			for ((index, checkbox) in MapEditor.npcDirectionCheckboxes.withIndex()) {
			    if (checkbox.isSelected()) { spawnDirection = index; break }	
			}
			val absolute = Util.getAbsoluteCoordinates(MapEditor.region, MapEditor.selectedPointX, MapEditor.selectedPointY, MapEditor.plane)
			val npcSpawn = TableData.NPCSpawn(id, absolute, canWalk, spawnDirection)
                        MapEditor.npcs!!.add(npcSpawn)
                        MapEditor.componentPointMap.filter { it.key.x == MapEditor.selectedPointX && it.key.y == MapEditor.selectedPointY }.forEach { (_, cell) ->
                            var hasLabel = false
                            cell.components.forEach { if(it is JLabel && it.icon == Image.YELLOW_DOT) hasLabel = true }
                            cell.layout = FlowLayout()
                            if(!hasLabel) cell.add(JLabel(Image.YELLOW_DOT))
                            cell.repaint()
                        }
                        MapEditor.npcPanel.redrawRows()
                        MapEditor.npcsUpdated = true
                    }
                    else if (MapEditor.itemIdInput.text.isNotEmpty() && MapEditor.state == EditorState.ADD_GROUNDITEM){
                        val id = MapEditor.itemIdInput.text.toIntOrNull()
                        val amount = MapEditor.itemAmountInput.text.toIntOrNull() ?: 1
                        val respawnTime = MapEditor.itemRespawnInput.text.toIntOrNull() ?: 1
                        if(id == null){
                            MapEditor.state = EditorState.NONE
                            return
                        }

			val absolute = Util.getAbsoluteCoordinates(MapEditor.region, MapEditor.selectedPointX, MapEditor.selectedPointY, MapEditor.plane)
                        val item = TableData.ItemSpawn(
                            id,
			    absolute,
                            amount,
                            respawnTime
                        )
                        MapEditor.items!!.add(item)
                        MapEditor.componentPointMap.filter { it.key.x == MapEditor.selectedPointX && it.key.y == MapEditor.selectedPointY }.forEach { (_, cell) ->
                            var hasLabel = false
                            cell.components.forEach { if(it is JLabel && it.icon == Image.RED_DOT) hasLabel = true }
                            cell.layout = FlowLayout()
                            if(!hasLabel) cell.add(JLabel(Image.RED_DOT))
                            cell.repaint()
                        }
                        MapEditor.itemPanel.redrawRows()
                        MapEditor.itemsUpdated = true
                    }
                } else if (SwingUtilities.isRightMouseButton(e)) {
                    val point = Point(MapEditor.selectedPointX, MapEditor.selectedPointY)
                    val def = MapTileParser.definition.getTile(point.x, point.y, MapEditor.plane)
                    val floDef = MapEditor.overlayMap[def.overlayId]!!
                    val cell = MapEditor.componentPointMap[point]!!
		    MapEditor.state = EditorState.NONE
                    val string = StringBuilder()
                        .append("<h2><b>Tile [${point.x}, ${point.y}] Information</b></h2><br>")
                        .append("<hr width=\"250px\"><br>")
                        .append("<table style=\"border:1px solid gray;margin-left:auto;margin-right:auto;>")
                        .append("<thead>")
                        .append("<tr style=\"text-align:center\">")
                        .append("<th>UnderlayID</th>")
                        .append("<th>Height</th>")
                        .append("<th>Settings</th>")
                        .append("</tr>")
                        .append("</thead>")
                        .append("<tbody>")
                        .append("<tr style=\"text-align:center\">")
                        .append("<td>${def.underlayId}</td>")
                        .append("<td>${def.height}</td>")
                        .append("<td>${def.settings}</td>")
                        .append("</tr>")
                        .append("</tbody>")
                        .append("</table>")
                        .append("<table style=\"border:1px solid gray;margin-left:auto;margin-right:auto;>")
                        .append("<thead>")
                        .append("<tr style=\"text-align:center\">")
                        .append("<th>OverlayID</th>")
                        .append("<th>Color</th>")
                        .append("<th>Hide underlay</th>")
                        .append("</tr>")
                        .append("</thead>")
                        .append("<tbody>")
                        .append("<tr style=\"text-align:center\">")
                        .append("<td>${def.overlayId}</td>")
                        .append("<td>${floDef.color}</td>")
                        .append("<td>${floDef.hideUnderlay}</td>")
                        .append("</tr>")
                        .append("</tbody>")
                        .append("</table>")
                        .append("<br/>")
                    val thisAbsolute = Util.getAbsoluteCoordinates(MapEditor.region, MapEditor.selectedPointX, MapEditor.selectedPointY, MapEditor.plane)
                    val itemsHere = MapEditor.items!!.filter { it.location == thisAbsolute }.toList()
                    val npcsHere = MapEditor.npcs!!.filter { it.location == thisAbsolute }.toList()

                    if(npcsHere.isNotEmpty()) {
                        string.append("<table style=\"border:1px solid gray;margin-left:auto;margin-right:auto;>")
                            .append("<thead>")
                            .append("<tr style=\"text-align:center\">")
                            .append("<th>NPC ID</th>")
                            .append("<th>Name</th>")
                            .append("</tr>")
                            .append("</thead>")
                            .append("<tbody>")
                        for (npc in npcsHere) {
                            string.append("<tr style=\"text-align:center\">")
                            string.append("<td>${npc.id}</td>")
                            string.append("<td>${TableData.getNPCName(npc.id)}</td>")
                            string.append("</tr>")
                        }
                        string.append("</tbody></table><br/>")
                    }

                    if(itemsHere.isNotEmpty()) {
                        string.append("<table style=\"border:1px solid gray;margin-left:auto;margin-right:auto;>")
                            .append("<thead>")
                            .append("<tr style=\"text-align:center\">")
                            .append("<th>Item ID</th>")
                            .append("<th>Info</th>")
                            .append("</tr>")
                            .append("</thead>")
                            .append("<tbody>")
                        for (item in itemsHere) {
                            string.append("<tr style=\"text-align:center\">")
                            string.append("<td>${item.id}</td>")
                            string.append("<td>${TableData.getItemName(item.id, false)}[${item.amount}] \uD83D\uDD64${item.respawnTicks}</td>")
                            string.append("</tr>")
                        }
                        string.append("</tbody></table><br/>")
                    }

                    if(scenery.isNotEmpty()) {
                        string.append("<h3>Scenery Info</h3>")
                        for(sc in scenery) {
                            if (sc.id != -1) {
                                string.append("${sc?.definition?.name} [ID: ${sc?.id}]<br/>")
                                string.append("Type: ${sc?.type}<br/>")
                                string.append("Rotation: ${sc?.rotation}<br/>")
                                string.append("---------------------<br/>")
                            }
                        }
                    }

                    MapEditor.underlayInfo.text = String.format("<html><body style=\"text-align: center;\">%s</body></html>", string.toString())
                    MapEditor.infoPane.selectedIndex = 0
                }
            }
        })
    }

    fun flagScenery(scenery: Scenery){
        this.scenery.add(scenery)
    }

    override fun paintComponent(g: Graphics) {
        if(this.defaultBackground == Color(0,0,0,0)) return
        super.paintComponent(g)
        for(sc in scenery) {
            var pointX1 = 0
            var pointY1 = 0
            var pointX2 = 0
            var pointY2 = 0
            val color = g.color
            val rotationOdd = sc.rotation % 2 != 0
            when (sc.type) {
                10 -> g.drawOval(5, 5, this.width - 10, this.height - 10)
                4 -> {
                    val offSetX =
                        if (!rotationOdd) (sc.rotation * 0.375 * width).toInt() else (0.35 * width).toInt()
                    val offSetY =
                        if (!rotationOdd) (0.65 * height).toInt() else (sc.rotation * 0.375 * height).toInt()
                    g.drawString("=", offSetX, offSetY)
                }
                11 -> {
                    g.color = Color(255, 255, 255, 75)
                    g.drawString("⛝", (0.25 * width).toInt(), (0.75 * height).toInt())
                    g.color = color
                }
                22 -> {
                    g.color = Color(255, 255, 255, 55)
                    g.drawString("⛆", (0.15 * width).toInt(), (0.65 * height).toInt())
                    g.color = color
                }
                0, 5 -> {
                    if (sc.id == 85) return
                    when (sc.rotation) {
                        0 -> {
                            pointX1 = 1; pointY1 = this.height - 1; pointX2 = 1; pointY2 = 1
                        }
                        1 -> {
                            pointX1 = 1; pointY1 = 1; pointX2 = this.width - 1; pointY2 = 1
                        }
                        2 -> {
                            pointX1 = this.width - 1; pointY1 = 1; pointX2 = this.width - 1; pointY2 = this.height - 1
                        }
                        3 -> {
                            pointX1 = 1; pointY1 = this.height - 1; pointX2 = this.width - 1; pointY2 = this.height - 1
                        }
                    }
                    if (sc?.definition?.interactive == 1 && sc?.type == 0) {
                        g.color = Color.RED
                    }
                    g.drawLine(pointX1, pointY1, pointX2, pointY2)
                    g.color = color
                }

                2 -> {
                    val nPoints = 4
                    var xArray: IntArray = intArrayOf(0, 0, 0, 0)
                    var yArray: IntArray = intArrayOf(0, 0, 0, 0)
                    when (sc.rotation) {
                        0 -> {
                            xArray = intArrayOf(1, 1, 1, this.width - 1); yArray = intArrayOf(1, this.height - 1, 1, 1)
                        }
                        1 -> {
                            xArray = intArrayOf(1, this.width - 1, this.width - 1, this.width - 1); yArray = intArrayOf(1, 1, 1, this.height - 1)
                        }
                        2 -> {
                            xArray = intArrayOf(1, this.width - 1, this.width - 1, this.width - 1); yArray = intArrayOf(this.height - 1, this.height - 1, 1, this.height - 1)
                        }
                        3 -> {
                            xArray = intArrayOf(1,1,1,this.width - 1); yArray = intArrayOf(1,this.height - 1, this.height - 1, this.height - 1)
                        }
                    }
                    if (sc?.definition?.interactive == 1) {
                        g.color = Color.RED
                    }
                    g.drawPolyline(xArray, yArray, nPoints)
                    g.color = color
                }

                9, 7, 6, 8 -> {
                    if (sc.id == 85 || sc.id == 83) return
                    when (sc.rotation) {
                        0, 2 -> {
                            pointX1 = 0; pointY1 = height; pointX2 = width; pointY2 = 0
                        }
                        1, 3 -> {
                            pointX1 = 0; pointY1 = 0; pointX2 = width; pointY2 = height
                        }
                    }

                    val overlayNorth = getOverlayId(sc.x + 1, sc.y)
                    val overlaySouth = getOverlayId(sc.x - 1, sc.y)
                    val northWest = getOverlayId(sc.x - 1, sc.y + 1)
                    val north = getOverlayId(sc.x, sc.y + 1)
                    val northEast = getOverlayId(sc.x + 1, sc.y + 1)
                    val east = getOverlayId(sc.x + 1, sc.y)
                    val southWest = getOverlayId(sc.x - 1, sc.y - 1)
                    val south = getOverlayId(sc.x, sc.y - 1)
                    val southEast = getOverlayId(sc.x + 1, sc.y - 1)
                    val west = getOverlayId(sc.x - 1, sc.y)

                    var backFillVertice = Pair(0, 0)
                    when (sc.rotation) {
                        0, 2 -> {
                            val sumA = getOverlaySum(north, northWest, west)
                            val sumB = getOverlaySum(south, southEast, east)
                            backFillVertice = if (sumA > sumB) Pair(width, height)
                            else if (sumA == sumB) Pair(-1, -1)
                            else Pair(0, 0)
                        }
                        1, 3 -> {
                            val sumA = getOverlaySum(north, northEast, east)
                            val sumB = getOverlaySum(south, southWest, west)
                            backFillVertice = if (sumA > sumB) Pair(0, height)
                            else if (sumA == sumB) Pair(-1, -1)
                            else Pair(width, 0)
                        }
                    }

                    val fluDef = getUnderlayColor(sc.x, sc.y)
                    g.color = fluDef
                    if (overlaySouth != overlayNorth && backFillVertice.first != -1)
                        g.fillPolygon(
                            intArrayOf(pointX1, pointX2, backFillVertice.first),
                            intArrayOf(pointY1, pointY2, backFillVertice.second),
                            3
                        )
                    g.color = color
                    g.drawLine(pointX1, pointY1, pointX2, pointY2)
                }
            }
        }
    }

    fun getOverlaySum(vararg overlays: Int): Int{
        var sum = 0
        for(overlay in overlays){
            sum += overlay.coerceAtMost(1)
        }
        return sum
    }

    fun getUnderlayColor(x: Int, y: Int): Color {
        var underlayID = (MapTileParser.definition.getTile(x, y, MapEditor.plane).underlayId - 1)
        if (underlayID < 0) {
            underlayID = 0
        }
        return MapEditor.underlayMap[underlayID]?.getRGB() ?: Color.BLACK
    }

    fun getOverlayId(x: Int, y: Int): Int {
        return MapTileParser.definition.getTile(x, y, MapEditor.plane).overlayId
    }
}
