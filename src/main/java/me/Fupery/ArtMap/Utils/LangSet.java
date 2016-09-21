package me.Fupery.ArtMap.Utils;

import org.bukkit.command.CommandSender;

interface LangSet<T> {
    void send(CommandSender sender);

    T get();
}
