package me.Fupery.Artiste;

import com.comphenix.tinyprotocol.Reflection;
import com.comphenix.tinyprotocol.TinyProtocol;
import io.netty.channel.Channel;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;

public class ArtistPipeline {

    private Artiste plugin;
    private Player player;
    private Easel easel;
    private TinyProtocol protocol;

    private float lastPitch;
    private float lastYaw;

    private Class<?> playerLookClass = Reflection.getClass("{nms}.PacketPlayInFlying$PacketPlayInLook");
    private Reflection.FieldAccessor<Float> playerPitch = Reflection.getField(playerLookClass, float.class, 0);
    private Reflection.FieldAccessor<Float> playerYaw = Reflection.getField(playerLookClass, float.class, 1);

    private Class<?> playerInteractClass = Reflection.getClass("{nms}.PacketPlayInUseEntity");
    private Class<?> enumEntityUseAction = Reflection.getClass("{nms}.PacketPlayInUseEntity$EnumEntityUseAction");
//    private Reflection.FieldAccessor<Enum> entityUseAction = Reflection.getField(playerInteractClass, "action", Enum.class);
    private Object[] enumEntityUseActionValues = enumEntityUseAction.getEnumConstants();

    private Class<?> playerDismountClass = Reflection.getClass("{nms}.PacketPlayInSteerVehicle");
    private Reflection.FieldAccessor<Boolean> playerDismount = Reflection.getField(playerDismountClass, "d", boolean.class);

    public ArtistPipeline(Artiste plugin, final Player player, final Easel easel) {
        this.plugin = plugin;
        this.player = player;
        this.easel = easel;

        protocol = new TinyProtocol(plugin) {
            @Override
            public Object onPacketOutAsync(Player reciever, Channel channel, Object packet) {
                return super.onPacketOutAsync(reciever, channel, packet);
            }

            @Override
            public Object onPacketInAsync(Player sender, Channel channel, Object packet) {

                if (playerLookClass.isInstance(packet)) {
                    float pitch = playerPitch.get(packet);
                    float yaw = playerYaw.get(packet);

                    if (lastPitch != playerPitch.get(packet)) {
                        lastPitch = pitch;
                        Bukkit.getLogger().info(playerPitch.get(packet).toString());
                    }

                    if (lastYaw != playerYaw.get(packet)) {
                        lastYaw = yaw;
                        Bukkit.getLogger().info(playerYaw.get(packet).toString());
                    }
                    return packet;

                } else if (playerInteractClass.isInstance(packet)) {
                    Bukkit.getLogger().info("yo");
                    try {
                        Field actionField = playerInteractClass.getField("action");
                        Bukkit.getLogger().info(actionField.get(packet).toString());
                        Bukkit.getLogger().info(actionField.getType().toString());
                    } catch (NoSuchFieldException|IllegalAccessException e) {
                        Bukkit.getLogger().warning(e.getMessage());
                    }

                    if (player.getItemInHand().getType() == Material.INK_SACK) {
                        setCanvasPixel(player.getItemInHand().getDurability());
                    }
                    return null;

                } else if (playerDismountClass.isInstance(packet)) {

                    if (playerDismount.get(packet)) {
                        Bukkit.getLogger().info("Uninjected " + player.getName());
                        uninjectPlayer(player);
                        return packet;
                    }
                }
                return super.onPacketInAsync(sender, channel, packet);
            }
        };
        Bukkit.getLogger().info("Injected " + player.getName());
        protocol.injectPlayer(player);
    }

    private void setCanvasPixel(short colour) {
    }

    public Artiste getPlugin() {
        return plugin;
    }

    public Player getPlayer() {
        return player;
    }
}