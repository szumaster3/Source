package core.net.packet.out;

import core.game.world.map.Location;
import core.net.packet.IoBuffer;
import core.net.packet.OutgoingPacket;
import core.net.packet.context.ClearChunkContext;

/**
 * The type Clear region chunk.
 */
public final class ClearRegionChunk implements OutgoingPacket<ClearChunkContext> {

    @Override
    public void send(ClearChunkContext context) {
        Location l = context.getPlayer().getPlayerFlags().getLastSceneGraph();
        int x = context.getChunk().getCurrentBase().getSceneX(l);
        int y = context.getChunk().getCurrentBase().getSceneY(l);
        if (x >= 0 && y >= 0 && x < 96 && y < 96) {
            IoBuffer buffer = new IoBuffer(112).put(x).putC(y);
            buffer.cypherOpcode(context.getPlayer().getSession().getIsaacPair().output);
            context.getPlayer().getSession().write(buffer);
        }
    }

}
