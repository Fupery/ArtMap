package me.Fupery.ArtMap.Command;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.Easel.Easel;
import me.Fupery.ArtMap.IO.MapArt;
import me.Fupery.ArtMap.IO.TitleFilter;
import me.Fupery.ArtMap.Recipe.ArtMaterial;
import me.Fupery.ArtMap.Utils.LocationTag;
import me.Fupery.ArtMap.Utils.Preview;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

import static me.Fupery.ArtMap.Utils.Formatting.*;

public class ArtMapCommandExecutor implements CommandExecutor {

    private final ArtMap plugin;
    private HashMap<String, ArtMapCommand> commands;

    public ArtMapCommandExecutor(final ArtMap plugin) {
        this.plugin = plugin;
        commands = new HashMap<>();
        //Commands go here - note that they are run on an async thread

        commands.put("save", new CommandSave(plugin));

        commands.put("delete", new CommandDelete(plugin));

        commands.put("preview", new CommandPreview(plugin));

        commands.put("list", new CommandList(plugin));

        commands.put("help", new CommandHelp(plugin));

        commands.put("get", new CommandGet(plugin));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length > 0) {

            if (commands.containsKey(args[0].toLowerCase())) {
                commands.get(args[0].toLowerCase()).runPlayerCommand(sender, args);

            } else {
                sender.sendMessage(playerError("/artmap help for a list of commands."));
            }

        } else {
            commands.get("help").runPlayerCommand(sender, args);
        }
        return true;
    }

    public ArtMap getPlugin() {
        return plugin;
    }
}