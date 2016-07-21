package me.Fupery.ArtMap.Protocol;

import com.google.common.collect.MapMaker;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import me.Fupery.ArtMap.Utils.Lang;
import me.Fupery.ArtMap.Utils.Reflection;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

public abstract class ArtistProtocol {

    private final Map<UUID, Channel> channelLookup = new MapMaker().weakValues().makeMap();

    private final String handlerName = "ArtMapHandler";

    public void injectPlayer(Player player) {

        Channel channel = getChannel(player);
        PacketHandler interceptor = (PacketHandler) channel.pipeline().get(handlerName);

        if (interceptor == null) {
            interceptor = new PacketHandler();
            channel.pipeline().addBefore("packet_handler", handlerName, interceptor);
        }
        interceptor.player = player;
    }

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
            e.printStackTrace();
        }
        channelLookup.remove(player.getUniqueId());
    }

    private Channel getChannel(Player player) {
        Channel channel = channelLookup.get(player.getUniqueId());

        if (channel == null) {
            channel = Reflection.getPlayerChannel(player);

            if (channel == null) {
                uninjectPlayer(player);
            }
            channelLookup.put(player.getUniqueId(), channel);
        }

        return channel;
    }

    public void close() {

        if (channelLookup != null && channelLookup.size() > 0) {

            for (UUID player : channelLookup.keySet()) {
                uninjectPlayer(Bukkit.getPlayer(player));
            }
            channelLookup.clear();
        }
    }

    public abstract Object onPacketInAsync(Player player, Channel channel, Object packet);

    private final class PacketHandler extends ChannelDuplexHandler {
        private Player player;

        @Override
        public void channelRead(ChannelHandlerContext context, Object msg) throws Exception {

            final Channel channel = context.channel();

            try {
                msg = onPacketInAsync(player, channel, msg);

            } catch (Exception e) {
                Bukkit.getLogger().log(Level.SEVERE, Lang.prefix + "Error in onPacketInAsync().", e);
            }
            if (msg != null) {
                super.channelRead(context, msg);
            }
        }
    }
}
