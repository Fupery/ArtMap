package me.Fupery.ArtMap.Protocol;

import io.netty.channel.Channel;
import me.Fupery.ArtMap.Easel.Easel;
import me.Fupery.ArtMap.Listeners.EaselInteractListener;
import me.Fupery.ArtMap.Protocol.Packet.ArtistPacket;
import me.Fupery.ArtMap.Utils.Lang;
import me.Fupery.ArtMap.Utils.LocationTag;
import me.Fupery.ArtMap.Utils.Reflection;
import me.Fupery.DataTables.DataTables;
import me.Fupery.DataTables.PixelTable;
import me.Fupery.InventoryMenu.Utils.SoundCompat;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.map.MapView;

import java.util.concurrent.ConcurrentHashMap;

public class ArtistHandler {

    private final ConcurrentHashMap<Player, CanvasRenderer> artists;
    private final ArtistProtocol protocol;

    public ArtistHandler() {
        artists = new ConcurrentHashMap<>();

        protocol = new ArtistProtocol() {

            @Override
            public Object onPacketInAsync(Player sender, Channel channel, Object packet) {

                if (artists.containsKey(sender)) {

                    ArtistPacket artMapPacket = Reflection.getArtistPacket(packet);

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
                    }

                } else {
                    removePlayer(sender);
                }
                return super.onPacketInAsync(sender, channel, packet);
            }
        };
    }

    private static PixelTable loadTables() {
        PixelTable pixelTable;
        try {
            pixelTable = DataTables.loadTable(4);
        } catch (DataTables.InvalidResolutionFactorException e) {
            pixelTable = null;
            e.printStackTrace();
        }
        return pixelTable;
    }

    public synchronized void addPlayer(Player player, MapView mapView, int yawOffset) {
        artists.put(player, new CanvasRenderer(mapView, yawOffset));
        protocol.injectPlayer(player);
    }

    public synchronized boolean containsPlayer(Player player) {
        return (artists.containsKey(player));
    }

    public synchronized void removePlayer(final Player player) {
        CanvasRenderer renderer = artists.get(player);
        artists.remove(player);

        protocol.uninjectPlayer(player);

        Entity seat = player.getVehicle();
        String mapID = "[Not Found]";
        SoundCompat.BLOCK_LADDER_STEP.play(player.getLocation(), 1, -3);
        player.leaveVehicle();

        if (seat != null) {

            if (seat.hasMetadata("easel")) {
                String tag = seat.getMetadata("easel").get(0).asString();
                Location location = LocationTag.getLocation(seat.getWorld(), tag);

                if (EaselInteractListener.easels.containsKey(location)) {
                    Easel easel = EaselInteractListener.easels.get(location);
                    easel.setIsPainting(false);

                    if (easel.hasItem()) {
                        mapID = "" + easel.getItem().getDurability();
                    }
                }
            }
            seat.remove();
        }

        if (renderer != null) {
            renderer.stop();
            renderer.saveMap();

        } else {
            Bukkit.getLogger().warning(Lang.prefix + ChatColor.RED + String.format(
                    "Renderer not found for player: %s, mapID: %s", player.getName(), mapID));
        }
    }

    public synchronized void clearPlayers() {
        for (Player player : artists.keySet()) {
            removePlayer(player);
        }
    }

    public ArtistProtocol getProtocol() {
        return protocol;
    }

}