package me.Fupery.ArtMap.Command;

import me.Fupery.ArtMap.ArtMap;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import static me.Fupery.ArtMap.Utils.Formatting.*;

interface AbstractCommand {
    boolean runCommand(CommandSender sender, String[] args, ReturnMessage msg);
}

abstract class ArtMapCommand implements AbstractCommand {

    private final AbstractCommand ArtMapCommand = this;
    ArtMap plugin;
    String usage;
    private String permission;
    private String success;
    private int minArgs;
    private int maxArgs;

    ArtMapCommand(String command, String permission, int minArgs, int maxArgs,
                  String usage, String success, CommandListener commands) {
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

                        if (ArtMapCommand.runCommand(sender, args, returnMsg)) {

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

