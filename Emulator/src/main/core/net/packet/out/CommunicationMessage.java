package core.net.packet.out;

import core.game.bots.AIPlayer;
import core.game.node.entity.player.Player;
import core.game.system.communication.ClanRepository;
import core.net.packet.IoBuffer;
import core.net.packet.OutgoingPacket;
import core.net.packet.PacketHeader;
import core.net.packet.context.MessageContext;
import core.tools.StringUtils;

import java.util.Random;

/**
 * The type Communication message.
 */
public final class CommunicationMessage implements OutgoingPacket<MessageContext> {

    private static int getMessageIndex(Player p) {
        int count = p.getAttribute("pm_index", 0) + 1;
        p.setAttribute("pm_index", count);
        return count;
    }

    @Override
    public void send(MessageContext context) {
        IoBuffer buffer = new IoBuffer(context.getOpcode(), PacketHeader.BYTE);
        String message = context.getMessage();
        Player player = context.getPlayer();
        String other = context.getOther();
        switch (context.getOpcode()) {
            case MessageContext.SEND_MESSAGE:
                byte[] bytes = new byte[256];
                int length = StringUtils.encryptPlayerChat(bytes, 0, 0, message.length(), message.getBytes());
                buffer.putLong(StringUtils.stringToLong(other));
                buffer.put((byte) message.length());
                buffer.putBytes(bytes, 0, length);
                break;
            case MessageContext.RECEIVE_MESSAGE:
                bytes = new byte[256];
                bytes[0] = (byte) message.length();
                length = 1 + StringUtils.encryptPlayerChat(bytes, 0, 1, message.length(), message.getBytes());
                player.setAttribute("replyTo", other);
                buffer.putLong(StringUtils.stringToLong(other)).putShort(new Random().nextInt(0xFFFF)).putTri(getMessageIndex(player)).put((byte) context.getChatIcon()).putBytes(bytes, 0, length);
                break;
            case MessageContext.CLAN_MESSAGE:
                ClanRepository clan = player.getCommunication().getClan();
                if (clan == null) {
                    return;
                }
                bytes = new byte[256];
                bytes[0] = (byte) context.getMessage().length();
                length = 1 + StringUtils.encryptPlayerChat(bytes, 0, 1, message.length(), message.getBytes());
                buffer.putLong(StringUtils.stringToLong(other));
                buffer.put((byte) 0);// it's just read does nothing
                buffer.putLong(StringUtils.stringToLong(clan.getName()));
                buffer.putShort(new Random().nextInt(0xFFFF));
                buffer.putTri(getMessageIndex(player));
                buffer.put((byte) context.getChatIcon()); // rights
                buffer.putBytes(bytes, 0, length);
                break;
        }
        if (player.isArtificial()) {
            ((AIPlayer) player).handleIncomingChat(context);
        }
        buffer.cypherOpcode(context.getPlayer().getSession().getIsaacPair().getOutput());
        player.getSession().write(buffer);
    }

}