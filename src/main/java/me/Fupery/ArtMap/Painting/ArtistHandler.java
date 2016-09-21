package me.Fupery.ArtMap.Painting;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.Easel.Easel;
import me.Fupery.ArtMap.IO.Protocol.In.Packet.ArtistPacket;
import me.Fupery.ArtMap.IO.Protocol.In.Packet.PacketType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.map.MapView;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static me.Fupery.ArtMap.IO.Protocol.In.Packet.ArtistPacket.PacketInteract.InteractType;
import static me.Fupery.ArtMap.Painting.Brushes.Brush.BrushAction;

public class ArtistHandler {

    public final Settings SETTINGS;
    private final ConcurrentHashMap<UUID, ArtSession> artists;

    public ArtistHandler() {
        artists = new ConcurrentHashMap<>();
        SETTINGS = new Settings(ArtMap.getConfiguration().FORCE_ART_KIT, 16384);
    }

    public boolean handlePacket(Player sender, ArtistPacket packet) {
        if (packet == null) {
            return true;
        }
        if (artists.containsKey(sender.getUniqueId())) {
            ArtSession session = artists.get(sender.getUniqueId());
            PacketType type = packet.getType();

            if (type == PacketType.LOOK) {
                ArtistPacket.PacketLook packetLook = (ArtistPacket.PacketLook) packet;
                session.updatePosition(packetLook.getYaw(), packetLook.getPitch());
                return true;

            } else if (type == PacketType.INTERACT) {
                InteractType click = ((ArtistPacket.PacketInteract) packet).getInteraction();
                session.paint(sender.getItemInHand(), (click == InteractType.ATTACK)
                        ? BrushAction.LEFT_CLICK : BrushAction.RIGHT_CLICK);
                return false;
            }
        } else {
            removePlayer(sender);
        }
        return true;
    }

    public synchronized void addPlayer(final Player player, Easel easel, MapView mapView, int yawOffset) {
        ArtSession session = new ArtSession(easel, mapView, yawOffset);
        if (session.start(player) && ArtMap.getProtocolManager().PACKET_RECIEVER.injectPlayer(player)) {
            artists.put(player.getUniqueId(), session);
            session.setActive(true);
        }
    }

    public Easel getEasel(Player player) {
        if (artists.containsKey(player.getUniqueId())) {
            return artists.get(player.getUniqueId()).getEasel();
        }
        return null;
    }

    public boolean containsPlayer(Player player) {
        return (artists.containsKey(player.getUniqueId()));
    }

    public synchronized void removePlayer(final Player player) {
        if (!artists.containsKey(player.getUniqueId())) return;//just for safety :)
        ArtSession session = artists.get(player.getUniqueId());
        if (!session.isActive()) return;
        artists.remove(player.getUniqueId());
        session.end(player);
        ArtMap.getProtocolManager().PACKET_RECIEVER.uninjectPlayer(player);
    }

    public ArtSession getCurrentSession(Player player) {
        return artists.get(player.getUniqueId());
    }

    private synchronized void clearPlayers() {
        for (UUID uuid : artists.keySet()) {
            removePlayer(Bukkit.getPlayer(uuid));
        }
    }

    public void stop() {
        clearPlayers();
        ArtMap.getProtocolManager().PACKET_RECIEVER.close();
    }

    public static class Settings {
        public final boolean FORCE_ART_KIT;
        public final int MAX_PIXELS_UPDATE_TICK;

        private Settings(boolean forceArtKit, int maxPixelsUpdatedPerTick) {
            this.FORCE_ART_KIT = forceArtKit;
            this.MAX_PIXELS_UPDATE_TICK = (maxPixelsUpdatedPerTick > 16384 || maxPixelsUpdatedPerTick <= 0)
                    ? 16384 : maxPixelsUpdatedPerTick;
        }
    }
}