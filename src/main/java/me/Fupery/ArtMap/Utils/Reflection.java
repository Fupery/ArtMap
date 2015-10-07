package me.Fupery.ArtMap.Utils;

import io.netty.channel.Channel;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class Reflection {


    public static Channel getPlayerConnection(Player player) {
        Channel channel;

        try {
            Method getHandle = player.getClass().getMethod("getHandle");
            Object playerHandle = getHandle.invoke(player);

            Field playerConnectionField = playerHandle.getClass().getField("playerConnection");
            Object playerConnection = playerConnectionField.get(playerHandle);

            Field networkManagerField = playerConnection.getClass().getField("networkManager");
            Object networkManager = networkManagerField.get(playerConnection);

            Field channelField = networkManager.getClass().getField("channel");
            channel = ((Channel) channelField.get(networkManager));

        } catch (Exception ignored) {
            ignored.printStackTrace();
            channel = null;
        }
        return channel;
    }
}
