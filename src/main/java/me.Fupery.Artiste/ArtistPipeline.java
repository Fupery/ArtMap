package me.Fupery.Artiste;

import com.comphenix.tinyprotocol.Reflection;
import com.comphenix.tinyprotocol.TinyProtocol;
import io.netty.channel.Channel;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ArtistPipeline {

    private Artiste plugin;
    private Player player;
    private TinyProtocol protocol;

    private Class<?> playerLookClass = Reflection.getClass("{nms}.PacketPlayInFlying$PacketPlayInLook");
    private Reflection.FieldAccessor<Float> playerPitch = Reflection.getField(playerLookClass, float.class, 0);
    private Reflection.FieldAccessor<Float> playerYaw = Reflection.getField(playerLookClass, float.class, 1);

    public ArtistPipeline(Artiste plugin, Player player) {
        this.plugin = plugin;
        this.player = player;

        protocol = new TinyProtocol(plugin) {
            @Override
            public Object onPacketOutAsync(Player reciever, Channel channel, Object packet) {
                return super.onPacketOutAsync(reciever, channel, packet);
            }

            @Override
            public Object onPacketInAsync(Player sender, Channel channel, Object packet) {

                if (playerLookClass.isInstance(packet)) {
                    Bukkit.getLogger().info(playerPitch.get(packet).toString());
                    Bukkit.getLogger().info(playerYaw.get(packet).toString());
                }
                return super.onPacketInAsync(sender, channel, packet);
            }
        };
        Bukkit.getLogger().info("Injected " + player.getName());
        protocol.injectPlayer(player);
    }

    public Artiste getPlugin() {
        return plugin;
    }

    public Player getPlayer() {
        return player;
    }
}
