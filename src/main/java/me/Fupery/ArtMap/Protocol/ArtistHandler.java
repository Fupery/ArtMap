package me.Fupery.ArtMap.Protocol;

import io.netty.channel.Channel;
import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.NMS.NMSInterface;
import me.Fupery.ArtMap.Protocol.Packet.ArtistPacket;
import me.Fupery.ArtMap.Utils.ArtDye;
import me.Fupery.ArtMap.Utils.LocationTag;
import me.Fupery.ArtMap.Utils.Recipe;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.map.MapView;

import java.util.concurrent.ConcurrentHashMap;

import static me.Fupery.ArtMap.Utils.Formatting.playerMessage;
import static me.Fupery.ArtMap.Utils.Formatting.saveUsage;

public class ArtistHandler {

    private ConcurrentHashMap<Player, CanvasRenderer> artists;

    private ArtMap plugin;
    private ArtistProtocol protocol;

    public ArtistHandler(final ArtMap plugin) {
        this.plugin = plugin;
        artists = new ConcurrentHashMap<>();
        final NMSInterface nmsInterface = plugin.getNmsInterface();

        protocol = new ArtistProtocol(plugin, nmsInterface) {

            @Override
            public Object onPacketInAsync(Player sender, Channel channel, Object packet) {

                if (artists != null && artists.containsKey(sender)) {

                    ArtistPacket artMapPacket = nmsInterface.getArtistPacket(packet);

                    if (artMapPacket == null) {
                        return packet;
                    }
                    CanvasRenderer renderer = artists.get(sender);

                    //keeps track of where the player is looking
                    if (artMapPacket instanceof ArtistPacket.PacketLook) {

                        ArtistPacket.PacketLook packetLook
                                = (ArtistPacket.PacketLook) artMapPacket;
                        renderer.setYaw(packetLook.getYaw());
                        renderer.setPitch(packetLook.getPitch());
                        return packet;

                        //adds pixels when the player clicks
                    } else if (artMapPacket instanceof ArtistPacket.PacketArmSwing) {

                        ItemStack item = sender.getItemInHand();

                        //paint bucket tool
                        if (item.getType() == Material.BUCKET) {

                            if (item.hasItemMeta()) {
                                ItemMeta meta = item.getItemMeta();

                                if (meta.getDisplayName().contains(Recipe.paintBucketTitle)
                                        && meta.hasLore()) {
                                    ArtDye colour = null;
                                    String[] lore = meta.getLore().toArray(new String[meta.getLore().size()]);

                                    for (ArtDye dye : ArtDye.values()) {

                                        if (lore[0].equals("§r" + dye.name())) {
                                            colour = dye;
                                            break;
                                        }
                                    }

                                    if (colour != null) {
                                        renderer.fillPixel(colour.getData());
                                    }
                                }
                            }
                            //dodge/burn tools
                        } else if (item.getType() == Material.FEATHER
                                || item.getType() == Material.COAL) {


                            //brush tool
                        } else {
                            ArtDye dye = ArtDye.getArtDye(item);

                            if (dye != null) {

                                renderer.drawPixel(dye.getData());
                                return null;
                            }
                        }

                        //flow brush allows for click & drag
                    } else if (artMapPacket instanceof ArtistPacket.PacketInteract) {

                        ItemStack item = sender.getItemInHand();

                        ArtDye dye = ArtDye.getArtDye(item);

                        if (dye != null) {

                            renderer.flowPixel(dye.getData());
                            return null;
                        }

                        //listens for when the player dismounts the easel
                    } else if (artMapPacket instanceof ArtistPacket.PacketVehicle) {

                        ArtistPacket.PacketVehicle packetVehicle
                                = ((ArtistPacket.PacketVehicle) artMapPacket);
                        if (packetVehicle.isDismount()) {
                            sender.sendMessage(playerMessage(saveUsage));
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
        CanvasRenderer renderer = artists.get(player);
        artists.remove(player);
        protocol.uninjectPlayer(player);

        renderer.stop();
        renderer.saveMap();

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

        if (artists.size() == 0) {
            protocol.close();
            plugin.setArtistHandler(null);
        }
    }

    public ArtistProtocol getProtocol() {
        return protocol;
    }

    public ConcurrentHashMap<Player, CanvasRenderer> getArtists() {
        return artists;
    }

    public ArtMap getPlugin() {
        return plugin;
    }
}