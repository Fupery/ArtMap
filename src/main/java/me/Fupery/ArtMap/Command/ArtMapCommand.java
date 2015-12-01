package me.Fupery.ArtMap.Command;

import me.Fupery.ArtMap.ArtMap;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static me.Fupery.ArtMap.Utils.Formatting.playerError;

interface AbstractCommand {
    boolean runCommand(CommandSender sender, String[] args, ReturnMessage msg);
}

public abstract class ArtMapCommand implements AbstractCommand {

    String usage;
    ArtMap plugin;
    private AbstractCommand ArtMapCommand = this;
    private String permission;
    private int minArgs;
    private int maxArgs;
    private boolean consoleAllowed;

    ArtMapCommand(String permission, String usage, boolean consoleAllowed) {
        this.permission = permission;
        this.consoleAllowed = consoleAllowed;

        if (usage == null) {
            throw new IllegalArgumentException("Usage must not be null");
        }
        String[] args = usage.replace("/artmap ", "").split("\\s+");
        maxArgs = args.length;
        minArgs = maxArgs - StringUtils.countMatches(usage, "[");
        this.usage = usage;
    }

    void runPlayerCommand(final CommandSender sender, final String args[]) {

        Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {

            @Override
            public void run() {

                ReturnMessage returnMsg = new ReturnMessage(sender, null);

                if (permission != null && !sender.hasPermission(permission)) {
                    returnMsg.message = ArtMap.Lang.NO_PERM.message();

                } else if (!consoleAllowed && !(sender instanceof Player)) {
                    returnMsg.message = ArtMap.Lang.NO_CONSOLE.message();

                } else if (args.length < minArgs || args.length > maxArgs) {
                    returnMsg.message = playerError(usage);

                } else if (ArtMapCommand.runCommand(sender, args, returnMsg)) {
                    return;
                }

                if (returnMsg.message != null) {
                    Bukkit.getScheduler().runTask(plugin, returnMsg);
                }
            }
        });
    }
}

