package me.Fupery.ArtMap.Protocol;

import com.google.common.collect.MapMaker;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import me.Fupery.ArtMap.IO.ErrorLogger;
import me.Fupery.ArtMap.Utils.Lang;
import me.Fupery.ArtMap.Utils.Reflection;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

public abstract class ArtMapProtocolListener implements ProtocolHandler {

    private final Map<UUID, Channel> channelLookup = new MapMaker().weakValues().makeMap();

    private final String handlerName = "ArtMapHandler";
    private ArtistHandler artistHandler;

    public ArtMapProtocolListener(ArtistHandler artistHandler) {
        this.artistHandler = artistHandler;
    }

    @Override
    public boolean injectPlayer(Player player) {
        Channel channel = getChannel(player);
        if (channel == null) {
            return false;
        }
        PacketHandler handler;
        try {
            handler = (PacketHandler) channel.pipeline().get(handlerName);

            if (handler == null) {
                handler = new PacketHandler();
                channel.pipeline().addBefore("packet_handler", handlerName, handler);
            }
        } catch (IllegalArgumentException | ClassCastException e) {
            handler = (PacketHandler) channel.pipeline().get(handlerName);
        }
        handler.player = player;
        return true;
    }

    @Override
    public void uninjectPlayer(Player player) {

        try {
            final Channel channel = getChannel(player);

            if (channel.pipeline().get(handlerName) != null) {

                channel.eventLoop().execute(new Runnable() {

                    @Override
                    public void run() {
                        if (channel.pipeline().get(handlerName) != null) {
                            channel.pipeline().remove(handlerName);
                        }
                    }
                });
            }
        } catch (Exception e) {
            ErrorLogger.log(e, "Error unbinding player channel!");
        }
        channelLookup.remove(player.getUniqueId());
    }

    private Channel getChannel(Player player) {
        Channel channel = channelLookup.get(player.getUniqueId());

        if (channel == null) {
            channel = Reflection.getPlayerChannel(player);

            if (channel == null) {
                Bukkit.getLogger().warning(Lang.PREFIX + "Error binding player channel!");
                return null;
            }
            channelLookup.put(player.getUniqueId(), channel);
        }

        return channel;
    }

    @Override
    public void close() {

        if (channelLookup != null && channelLookup.size() > 0) {

            for (UUID player : channelLookup.keySet()) {
                uninjectPlayer(Bukkit.getPlayer(player));
            }
            channelLookup.clear();
        }
    }

    public Object onPacketInAsync(Player player, Channel channel, Object packet) {
        if (!artistHandler.containsPlayer(player)) return packet;
        return onPacketPlayIn(player, Reflection.getArtistPacket(packet)) ? packet : null;
    }

    private final class PacketHandler extends ChannelDuplexHandler {
        private Player player;

        @Override
        public void channelRead(ChannelHandlerContext context, Object msg) throws Exception {

            final Channel channel = context.channel();

            try {
                msg = onPacketInAsync(player, channel, msg);

            } catch (Exception e) {
                ErrorLogger.log(e, "Error in onPacketInAsync().");
            }
            if (msg != null) {
                super.channelRead(context, msg);
            }
        }
    }
}
