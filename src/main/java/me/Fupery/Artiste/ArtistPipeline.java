package me.Fupery.Artiste;

import com.comphenix.tinyprotocol.Reflection;
import com.comphenix.tinyprotocol.TinyProtocol;
import io.netty.channel.Channel;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

public class ArtistPipeline {

    private Artiste plugin;
    private Player player;
    private TinyProtocol protocol;

    private float lastPitch;
    private float lastYaw;
    private DyeColor queuedPixel = null;

    private CanvasRenderer renderer;

    private Class<?> playerLookClass = Reflection.getClass("{nms}.PacketPlayInFlying$PacketPlayInLook");
    private Reflection.FieldAccessor<Float> playerPitch = Reflection.getField(playerLookClass, float.class, 0);
    private Reflection.FieldAccessor<Float> playerYaw = Reflection.getField(playerLookClass, float.class, 1);

    private Class<?> playerSwingArmClass = Reflection.getClass("{nms}.PacketPlayInArmAnimation");

    private Class<?> playerInteractClass = Reflection.getClass("{nms}.PacketPlayInUseEntity");

    private Class<?> playerDismountClass = Reflection.getClass("{nms}.PacketPlayInSteerVehicle");
    private Reflection.FieldAccessor<Boolean> playerDismount = Reflection.getField(playerDismountClass, "d", boolean.class);

    public ArtistPipeline(Artiste plugin, final Player player, final Easel easel) {
        this.plugin = plugin;
        this.player = player;
        getMapRenderer(easel.getFrame());

        protocol = new TinyProtocol(plugin) {
            @Override
            public Object onPacketOutAsync(Player reciever, Channel channel, Object packet) {
                return super.onPacketOutAsync(reciever, channel, packet);
            }

            @Override
            public Object onPacketInAsync(Player sender, Channel channel, Object packet) {

                if (player == sender) {

                    if (playerLookClass.isInstance(packet)) {
                        float pitch = playerPitch.get(packet);
                        float yaw = playerYaw.get(packet);

                        if (lastPitch != pitch) {
                            lastPitch = pitch;
//                            Bukkit.getLogger().info("pitch: " + (int) pitch);
                        }

                        if (lastYaw != yaw) {
                            lastYaw = yaw;
//                            Bukkit.getLogger().info("yaw: " + (int) yaw);
                        }
                        if (queuedPixel != null) {

                            if (renderer != null) {
//                                renderer.addPixel(compensateValue(lastPitch), compensateValue(lastYaw), queuedPixel);

                                queuedPixel = null;
                            }
                        }
                        return packet;

                    } else if (playerInteractClass.isInstance(packet) || playerSwingArmClass.isInstance(packet)) {

                        if (player.getItemInHand().getType() == Material.INK_SACK) {
                            queuedPixel = DyeColor.getByData((byte) (15 - player.getItemInHand().getDurability()));

                            if (renderer != null) {
                                byte[] pixel = getPixel(lastPitch, lastYaw);
                                renderer.addPixel(pixel[0], pixel[1], queuedPixel);
                            }
//                            Bukkit.getLogger().info("click");

                        }
                        return null;

                    } else if (playerDismountClass.isInstance(packet)) {

                        if (playerDismount.get(packet)) {
                            uninjectPlayer(player);
                            player.leaveVehicle();
                            easel.setIsPainting(false);
                            Bukkit.getLogger().info("Uninjected " + player.getName());
                            easel.getSeat().remove();
                            return null;
                        }
                    }
                }
                return super.onPacketInAsync(sender, channel, packet);
            }
        };
        Bukkit.getLogger().info("Injected " + player.getName());
        protocol.injectPlayer(player);
    }

    private void queueCanvasPixel(DyeColor color) {
        queuedPixel = color;
    }

    private void getMapRenderer(ItemFrame frame) {

        if (frame.getItem().getType() == Material.MAP) {
            MapView mapView = Bukkit.getMap(frame.getItem().getDurability());

            for (MapRenderer r : mapView.getRenderers()) {

                if (r instanceof CanvasRenderer) {
                    renderer = ((CanvasRenderer) r);
                    Bukkit.getLogger().info("found renderer");
                }
            }
        }
    }

    public Artiste getPlugin() {
        return plugin;
    }

    public Player getPlayer() {
        return player;
    }

    private static byte[] getPixel(float pitch, float yaw) {
        byte[] pixel = new byte[2];

        if (pitch > 0) {
            pitch -= 180;
        } else {
            pitch += 180;
        }

        double yawAdjust = ((0.0044 * pitch * pitch) - (0.0075 * pitch)) * (0.0265 * yaw);

        if (yaw > 0) {

            if (yawAdjust > 0) {
                yaw += yawAdjust;
            }

        } else if (yaw < 0) {

            if (yawAdjust < 0) {
                yaw += yawAdjust;
            }
        }
        pixel[0] = ((byte) ((Math.tan(Math.toRadians(pitch)) * .6155 * 128) + 64));
        pixel[1] = ((byte) ((Math.tan(Math.toRadians(yaw)) * .6155 * 128) + 64));
        return pixel;
    }
}