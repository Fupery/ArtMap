package me.Fupery.ArtMap.Protocol;

import io.netty.channel.Channel;
import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.Easel.Easel;
import me.Fupery.ArtMap.NMS.NMSInterface;
import me.Fupery.ArtMap.Protocol.Packet.ArtistPacket;
import me.Fupery.ArtMap.Utils.LocationTag;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.map.MapView;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class ArtistHandler {

    private final HashMap<Player, CanvasRenderer> artists;

    private final ArtMap plugin;
    private final ArtistProtocol protocol;

    public ArtistHandler(final ArtMap plugin) {
        this.plugin = plugin;
        artists = new HashMap<>();
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
                    }

                } else {
                    removePlayer(sender);
                }
                return super.onPacketInAsync(sender, channel, packet);
            }
        };
    }

    public synchronized void addPlayer(Player player, MapView mapView, int yawOffset) {

        if (plugin.getPixelTable() != null) {
            artists.put(player, new CanvasRenderer(plugin, mapView, yawOffset));
            protocol.injectPlayer(player);
        }
    }

    public boolean containsPlayer(Player player) {
        return (artists.containsKey(player));
    }

    public synchronized void removePlayer(final Player player) {
        CanvasRenderer renderer = artists.get(player);
        artists.remove(player);

        if (plugin.isEnabled()) {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
                @Override
                public void run() {
                    protocol.uninjectPlayer(player);
                }
            });

        } else {
            protocol.uninjectPlayer(player);
        }
        Entity seat = player.getVehicle();
        String mapID = "[Not Found]";
        player.playSound(player.getLocation(), Sound.STEP_LADDER, (float) 0.5, -3);
        player.leaveVehicle();

        if (seat != null) {

            if (seat.hasMetadata("easel")) {
                String tag = seat.getMetadata("easel").get(0).asString();
                Location location = LocationTag.getLocation(seat.getWorld(), tag);

                if (plugin.getEasels().containsKey(location)) {
                    Easel easel = plugin.getEasels().get(location);
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
            plugin.getLogger().warning(ChatColor.RED + String.format(
                    "Renderer not found for player: %s, mapID: %s", player.getName(), mapID));
        }

        if (artists.size() == 0) {
            protocol.close();
            plugin.setArtistHandler(null);
        }

    }

    public void clearPlayers() {
        for (Player player : artists.keySet()) {
            removePlayer(player);
        }
    }

    public ArtistProtocol getProtocol() {
        return protocol;
    }

    public ArtMap getPlugin() {
        return plugin;
    }
}