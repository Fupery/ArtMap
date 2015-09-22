package me.Fupery.Artiste.Artist;
import com.comphenix.tinyprotocol.Reflection;

import io.netty.channel.*;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.comphenix.tinyprotocol.Reflection.FieldAccessor;
import com.comphenix.tinyprotocol.Reflection.MethodInvoker;
import com.google.common.collect.Lists;
import com.google.common.collect.MapMaker;

/**
 *  Adapted from com.comhenix.tinyprotocol.TinyProtocol
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

    private static final Class<Object> minecraftServerClass =
            Reflection.getUntypedClass("{nms}.MinecraftServer");
    private static final Class<Object> serverConnectionClass =
            Reflection.getUntypedClass("{nms}.ServerConnection");
    private static final FieldAccessor<Object> getMinecraftServer =
            Reflection.getField("{obc}.CraftServer", minecraftServerClass, 0);
    private static final FieldAccessor<Object> getServerConnection =
            Reflection.getField(minecraftServerClass, serverConnectionClass, 0);
    private static final MethodInvoker getNetworkMarkers =
            Reflection.getTypedMethod(serverConnectionClass, null, List.class, serverConnectionClass);

    // Speedup channel lookup
    private Map<String, Channel> channelLookup = new MapMaker().weakValues().makeMap();

    // List of network markers
    private List<Object> networkManagers;

    // Injected channel handlers
    private List<Channel> serverChannels = Lists.newArrayList();
    private ChannelInboundHandlerAdapter serverChannelHandler;
    private ChannelInitializer<Channel> beginInitProtocol;
    private ChannelInitializer<Channel> endInitProtocol;

    private String handlerName = "ArtisteHandler";

    protected volatile boolean closed;
    protected Plugin plugin;

    public ArtistProtocol(Plugin plugin) {
        this.plugin = plugin;
        registerChannelHandler();
    }

    @SuppressWarnings("unchecked")
    private void registerChannelHandler() {
        Object server = getMinecraftServer.get(Bukkit.getServer());
        Object serverConnection = getServerConnection.get(server);
        boolean looking = true;

        // We need to synchronize against this list
        networkManagers = (List<Object>) getNetworkMarkers.invoke(null, serverConnection);
        createServerChannelHandler();

        // Find the correct list, or implicitly throw an exception
        for (int i = 0; looking; i++) {
            List<Object> list = Reflection.getField(
                    serverConnection.getClass(), List.class, i).get(serverConnection);

            for (Object item : list) {

                if (!ChannelFuture.class.isInstance(item)) {
                    break;
                }

                // Channel future that contains the server connection
                Channel serverChannel = ((ChannelFuture) item).channel();

                serverChannels.add(serverChannel);
                serverChannel.pipeline().addFirst(serverChannelHandler);
                looking = false;
            }
        }
    }

    private void createServerChannelHandler() {
        // Handle connected channels
        endInitProtocol = new ChannelInitializer<Channel>() {

            @Override
            public void initChannel(Channel channel) throws Exception {

                try {

                    synchronized (networkManagers) {
                        // Stop injecting channels
                        if (!closed) {
                            injectChannel(channel);
                        }
                    }

                } catch (Exception e) {
                    plugin.getLogger().log(Level.SEVERE, "Cannot inject incoming channel " + channel, e);
                }
            }

        };

        beginInitProtocol = new ChannelInitializer<Channel>() {

            @Override
            public void initChannel(Channel channel) throws Exception {
                channel.pipeline().addLast(endInitProtocol);
            }

        };

        serverChannelHandler = new ChannelInboundHandlerAdapter() {

            @Override
            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                Channel channel = (Channel) msg;

                channel.pipeline().addFirst(beginInitProtocol);
                ctx.fireChannelRead(msg);
            }
        };
    }

    public void injectPlayer(Player player) {
        Channel channel = getChannel(player);
        injectChannel(channel).player = player;
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
    }

    public boolean isInjected(Player player) {
        Channel channel = getChannel(player);
        return (channel != null && channel.pipeline().get(handlerName) != null);
    }

    private PacketHandler injectChannel(Channel channel) {

        try {
            PacketHandler interceptor = (PacketHandler) channel.pipeline().get(handlerName);

            if (interceptor == null) {
                interceptor = new PacketHandler();
                channel.pipeline().addBefore("packet_handler", handlerName, interceptor);
            }

            return interceptor;

        } catch (IllegalArgumentException e) {
            return (PacketHandler) channel.pipeline().get(handlerName);
        }
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

    public Object onPacketInAsync(Player player, Channel channel, Object packet) {
        return packet;
    }

    public Object onPacketOutAsync(Player player, Channel channel, Object packet) {
        return packet;
    }

    private final class PacketHandler extends ChannelDuplexHandler {
        private Player player;

        @Override
        public void channelRead(ChannelHandlerContext context, Object msg) throws Exception {

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
        public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {

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
