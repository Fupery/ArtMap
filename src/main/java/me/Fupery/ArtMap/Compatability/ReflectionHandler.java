package me.Fupery.ArtMap.Compatability;

import io.netty.channel.Channel;
import org.bukkit.entity.Player;

public interface ReflectionHandler extends CompatibilityHandler {

    Channel getPlayerChannel(Player player) throws ReflectiveOperationException;
}
