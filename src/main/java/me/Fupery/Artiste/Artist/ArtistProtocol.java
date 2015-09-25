package me.Fupery.Artiste.Artist;

import com.comphenix.tinyprotocol.Reflection;
import com.comphenix.tinyprotocol.Reflection.FieldAccessor;
import com.comphenix.tinyprotocol.Reflection.MethodInvoker;
import com.google.common.collect.MapMaker;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Map;
import java.util.logging.Level;

/**
 * Adapted from com.comhenix.tinyprotocol.TinyProtocol
 */
public abstract class ArtistProtocol {

    private static final MethodInvoker getPlayerHandle =
            Reflection.getMethod("{obc}.entity.CraftPlayer", "getHandle");
    private static final FieldAccessor<Object> getConnection =
            Reflection.getField("{nms}.EntityPlayer", "playerConnection", Object.class);
    private static final FieldAccessor<Object> getManager =
            Reflection.getField("{nms}.PlayerConnection", "networkManager", Object.class);
    private static final FieldAccessor<Channel> getChannel =
            Reflection.getField("{nms}.NetworkManager", Channel.class, 0);

    protected Plugin plugin;

    private Map<String, Channel> channelLookup = new MapMaker().weakValues().makeMap();

    private String handlerName = "ArtisteHandler";

    public ArtistProtocol(Plugin plugin) {
        this.plugin = plugin;
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

    public Channel getChannel(Player player) {
        Channel channel = channelLookup.get(player.getName());

        if (channel == null) {
            Object connection = getConnection.get(getPlayerHandle.invoke(player));
            Object manager = getManager.get(connection);

            channelLookup.put(player.getName(), channel = getChannel.get(manager));
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

    public Object onPacketOutAsync(Player player, Channel channel, Object packet) {
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

        @Override
        public void write(ChannelHandlerContext ctx, Object msg,
                          ChannelPromise promise) throws Exception {

            try {
                msg = onPacketOutAsync(player, ctx.channel(), msg);

            } catch (Exception e) {
                plugin.getLogger().log(Level.SEVERE, "Error in onPacketOutAsync().", e);
            }

            if (msg != null) {
                super.write(ctx, msg, promise);
            }
        }
    }
}
