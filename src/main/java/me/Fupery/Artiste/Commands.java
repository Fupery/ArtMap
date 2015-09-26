package me.Fupery.Artiste;

import me.Fupery.Artiste.IO.MapArt;
import me.Fupery.Artiste.Utils.TitleFilter;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import static me.Fupery.Artiste.Utils.Formatting.*;

interface AbstractCommand {
    boolean runCommand(CommandSender sender, String[] args, ReturnMessage msg);
}

public class Commands implements CommandExecutor {

    private Artiste plugin;
    private HashMap<String, ArtisteCommand> commands;

    public Commands(final Artiste plugin) {
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
                    artist = "all";
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
                                playerMessage(String.format(listHeader, artist))
                                , pg, list, true);

                if (footerButton != null) {
                    multiMsg.footer.addExtra(footerButton);
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

abstract class ArtisteCommand implements AbstractCommand {

    protected Artiste plugin;
    protected String command, permission, usage, success;
    protected int minArgs, maxArgs;
    protected final AbstractCommand artisteCommand = this;

    protected ArtisteCommand(String command, String permission, int minArgs, int maxArgs,
                             String usage, String success, Commands commands) {
        this.command = command;
        this.permission = permission;
        this.plugin = commands.getPlugin();
        this.minArgs = minArgs;
        this.maxArgs = maxArgs;
        commands.getCommands().put(command, this);

        if (usage != null) {
            this.usage = usage;

        } else {
            this.usage = null;
        }

        if (success != null) {
            this.success = success;

        } else {
            this.success = null;
        }
    }

    void runPlayerCommand(final CommandSender sender, final String args[]) {

        Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {

            @Override
            public void run() {

                ReturnMessage returnMsg = new ReturnMessage(sender, null);

                if (permission == null || sender.hasPermission(permission)) {

                    if (args.length >= minArgs && args.length <= maxArgs) {

                        if (artisteCommand.runCommand(sender, args, returnMsg)) {

                            if (success != null) {
                                returnMsg.message = playerMessage(success);
                            }
                        }

                    } else {
                        returnMsg.message = playerError(usage);
                    }

                } else {
                    returnMsg.message = playerError(noperm);
                }

                if (returnMsg.message != null) {
                    Bukkit.getScheduler().runTask(plugin, returnMsg);
                }
            }
        });
    }
}

class ReturnMessage implements Runnable {

    CommandSender sender;
    String message;

    ReturnMessage(CommandSender sender, String message) {
        this.sender = sender;
        this.message = message;
    }

    @Override
    public void run() {
        sender.sendMessage(message);
    }
}

class MultiLineReturnMessage extends ReturnMessage {

    private String[] messages;
    TextComponent footer;

    MultiLineReturnMessage(CommandSender sender, String message,
                           int pages, String[] msgs, boolean footer) {
        super(sender, message);
        messages = null;

        addMessages(pages, msgs);
    }

    @Override
    public void run() {
        sender.sendMessage(seperator);
        sender.sendMessage(message);
        sender.sendMessage(messages);

        if (footer != null && sender instanceof Player) {
            ((Player) sender).spigot().sendMessage(footer);
        }
    }

    private void addMessages(int pg, String[] msgs) {
        int i = pg * 8;
        String[] returnMsgs;

        if (msgs.length <= 8) {
            returnMsgs = msgs;

        } else {

            while (msgs.length < i && i >= 8) {
                i -= 8;
            }
            int k;

            if (msgs.length > i + 8) {
                k = i + 8;

            } else {
                k = msgs.length;
            }

            returnMsgs = Arrays.copyOfRange(msgs, i, k);
        }
        footer = new TextComponent(String.format(listFooterPage, i / 8, msgs.length / 8));
        messages = returnMsgs;
    }
}