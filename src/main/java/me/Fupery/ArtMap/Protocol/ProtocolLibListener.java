package me.Fupery.ArtMap.Protocol;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.*;
import com.comphenix.protocol.wrappers.EnumWrappers;
import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.Protocol.Packet.ArtistPacket;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import static me.Fupery.ArtMap.Protocol.Packet.ArtistPacket.PacketInteract;
import static me.Fupery.ArtMap.Protocol.Packet.ArtistPacket.PacketInteract.InteractType;

public abstract class ProtocolLibListener implements ProtocolHandler {

    private ArtistHandler handler;

    public ProtocolLibListener(ArtistHandler handler) {
        registerListeners(ArtMap.instance());
        this.handler = handler;
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
                if (!handler.containsPlayer(event.getPlayer())) return;
                ArtistPacket packet = getPacketType(event.getPacket());
                if (packet == null) return;
                if (!onPacketPlayIn(event.getPlayer(), packet)) event.setCancelled(true);
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
    public boolean injectPlayer(Player player) {
        return true;
    }

    @Override
    public void uninjectPlayer(Player player) {
    }

    @Override
    public void close() {
        ProtocolLibrary.getProtocolManager().removePacketListeners(ArtMap.instance());
    }
}
