package me.Fupery.ArtMap.Protocol;

import io.netty.channel.Channel;
import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.Easel.Easel;
import me.Fupery.ArtMap.Protocol.Packet.ArtistPacket;
import me.Fupery.ArtMap.Protocol.Packet.PacketType;
import me.Fupery.ArtMap.Utils.Reflection;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.map.MapView;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static me.Fupery.ArtMap.Protocol.Brushes.Brush.BrushAction;
import static me.Fupery.ArtMap.Protocol.Packet.ArtistPacket.PacketInteract.InteractType;

public class ArtistHandler {

    public final Settings SETTINGS;
    private final ConcurrentHashMap<UUID, ArtSession> artists;
    private final ArtistProtocol protocol;

    public ArtistHandler(ArtMap plugin) {
        artists = new ConcurrentHashMap<>();
        SETTINGS = new Settings(plugin.getConfig().getBoolean("forceArtKit"), 16384);

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

    public synchronized void addPlayer(final Player player, Easel easel, MapView mapView, int yawOffset) {
        ArtSession session = new ArtSession(easel, mapView, yawOffset);
        if (session.start(player) && protocol.injectPlayer(player)) {
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
        protocol.uninjectPlayer(player);
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
        protocol.close();
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