package me.Fupery.ArtMap.Protocol;

import io.netty.channel.Channel;
import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.NMS.NMSInterface;
import me.Fupery.ArtMap.Protocol.Packet.ArtistPacket;
import me.Fupery.ArtMap.Utils.LocationTag;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
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
                    ArtBrush brush = renderer.getBrush();

                    //keeps track of where the player is looking
                    if (artMapPacket instanceof ArtistPacket.PacketLook) {

                        ArtistPacket.PacketLook packetLook
                                = (ArtistPacket.PacketLook) artMapPacket;
                        renderer.setYaw(packetLook.getYaw());
                        renderer.setPitch(packetLook.getPitch());
                        return packet;

                        //paints when player clicks
                    } else if (artMapPacket instanceof ArtistPacket.PacketArmSwing) {

                        brush.paint(sender.getItemInHand(), true);
                        return null;

                    } else if (artMapPacket instanceof ArtistPacket.PacketInteract
                            && ((ArtistPacket.PacketInteract) artMapPacket).getInteraction()
                            == ArtistPacket.PacketInteract.InteractType.INTERACT) {

                        brush.paint(sender.getItemInHand(), false);
                        return null;

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