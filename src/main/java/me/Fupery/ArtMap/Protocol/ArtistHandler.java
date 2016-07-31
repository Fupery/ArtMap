package me.Fupery.ArtMap.Protocol;

import io.netty.channel.Channel;
import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.Easel.Easel;
import me.Fupery.ArtMap.Listeners.EaselInteractListener;
import me.Fupery.ArtMap.Protocol.Packet.ArtistPacket;
import me.Fupery.ArtMap.Protocol.Packet.PacketType;
import me.Fupery.ArtMap.Utils.Lang;
import me.Fupery.ArtMap.Utils.LocationTag;
import me.Fupery.ArtMap.Utils.Reflection;
import me.Fupery.DataTables.DataTables;
import me.Fupery.DataTables.PixelTable;
import me.Fupery.InventoryMenu.Utils.SoundCompat;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.map.MapView;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static me.Fupery.ArtMap.Protocol.Brushes.Brush.BrushAction;
import static me.Fupery.ArtMap.Protocol.Packet.ArtistPacket.PacketInteract.InteractType;

public class ArtistHandler {

    private final ConcurrentHashMap<UUID, ArtSession> artists;
    private final ArtistProtocol protocol;

    public ArtistHandler() {
        artists = new ConcurrentHashMap<>();

        protocol = new ArtistProtocol() {

            @Override
            public Object onPacketInAsync(Player sender, Channel channel, Object packet) {

                if (artists.containsKey(sender.getUniqueId())) {
                    ArtistPacket artistPacket = Reflection.getArtistPacket(packet);
                    if (artistPacket == null) {
                        return packet;
                    }
                    ArtSession session = artists.get(sender.getUniqueId());
                    PacketType type = artistPacket.getType();

                    if (type == PacketType.LOOK) {
                        ArtistPacket.PacketLook packetLook = (ArtistPacket.PacketLook) artistPacket;
                        session.updatePosition(packetLook.getYaw(), packetLook.getPitch());
                        return packet;

                    } else if (type == PacketType.INTERACT) {
                        InteractType click = ((ArtistPacket.PacketInteract) artistPacket).getInteraction();
                        session.paint(sender.getItemInHand(), (click == InteractType.ATTACK)
                                ? BrushAction.LEFT_CLICK : BrushAction.RIGHT_CLICK);
                        return null;
                    }
                } else {
                    removePlayer(sender);
                }
                return packet;
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

    public void addPlayer(final Player player, MapView mapView, int yawOffset) {
        artists.put(player.getUniqueId(), new ArtSession(player, mapView, yawOffset));
        if (!protocol.injectPlayer(player)) {
            artists.remove(player.getUniqueId());
            Entity seat = player.getVehicle();
            if (seat != null) {
                player.leaveVehicle();
                removeSeat(seat);
            }
        }
    }

    public boolean containsPlayer(Player player) {
        return (artists.containsKey(player.getUniqueId()));
    }

    public synchronized void removePlayer(final Player player) {
        removePlayer(player, player.getVehicle());
    }

    public synchronized void removePlayer(final Player player, Entity seat) {
        if (!artists.containsKey(player.getUniqueId())) return;//just for safety :)
        ArtSession session = artists.get(player.getUniqueId());
        artists.remove(player.getUniqueId());
        protocol.uninjectPlayer(player);
        SoundCompat.BLOCK_LADDER_STEP.play(player.getLocation(), 1, -3);
        player.leaveVehicle();
        removeSeat(seat);

        if (session != null) {
            session.end();
        } else {
            ArtMap.runTaskLater(new Runnable() {
                @Override
                public void run() {
                    ArtSession session = artists.get(player.getUniqueId());
                    if (session != null) {
                        session.end();
                    } else {
                        Bukkit.getLogger().warning(Lang.prefix + String.format(
                                "§cRenderer not found for player: %s", player.getName()));
                    }
                }
            }, 1);

        }
    }

    private void removeSeat(Entity seat) {
        ArtMap.runTaskLater(new Runnable() {
            @Override
            public void run() {
                if (seat == null) {
                    return;
                }

                if (!seat.hasMetadata("easel")) {
                    return;
                }
                String tag = seat.getMetadata("easel").get(0).asString();
                Location location = LocationTag.getLocation(seat.getWorld(), tag);

                if (EaselInteractListener.easels.containsKey(location)) {
                    Easel easel = EaselInteractListener.easels.get(location);
                    easel.setIsPainting(false);
                }
                seat.remove();
            }
        }, 1);

    }

    private synchronized void clearPlayers() {
        for (UUID uuid : artists.keySet()) {
            removePlayer(Bukkit.getPlayer(uuid));
        }
    }

    public void stop() {
        clearPlayers();
        protocol.close();
    }
}