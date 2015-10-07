package me.Fupery.ArtMap.Command;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

import static me.Fupery.ArtMap.Utils.Formatting.*;

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
    private TextComponent[] lineButtons;
    private TextComponent footer;

    MultiLineReturnMessage(CommandSender sender, String message, int pages,
                           String[] msgs, boolean footer) {
        super(sender, message);
        lineButtons = null;
        addMessages(pages, msgs);
    }

    @Override
    public void run() {
        sender.sendMessage(seperator);
        sender.sendMessage(message);

        if (lineButtons != null && sender instanceof Player) {

            for (TextComponent line : lineButtons) {
                ((Player) sender).spigot().sendMessage(line);
            }

        } else {
            sender.sendMessage(messages);
        }

        if (footer != null && sender instanceof Player) {
            ((Player) sender).spigot().sendMessage(footer);
        }
    }

    public void makeLinesButtons(String lineHoverText, String lineClickCommand) {
        lineButtons = new TextComponent[messages.length];

        for (int i = 0; i < messages.length; i++) {
            String title = extractListTitle(messages[i]);
            String cmd = String.format(lineClickCommand, title);
            String hover = String.format(lineHoverText, title);

            lineButtons[i] = new TextComponent(messages[i]);
            lineButtons[i].setHoverEvent(
                    new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                            new ComponentBuilder(hover).create()));
            lineButtons[i].setClickEvent(
                    new ClickEvent(ClickEvent.Action.RUN_COMMAND, cmd));
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
