package me.Fupery.ArtMap.Protocol;

import io.netty.channel.Channel;
import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.Easel.Recipe;
import me.Fupery.ArtMap.Utils.LocationTag;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.map.MapView;

import java.util.concurrent.ConcurrentHashMap;

public class ArtistHandler {

    private ConcurrentHashMap<Player, CanvasRenderer> artists;

    private ArtMap plugin;
    private ArtistProtocol protocol;

    public ArtistHandler(ArtMap plugin) {
        this.plugin = plugin;
        artists = new ConcurrentHashMap<>();

        protocol = new ArtistProtocol(plugin) {

            @Override
            public Object onPacketInAsync(Player sender, Channel channel, Object packet) {

                if (artists != null && artists.containsKey(sender)) {

                    ArtistPacket ArtMapPacket = ArtistPacket.getArtistPacket(packet);

                    if (ArtMapPacket == null) {
                        return packet;
                    }
                    CanvasRenderer renderer = artists.get(sender);

                    //keeps track of where the player is looking
                    if (ArtMapPacket.getType() == PacketType.LOOK) {

                        renderer.setYaw(ArtMapPacket.getSuperField(float.class, "yaw"));
                        renderer.setPitch(ArtMapPacket.getSuperField(Float.class, "pitch"));
                        return packet;

                        //adds pixels when the player clicks
                    } else if (ArtMapPacket.getType() == PacketType.ARM_ANIMATION) {
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
                    } else if (ArtMapPacket.getType() == PacketType.STEER_VEHICLE) {

                        if (ArtMapPacket.getField(boolean.class, "d")) {
                            removePlayer(sender);
                            return null;
                        }

                    }

                } else {
                    removePlayer(sender);
                }
                return super.onPacketInAsync(sender, channel, packet);
            }
        };
    }

    public void addPlayer(Player player, MapView mapView, int yawOffset) {

        if (plugin.getPixelTable() != null) {
            artists.put(player, new CanvasRenderer(plugin, mapView, yawOffset));
            protocol.injectPlayer(player);
        }
    }

    public boolean containsPlayer(Player player) {
        return (artists.containsKey(player));
    }

    public void removePlayer(Player player) {
        protocol.uninjectPlayer(player);
        Entity seat = player.getVehicle();
        player.leaveVehicle();

        if (seat != null) {

            if (seat.hasMetadata("easel")) {
                String tag = seat.getMetadata("easel").get(0).asString();
                Location location = LocationTag.getLocation(seat.getWorld(), tag);

                if (plugin.getEasels().containsKey(location)) {
                    plugin.getEasels().get(location).setIsPainting(false);
                }
            }
            seat.remove();
        }
        CanvasRenderer renderer = artists.get(player);
        renderer.saveMap();
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

    public ArtMap getPlugin() {
        return plugin;
    }
}