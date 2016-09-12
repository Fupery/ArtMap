package me.Fupery.ArtMap.Command;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.IO.MapArt;
import me.Fupery.ArtMap.Menu.Handler.MenuHandler;
import me.Fupery.ArtMap.Utils.GenericMapRenderer;
import me.Fupery.ArtMap.Utils.Lang;
import me.Fupery.ArtMap.Utils.Reflection;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
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

        //convenience commands
        commands.put("help", new AsyncCommand(null, "/artmap [help]", true) {
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
        commands.put("restore", new AsyncCommand("artmap.admin", "/artmap restore <title>", false) {
            @Override
            public void runCommand(CommandSender sender, String[] args, ReturnMessage msg) {
                MapArt art = ArtMap.getArtDatabase().getArtwork(args[1]);
                if (art == null) {
                    sender.sendMessage(String.format(ArtMap.getLang().getMsg("MAP_NOT_FOUND"), args[1]));
                } else {
                    byte[] map = ArtMap.getArtDatabase().getMap(art.getTitle());
                    ArtMap.getTaskManager().SYNC.run(() -> {
                        MapView mapView = Bukkit.getMap(art.getMapId());
                        if (mapView == null) {
                            mapView = Bukkit.createMap(((Player) sender).getWorld());
                            final MapView finalMapView = mapView;
                            ArtMap.getTaskManager().ASYNC.run(() -> {
                                ArtMap.getArtDatabase().updateMapID(art.updateMapId(finalMapView.getId()));
                            });
                            sender.sendMessage(ArtMap.getLang().getMsg("MISSING_MAP_ID"));
                        }
                        int id = mapView.getId();
                        ArtMap.getMapManager().overrideMap(mapView, map);
                        sender.sendMessage(String.format(
                                ArtMap.getLang().getMsg("RESTORED_SUCCESSFULY"), art.getTitle(), id));
                    });
                }
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