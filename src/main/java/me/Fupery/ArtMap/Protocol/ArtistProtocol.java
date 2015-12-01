package me.Fupery.ArtMap.Protocol;

import com.google.common.collect.MapMaker;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import me.Fupery.ArtMap.NMS.NMSInterface;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Map;
import java.util.logging.Level;

public abstract class ArtistProtocol {

    private final Plugin plugin;
    private final NMSInterface nmsInterface;

    private final Map<String, Channel> channelLookup = new MapMaker().weakValues().makeMap();

    private final String handlerName = "ArtMapHandler";

    public ArtistProtocol(Plugin plugin, NMSInterface nmsInterface) {
        this.plugin = plugin;
        this.nmsInterface = nmsInterface;
    }

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
            Channel channel = getChannel(player);

            if (channel.pipeline().get(handlerName) != null) {
                channel.pipeline().remove(handlerName);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        channelLookup.remove(player.getName());
    }

    private Channel getChannel(Player player) {
        Channel channel = channelLookup.get(player.getName());

        if (channel == null) {
            channel = nmsInterface.getPlayerChannel(player);

            if (channel == null) {
                uninjectPlayer(player);
            }
            channelLookup.put(player.getName(), channel);
        }

        return channel;
    }

    public void close() {

        if (channelLookup != null && channelLookup.size() > 0) {

            for (String name : channelLookup.keySet()) {
                uninjectPlayer(Bukkit.getPlayer(name));
            }
            channelLookup.clear();
        }
    }

    public Object onPacketInAsync(Player player, Channel channel, Object packet) {
        return packet;
    }

    private final class PacketHandler extends ChannelDuplexHandler {
        private Player player;

        @Override
        public void channelRead(ChannelHandlerContext context,
                                Object msg) throws Exception {

            final Channel channel = context.channel();

            try {
                msg = onPacketInAsync(player, channel, msg);

            } catch (Exception e) {
                plugin.getLogger().log(Level.SEVERE, "Error in onPacketInAsync().", e);
            }

            if (msg != null) {
                super.channelRead(context, msg);
            }
        }
    }
}
