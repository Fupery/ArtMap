package me.Fupery.ArtMap.Preview;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import java.util.UUID;

public interface Preview {

    boolean start(Player player);

    boolean end(Player player);

    boolean isEventAllowed(UUID player, Event event);
}
