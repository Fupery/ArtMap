package me.Fupery.ArtMap.Command;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.IO.MapArt;
import me.Fupery.ArtMap.IO.SQLiteDatabase;
import me.Fupery.ArtMap.Menu.Handler.MenuHandler;
import me.Fupery.ArtMap.Utils.Lang;
import me.Fupery.ArtMap.Utils.Reflection;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.map.MapView;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

public class CommandHandler implements CommandExecutor {

    private final HashMap<String, Command> commands;

    public CommandHandler() {
        commands = new HashMap<>();
        //Commands go here - note that they are run on an async thread

        commands.put("save", new CommandSave());

        commands.put("delete", new CommandDelete());

        commands.put("preview", new CommandPreview());

        //convenience commands
        commands.put("help", new Command(null, "/artmap [help]", true) {
            @Override
            public void runCommand(CommandSender sender, String[] args, ReturnMessage msg) {

                if (sender instanceof Player) {
                    ArtMap.getTaskManager().SYNC.run(() -> {
                        MenuHandler menuHandler = ArtMap.getMenuHandler();
                        menuHandler.openMenu(((Player) sender), menuHandler.MENU.HELP.get(((Player) sender)));
                    });


                } else {
                    ArtMap.getLang().sendArray("CONSOLE_HELP", sender);
                }
            }
        });
        commands.put("reload", new Command("artmap.admin", "/artmap restore", true) {
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
        commands.put("restore", new Command("artmap.admin", "/artmap restore <title>", true) {
            @Override
            public void runCommand(CommandSender sender, String[] args, ReturnMessage msg) {
                MapArt art = ArtMap.getArtDatabase().getArtwork(args[1]);
                ArtMap.getTaskManager().SYNC.run(() -> {

                    if (art == null) {
                        ArtMap.getLang().sendMsg("MAP_NOT_FOUND", sender);

                    } else {
                        byte[] map = ((SQLiteDatabase) ArtMap.getArtDatabase()).getMap(art.getTitle());
                        MapView mapView = Bukkit.getMap(art.getMapId());
                        if (mapView == null) {
                            mapView = Bukkit.createMap(((Player) sender).getWorld());
                            ((SQLiteDatabase) ArtMap.getArtDatabase()).updateMapID(art.updateMapId(mapView.getId()));
                            sender.sendMessage("Map ID not found - assigning new id!");// TODO: 8/09/2016 hardcoding
                        }
                        Reflection.setWorldMap(mapView, map);
                        sender.sendMessage("Restored Successfully!");// TODO: 8/09/2016 hardcoding
                    }

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
                sender.sendMessage(ArtMap.getLang().getMsg("HELP"));
            }

        } else {
            commands.get("help").runPlayerCommand(sender, args);
        }
        return true;
    }

}