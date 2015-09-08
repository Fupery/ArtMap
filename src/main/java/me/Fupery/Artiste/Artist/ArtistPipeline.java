package me.Fupery.Artiste.Artist;

import com.comphenix.tinyprotocol.Reflection;
import com.comphenix.tinyprotocol.TinyProtocol;
import io.netty.channel.Channel;
import me.Fupery.Artiste.Artiste;
import me.Fupery.Artiste.Easel.Easel;
import me.Fupery.Artiste.Easel.Recipe;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class ArtistPipeline {

    private Artiste plugin;
    private TinyProtocol protocol;

    ConcurrentHashMap<Player, Artist> artists;

    private Class<?> playerLookClass =
            Reflection.getClass("{nms}.PacketPlayInFlying$PacketPlayInLook");
    private Reflection.FieldAccessor<Float> playerPitch =
            Reflection.getField(playerLookClass, float.class, 0);
    private Reflection.FieldAccessor<Float> playerYaw =
            Reflection.getField(playerLookClass, float.class, 1);

    private Class<?> playerSwingArmClass =
            Reflection.getClass("{nms}.PacketPlayInArmAnimation");

    private Class<?> playerDismountClass =
            Reflection.getClass("{nms}.PacketPlayInSteerVehicle");
    private Reflection.FieldAccessor<Boolean> playerDismount =
            Reflection.getField(playerDismountClass, "d", boolean.class);

    public ArtistPipeline(Artiste plugin) {
        this.plugin = plugin;
        artists = new ConcurrentHashMap<>();

        protocol = new TinyProtocol(plugin) {
            @Override
            public Object onPacketOutAsync(Player reciever, Channel channel, Object packet) {
                return super.onPacketOutAsync(reciever, channel, packet);
            }

            @Override
            public Object onPacketInAsync(Player sender, Channel channel, Object packet) {

                if (artists.containsKey(sender)) {

                    Artist artist = artists.get(sender);

                    //keeps track of where the player is looking
                    if (playerLookClass.isInstance(packet)) {
                        float pitch = playerPitch.get(packet);
                        float yaw = playerYaw.get(packet);

                        if (artist.getLastPitch() != pitch) {
                            artist.setLastPitch(pitch);
                        }

                        if (artist.getLastYaw() != yaw) {
                            artist.setLastYaw(yaw);
                        }
                        return packet;

                        //adds pixels when the player clicks
                    } else if (playerSwingArmClass.isInstance(packet)) {

                        ItemStack item = sender.getItemInHand();

                        //brush tool
                        if (item.getType() == Material.INK_SACK) {

                            if (artist.getRenderer() != null) {
                                byte[] pixel = getPixel(artist.getPitchOffset(),
                                        artist.getLastPitch(), artist.getLastYaw());

                                if (pixel != null) {
                                    artist.getRenderer().drawPixel(pixel[0], pixel[1],
                                            DyeColor.getByData((byte) (15 - item.getDurability())));
                                }
                            }

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

                                    if (colour != null && artist.getRenderer() != null) {

                                        byte[] pixel = getPixel(artist.getPitchOffset(),
                                                artist.getLastPitch(), artist.getLastYaw());

                                        if (pixel != null) {
                                            artist.getRenderer().fillPixel(pixel[0], pixel[1], colour);
                                        }
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
        artists.put(player, new Artist(plugin, easel));
        protocol.injectPlayer(player);
    }

    public boolean containsPlayer(Player player) {

        return (artists.containsKey(player));
    }

    public void removePlayer(Player player) {
        Easel easel = artists.get(player).getEasel();
        protocol.uninjectPlayer(player);
        player.leaveVehicle();
        easel.setIsPainting(false);
        easel.getSeat().remove();
        MapView mapView = Bukkit.getMap(easel.getFrame().getItem().getDurability());

        if (mapView.getRenderers() != null) {
            mapView.getRenderers().clear();
        }
        artists.remove(player);

        if (artists.size() == 0) {
            protocol.close();
            plugin.setArtistPipeline(null);
        }
    }

    //finds the corresponding pixel for the pitch & yaw clicked
    private byte[] getPixel(int pitchOffset, float pitch, float yaw) {
        byte[] pixel = new byte[2];

        if (pitch > 0) {
            pitch -= pitchOffset;

        } else {
            pitch += pitchOffset;
        }

        double yawAdjust = ((0.0044 * pitch * pitch) - (0.0075 * pitch)) * (0.0265 * yaw);

        if (yaw > 0) {

            if (yawAdjust > 0) {
                yaw += yawAdjust;
            }

        } else if (yaw < 0) {

            if (yawAdjust < 0) {
                yaw += yawAdjust;
            }
        }
        pixel[0] = ((byte) ((Math.tan(Math.toRadians(pitch)) * .6155 * 32) + 16));
        pixel[1] = ((byte) ((Math.tan(Math.toRadians(yaw)) * .6155 * 32) + 16));
//        pixel = table.getPixel(pitch, yaw);

        for (byte b : pixel) {

            if (b >= 32 || b < 0) {
                return null;
            }
        }
        return pixel;
    }
    public Artiste getPlugin() {
        return plugin;
    }
}