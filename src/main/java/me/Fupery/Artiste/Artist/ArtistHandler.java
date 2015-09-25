package me.Fupery.Artiste.Artist;

import com.comphenix.tinyprotocol.Reflection;
import io.netty.channel.Channel;
import me.Fupery.Artiste.Artiste;
import me.Fupery.Artiste.Easel.Easel;
import me.Fupery.Artiste.Easel.Recipe;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.concurrent.ConcurrentHashMap;

public class ArtistHandler {

    ConcurrentHashMap<Player, CanvasRenderer> artists;

    private Artiste plugin;
    private ArtistProtocol protocol;

    private Class<?> playerLookClass =
            Reflection.getClass("{nms}.PacketPlayInFlying$PacketPlayInLook");
    private Reflection.FieldAccessor<Float> playerYaw =
            Reflection.getField(playerLookClass, float.class, 0);
    private Reflection.FieldAccessor<Float> playerPitch =
            Reflection.getField(playerLookClass, float.class, 1);

    private Class<?> playerSwingArmClass =
            Reflection.getClass("{nms}.PacketPlayInArmAnimation");

    private Class<?> playerDismountClass =
            Reflection.getClass("{nms}.PacketPlayInSteerVehicle");
    private Reflection.FieldAccessor<Boolean> playerDismount =
            Reflection.getField(playerDismountClass, "d", boolean.class);

    public ArtistHandler(Artiste plugin) {
        this.plugin = plugin;
        artists = new ConcurrentHashMap<>();

        protocol = new ArtistProtocol(plugin) {
            @Override
            public Object onPacketOutAsync(Player receiver, Channel channel, Object packet) {
                return super.onPacketOutAsync(receiver, channel, packet);
            }

            @Override
            public Object onPacketInAsync(Player sender, Channel channel, Object packet) {

                if (artists != null && artists.containsKey(sender)) {

                    CanvasRenderer renderer = artists.get(sender);

                    //keeps track of where the player is looking
                    if (playerLookClass.isInstance(packet)) {
                        float yaw = playerYaw.get(packet);
                        float pitch = playerPitch.get(packet);

                        if (renderer.getLastYaw() != yaw) {
                            renderer.setLastYaw(yaw);
                        }

                        if (renderer.getLastPitch() != pitch) {
                            renderer.setLastPitch(pitch);
                        }
                        return packet;

                        //adds pixels when the player clicks
                    } else if (playerSwingArmClass.isInstance(packet)) {

                        ItemStack item = sender.getItemInHand();

                        //brush tool
                        if (item.getType() == Material.INK_SACK) {

                            renderer.drawPixel(DyeColor.getByData((byte) (15 - item.getDurability())));

                            //paint bucket tool
                        } else if (item.getType() == Material.BUCKET) {

                            if (item.hasItemMeta()) {
                                ItemMeta meta = item.getItemMeta();

                                if (meta.getDisplayName().contains(Recipe.paintBucketTitle)
                                        && meta.hasLore()) {
                                    DyeColor colour = null;

                                    for (DyeColor d : DyeColor.values()) {

                                        if (meta.getLore().toArray()[0].equals("§r" + d.name())) {
                                            colour = d;
                                        }
                                    }

                                    if (colour != null) {

                                        renderer.fillPixel(colour);
                                    }
                                }
                            }
                        }
                        return null;

                        //listens for when the player dismounts the easel
                    } else if (playerDismountClass.isInstance(packet)) {

                        if (playerDismount.get(packet)) {
                            removePlayer(sender);
                            return null;
                        }
                    }

                }
                return super.onPacketInAsync(sender, channel, packet);
            }
        };
    }

    public void addPlayer(Player player, Easel easel) {
        artists.put(player, new CanvasRenderer(plugin, 4, easel));
        protocol.injectPlayer(player);
    }

    public boolean containsPlayer(Player player) {
        return (artists.containsKey(player));
    }

    public void removePlayer(Player player) {
        protocol.uninjectPlayer(player);
        Entity seat = player.getVehicle();
        player.leaveVehicle();

        if (seat != null) {
            seat.remove();
        }
        CanvasRenderer renderer = artists.get(player);
        renderer.clearRenderers();
        artists.remove(player);

        if (artists.size() == 0) {
            protocol.close();
            plugin.setArtistHandler(null);
        }
    }

    public ArtistProtocol getProtocol() {
        return protocol;
    }

    public Artiste getPlugin() {
        return plugin;
    }
}