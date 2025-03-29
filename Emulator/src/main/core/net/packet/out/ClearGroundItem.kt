package core.net.packet.out

import core.game.node.item.Item
import core.net.packet.IoBuffer
import core.net.packet.OutgoingPacket
import core.net.packet.context.BuildItemContext

/**
 * The type Clear ground item.
 */
class ClearGroundItem : OutgoingPacket<BuildItemContext> {
    override fun send(context: BuildItemContext) {
        val player = context.player
        val item = context.item
        val buffer = write(UpdateAreaPosition.getBuffer(player, item.location.chunkBase), item)
        buffer.cypherOpcode(context.player.session.isaacPair.output)
        player.session.write(buffer)
    }

    companion object {
        /**
         * Write io buffer.
         *
         * @param buffer the buffer
         * @param item   the item
         * @return the io buffer
         */
        @JvmStatic
        fun write(buffer: IoBuffer, item: Item): IoBuffer {
            val l = item.location
            buffer.put(240)
            buffer.putS((l.chunkOffsetX shl 4) or (l.chunkOffsetY and 0x7)).putShort(item.id)
            return buffer
        }
    }
}