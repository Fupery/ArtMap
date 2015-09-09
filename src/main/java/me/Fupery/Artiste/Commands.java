package me.Fupery.Artiste;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.concurrent.ConcurrentHashMap;

import static me.Fupery.Artiste.Utils.Formatting.*;

public class Commands implements CommandExecutor {

    private Artiste plugin;
    private ArtisteCommand save, delete;

    public Commands(final Artiste plugin) {
        this.plugin = plugin;

        save = new ArtisteCommand("save", "artiste.artist",
                2, "/artmap save <title>", punchCanvas, this) {
            @Override
            public boolean run(CommandSender sender, String[] args) {

                if (sender instanceof Player){

                    final Player player = (Player) sender;
                    ConcurrentHashMap<Player, String> queue = plugin.getNameQueue();

                    if (queue != null) {

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
                return false;
            }
        };

        delete = new ArtisteCommand("delete", "artiste.admin",
                2, "/artmap delete <title>", deleted, this) {
            @Override
            public boolean run(CommandSender sender, String[] args) {
                //delete artwork
                return true;
            }
        };

    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length > 0) {

            if (args[0].equalsIgnoreCase("save")) {
                save.runPlayerCommand(sender, args);

            } else if (args[0].equalsIgnoreCase("delete")) {
                delete.runPlayerCommand(sender, args);
            }
        }
        return true;
    }

    public Artiste getPlugin() {
        return plugin;
    }
}
abstract class ArtisteCommand implements AbstractCommand {

    protected Artiste plugin;
    protected String command, permission, usage, success;
    protected int minArgs;

    protected ArtisteCommand(String command, String permission, int minArgs,
                             String usage, String success, Commands commands) {
        this.command = command;
        this.permission = permission;
        this.plugin = commands.getPlugin();
        this.minArgs = minArgs;

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

    void runPlayerCommand(CommandSender sender, String args[]) {

        if (permission != null && sender.hasPermission(permission)) {

            if (args.length >= minArgs) {

                if (this.run(sender, args)) {

                    if (success != null) {
                        sender.sendMessage(playerMessage(success));
                    }
                }

            } else {
                sender.sendMessage(playerError(usage));
            }

        } else {
            sender.sendMessage(playerError(noperm));
        }
    }
}
interface AbstractCommand {
    boolean run(CommandSender sender, String[] args);
}