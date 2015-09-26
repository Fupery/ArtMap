package me.Fupery.Artiste.Command;

import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

import static me.Fupery.Artiste.Utils.Formatting.listFooterPage;
import static me.Fupery.Artiste.Utils.Formatting.seperator;

class ReturnMessage implements Runnable {

    protected CommandSender sender;
    protected String message;

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
    private TextComponent footer;

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

    public TextComponent getFooter() {
        return footer;
    }
}
