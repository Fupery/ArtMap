package me.Fupery.ArtMap.Command;

import me.Fupery.ArtMap.ArtMap;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import static me.Fupery.ArtMap.Utils.Formatting.playerError;

interface AbstractCommand {
    boolean runCommand(CommandSender sender, String[] args, ReturnMessage msg);
}

abstract class ArtMapCommand implements AbstractCommand {

    private final AbstractCommand ArtMapCommand = this;
    ArtMap plugin;
    String usage;
    private String permission;
    private int minArgs;
    private int maxArgs;

    ArtMapCommand(String permission, int minArgs, int maxArgs,
                  String usage) {
        this.permission = permission;
        this.minArgs = minArgs;
        this.maxArgs = maxArgs;

        if (usage != null) {
            this.usage = usage;

        } else {
            this.usage = null;
        }
    }

    void runPlayerCommand(final CommandSender sender, final String args[]) {

        Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {

            @Override
            public void run() {

                ReturnMessage returnMsg = new ReturnMessage(sender, null);

                if (permission == null || sender.hasPermission(permission)) {

                    if (args.length >= minArgs && args.length <= maxArgs) {

                        if (ArtMapCommand.runCommand(sender, args, returnMsg)) {
                            return;
                        }

                    } else {
                        returnMsg.message = playerError(usage);
                    }

                } else {
                    returnMsg.message = ArtMap.Lang.NO_PERM.message();
                }

                if (returnMsg.message != null) {
                    Bukkit.getScheduler().runTask(plugin, returnMsg);
                }
            }
        });
    }
}

