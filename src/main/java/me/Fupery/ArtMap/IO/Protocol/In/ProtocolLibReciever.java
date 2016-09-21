package me.Fupery.ArtMap.IO.Protocol.In;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.*;
import com.comphenix.protocol.wrappers.EnumWrappers;
import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.IO.Protocol.In.Packet.ArtistPacket;
import me.Fupery.ArtMap.Painting.ArtistHandler;
import org.bukkit.plugin.java.JavaPlugin;

import static me.Fupery.ArtMap.IO.Protocol.In.Packet.ArtistPacket.PacketInteract;
import static me.Fupery.ArtMap.IO.Protocol.In.Packet.ArtistPacket.PacketInteract.InteractType;

public class ProtocolLibReciever extends PacketReciever {

    public ProtocolLibReciever() {
        registerListeners(ArtMap.instance());
    }

    private void registerListeners(JavaPlugin plugin) {
        PacketAdapter.AdapterParameteters options = new PacketAdapter.AdapterParameteters();
        options.plugin(plugin);
        options.optionAsync();
        options.connectionSide(ConnectionSide.CLIENT_SIDE);
        options.listenerPriority(ListenerPriority.HIGH);
        options.types(
                PacketType.Play.Client.ARM_ANIMATION,
                PacketType.Play.Client.LOOK,
                PacketType.Play.Client.USE_ENTITY
        );
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(options) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                ArtistHandler handler = ArtMap.getArtistHandler();
                if (!handler.containsPlayer(event.getPlayer())) return;
                ArtistPacket packet = getPacketType(event.getPacket());
                if (packet == null) return;
                if (!onPacketPlayIn(handler, event.getPlayer(), packet)) event.setCancelled(true);
            }
        });
    }

    private ArtistPacket getPacketType(PacketContainer packet) {
        if (packet.getType() == PacketType.Play.Client.LOOK) {
            float yaw = packet.getFloat().read(0);
            float pitch = packet.getFloat().read(1);
            return new ArtistPacket.PacketLook(yaw, pitch);

        } else if (packet.getType() == PacketType.Play.Client.ARM_ANIMATION) {
            return new ArtistPacket.PacketArmSwing();

        } else if (packet.getType() == PacketType.Play.Client.USE_ENTITY) {
            EnumWrappers.EntityUseAction action = packet.getEntityUseActions().read(0);
            if (action == EnumWrappers.EntityUseAction.ATTACK) {
                return new PacketInteract(InteractType.ATTACK);
            } else {
                return new PacketInteract(InteractType.INTERACT);
            }
        }
        return null;
    }

    @Override
    public void close() {
        ProtocolLibrary.getProtocolManager().removePacketListeners(ArtMap.instance());
    }
}
