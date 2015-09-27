package me.Fupery.Artiste.Command;

import me.Fupery.Artiste.Artiste;
import me.Fupery.Artiste.IO.MapArt;
import me.Fupery.Artiste.Utils.TitleFilter;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Art;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import static me.Fupery.Artiste.Utils.Formatting.*;

public class CommandListener implements CommandExecutor {

    private Artiste plugin;
    private HashMap<String, ArtisteCommand> commands;

    public CommandListener(final Artiste plugin) {
        this.plugin = plugin;
        commands = new HashMap<>();

        new ArtisteCommand("save", "artiste.artist",
                2, 2, "/artmap save <title>", null, this) {

            @Override
            public boolean runCommand(CommandSender sender, String[] args, ReturnMessage msg) {

                if (sender instanceof Player) {

                    final Player player = (Player) sender;
                    ConcurrentHashMap<Player, String> queue = plugin.getNameQueue();

                    if (!new TitleFilter(plugin, args[1]).check()) {
                        msg.message = playerError(badTitle);
                        return false;
                    }

                    MapArt art = MapArt.getArtwork(plugin, args[1]);

                    if (art != null) {
                        msg.message = playerError(titleUsed);
                        return false;
                    }

                    if (queue != null) {

                        msg.message = playerMessage(String.format(punchCanvas, args[1]));

                        if (queue.containsKey(player)) {
                            queue.remove(player);
                        }
                        queue.put(player, args[1]);

                        //timeout removes player from the queue after a delay
                        plugin.getServer().getScheduler().runTaskLaterAsynchronously(plugin, new Runnable() {
                            @Override
                            public void run() {

                                if (plugin.getNameQueue().containsKey(player)) {
                                    plugin.getNameQueue().remove(player);
                                }
                            }
                        }, 1200);
                        return true;
                    }
                }
                msg.message = playerError(playerOnly);
                return false;
            }
        };

        new ArtisteCommand("delete", null,
                2, 2, "/artmap delete <title>", null, this) {
            @Override
            public boolean runCommand(CommandSender sender, String[] args, ReturnMessage msg) {

                MapArt art = MapArt.getArtwork(plugin, args[1]);

                if (art != null && sender instanceof Player
                        && !art.getPlayer().getName().equalsIgnoreCase(sender.getName())
                        && !sender.hasPermission("artiste.admin")) {
                    msg.message = playerMessage(noperm);
                    return false;
                }

                if (MapArt.deleteArtwork(plugin, args[1])) {
                    msg.message = playerMessage(String.format(deleted, args[1]));
                    return true;

                } else {
                    msg.message = playerError(String.format(mapNotFound, args[1]));
                    return false;
                }
            }
        };

        new ArtisteCommand("preview", null, 2, 2, "/artmap preview <title>", null, this) {

            @Override
            public boolean runCommand(CommandSender sender, String[] args, ReturnMessage msg) {

                if (sender instanceof Player) {
                    Player player = (Player) sender;

                    if (player.getItemInHand().getType() == Material.AIR) {

                        MapArt art = MapArt.getArtwork(plugin, args[1]);

                        if (art != null) {

                            if (player.hasPermission("artiste.admin")) {

                                player.setItemInHand(art.getMapItem());

                            } else {
                                plugin.startPreviewing(((Player) sender), art);
                            }
                            msg.message = playerMessage(String.format(previewing, args[1]));
                            return true;

                        } else {
                            msg.message = playerError(String.format(mapNotFound, args[1]));
                        }

                    } else {
                        msg.message = playerMessage(emptyHandPreview);
                    }

                } else {
                    msg.message = playerError(playerOnly);
                }
                return false;
            }
        };

        new ArtisteCommand("list", null, 1, 3,
                "/artmap list [playername|all] [pg]", null, this) {
            @Override
            public boolean runCommand(CommandSender sender, String[] args, ReturnMessage msg) {
                String artist;
                int pg = 0;

                //checks args are valid
                if (args.length > 1) {
                    artist = args[1];

                } else {
                    artist = (sender instanceof Player) ? sender.getName() : "all";
                }
                if (args.length == 3) {

                    for (char c : args[2].toCharArray()) {

                        if (!Character.isDigit(c)) {
                            msg.message = playerError(usage);
                            return false;
                        }
                    }
                    pg = Integer.parseInt(args[2]);
                }

                //fetches artworks by 'artist'
                String[] list = MapArt.listArtworks(plugin, artist.toLowerCase());

                //returns if no artworks found
                if (list == null || list.length == 0) {
                    msg.message = playerError(String.format(noArtworksFound, artist));
                    return false;
                }
                //checks we are not on the last page
                int totalLength = list.length / 8;

                //footer shows current page number
                TextComponent footerButton = null;

                if (totalLength > pg) {

                    //attaches a clickable button to open the next page to the footer
                    footerButton = new TextComponent(listFooterButton);
                    String cmd = String.format("/artmap list %s %s", artist, pg + 1);

                    footerButton.setClickEvent(
                            new ClickEvent(ClickEvent.Action.RUN_COMMAND, cmd));
                    footerButton.setHoverEvent(
                            new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                    new ComponentBuilder(listFooterNxt).create()));
                }

                //builds the list
                MultiLineReturnMessage multiMsg =
                        new MultiLineReturnMessage(sender,
                                playerMessage(String.format(listHeader, artist)),
                                pg, list, true);
                multiMsg.makeLinesButtons(listLineHover, "/artmap preview %s");

                if (footerButton != null) {
                    multiMsg.getFooter().addExtra(footerButton);
                }

                //sends the list to the player
                Bukkit.getScheduler().runTask(plugin, multiMsg);
                return true;
            }
        };

        new ArtisteCommand("help", null, 1, 1, "/artmap help", null, this) {
            @Override
            public boolean runCommand(CommandSender sender, String[] args, ReturnMessage msg) {
                MultiLineReturnMessage multiMsg =
                        new MultiLineReturnMessage(sender, playerMessage(helpHeader), 0, new String[]{
                                helpLine("/artmap save <title>", "save your artwork"),
                                helpLine("/artmap delete <title>", "delete your artwork"),
                                helpLine("/artmap list [playername|all] [pg]", "list artworks"),
                                helpMessage
                        }, false);
                Bukkit.getScheduler().runTask(plugin, multiMsg);
                return true;
            }
        };
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length > 0) {

            if (commands.containsKey(args[0].toLowerCase())) {
                commands.get(args[0].toLowerCase()).runPlayerCommand(sender, args);
                return true;
            }
        }
        sender.sendMessage(playerError("/artmap help for a list of commands."));
        return true;
    }

    public HashMap<String, ArtisteCommand> getCommands() {
        return commands;
    }

    public Artiste getPlugin() {
        return plugin;
    }
}