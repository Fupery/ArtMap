package me.Fupery.ArtMap.Command;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.Utils.Lang;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public abstract class Command {

    private final String permission;
    private final boolean consoleAllowed;
<<<<<<< HEAD
    private final String usage;
=======
    private String usage;
>>>>>>> master
    private int minArgs;
    private int maxArgs;

    Command(String permission, String usage, boolean consoleAllowed) {
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

        ArtMap.runTaskAsync(new Runnable() {
            @Override
            public void run() {
                ReturnMessage returnMsg = new ReturnMessage(sender, null);

                if (permission != null && !sender.hasPermission(permission)) {
                    returnMsg.message = Lang.NO_PERM.message();

                } else if (!consoleAllowed && !(sender instanceof Player)) {
                    returnMsg.message = Lang.NO_CONSOLE.message();

                } else if (args.length < minArgs || args.length > maxArgs) {
                    returnMsg.message = Lang.prefix + ChatColor.RED + " " + usage;

                } else {
                    runCommand(sender, args, returnMsg);
                }

                if (returnMsg.message != null) {
                    ArtMap.runTask(returnMsg);
                }
            }
        });
    }

    public abstract void runCommand(CommandSender sender, String[] args, ReturnMessage msg);
}

