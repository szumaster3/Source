import cacheops.cache.definition.data.MapTile
import cacheops.cache.definition.decoder.MapTileParser

fun Pair<Int,Int>.getSurroundingTiles() : List<MapTile> {
    val list = ArrayList<MapTile>()
    list.add(MapTileParser.definition.getTile(first, second + 1, MapEditor.plane))
    list.add(MapTileParser.definition.getTile(first + 1, second, MapEditor.plane))
    list.add(MapTileParser.definition.getTile(first, second - 1, MapEditor.plane))
    list.add(MapTileParser.definition.getTile(first - 1, second, MapEditor.plane))

    return list
}