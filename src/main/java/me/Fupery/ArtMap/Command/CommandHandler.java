package me.Fupery.ArtMap.Command;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.Config.Lang;
import me.Fupery.ArtMap.Event.PlayerOpenMenuEvent;
import me.Fupery.ArtMap.Menu.Handler.MenuHandler;
import me.Fupery.ArtMap.Recipe.ArtMaterial;
import me.Fupery.ArtMap.Utils.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

public class CommandHandler implements CommandExecutor {

    private final HashMap<String, AsyncCommand> commands;

    public CommandHandler() {
        commands = new HashMap<>();
        //Commands go here - note that they are run on an async thread

        commands.put("save", new CommandSave());

        commands.put("delete", new CommandDelete());

        commands.put("preview", new CommandPreview());

        commands.put("restore", new CommandRestore());

        commands.put("give", new AsyncCommand("artmap.admin", "/artmap give <player> <easel|canvas> [amount]", true) {
            @Override
            public void runCommand(CommandSender sender, String[] args, ReturnMessage msg) {
                Player player = Bukkit.getPlayer(args[1]);
                if (player != null) {
                    ArtMaterial material;
                    if (args[2].equalsIgnoreCase("easel")) material = ArtMaterial.EASEL;
                    else if (args[2].equalsIgnoreCase("canvas")) material = ArtMaterial.CANVAS;
                    else {
                        sender.sendMessage(Lang.PREFIX + ChatColor.RED + this.usage);
                        return;
                    }
                    ItemStack item = material.getItem();
                    if (args.length > 3) {
                        int amount = Integer.parseInt(args[3]);
                        if (amount > 1) item.setAmount(amount);
                    }
                    ArtMap.getTaskManager().SYNC.run(() -> ItemUtils.giveItem(player, item));
                    return;
                }
                sender.sendMessage(String.format(Lang.PLAYER_NOT_FOUND.get(), args[1]));
            }
        });

        //convenience commands
        commands.put("help", new AsyncCommand("artmap.menu", "/artmap [help]", true) {
            @Override
            public void runCommand(CommandSender sender, String[] args, ReturnMessage msg) {
                if (sender instanceof Player) {
                    ArtMap.getTaskManager().SYNC.run(() -> {
                        if (args.length > 0 & sender.hasPermission("artmap.admin")) {
                            Lang.Array.CONSOLE_HELP.send(sender);
                        }
                        PlayerOpenMenuEvent event = new PlayerOpenMenuEvent((Player) sender);
                        Bukkit.getServer().getPluginManager().callEvent(event);
                        MenuHandler menuHandler = ArtMap.getMenuHandler();
                        menuHandler.openMenu(((Player) sender), menuHandler.MENU.HELP.get(((Player) sender)));
                    });
                } else {
                    Lang.Array.CONSOLE_HELP.send(sender);
                }
            }
        });
        commands.put("reload", new AsyncCommand("artmap.admin", "/artmap reload", true) {
            @Override
            public void runCommand(CommandSender sender, String[] args, ReturnMessage msg) {
                ArtMap.getTaskManager().SYNC.run(() -> {
                    JavaPlugin plugin = ArtMap.instance();
                    plugin.onDisable();
                    plugin.onEnable();
                    sender.sendMessage(Lang.PREFIX + ChatColor.GREEN + "Successfully reloaded ArtMap!");
                });
            }
        });
    }

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {

        if (args.length > 0) {

            if (commands.containsKey(args[0].toLowerCase())) {
                commands.get(args[0].toLowerCase()).runPlayerCommand(sender, args);

            } else {
                Lang.HELP.send(sender);
            }

        } else {
            commands.get("help").runPlayerCommand(sender, args);
        }
        return true;
    }

}