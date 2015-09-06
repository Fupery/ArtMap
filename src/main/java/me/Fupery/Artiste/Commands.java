package me.Fupery.Artiste;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.concurrent.ConcurrentHashMap;

class Commands implements CommandExecutor {

    private Artiste plugin;

    public Commands(Artiste plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof Player && args.length > 0) {

            final Player player = (Player) sender;

            if (args[0].equalsIgnoreCase("save")) {

                if (args.length > 1) {

                    if (player.hasPermission("artiste.artist")) {

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
                            player.sendMessage("Now punch the easel again to save your artwork");
                        }

                    } else {
                        player.sendMessage("no perm");
                    }

                } else {
                    player.sendMessage("usage");
                }
                return true;

            } else if (args[0].equalsIgnoreCase("delete")) {

                if (player.hasPermission("artiste.admin")) {
                    //delete artwork
                }
            }
        }
        return true;
    }
}
