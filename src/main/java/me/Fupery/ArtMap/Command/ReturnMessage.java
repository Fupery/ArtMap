package me.Fupery.ArtMap.Command;

import me.Fupery.ArtMap.Utils.Formatting;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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

    private TextComponent[] lines;
    private TextComponent footer;

    MultiLineReturnMessage(CommandSender sender, String header) {
        super(sender, header);
        lines = null;
        footer = null;
    }

    @Override
    public void run() {
        sender.sendMessage(Formatting.seperator);
        sender.sendMessage(message);

        for (TextComponent line : lines) {
            sendMessage(line);
        }

        if (footer != null) {
            sendMessage(footer);
        }
    }

    public void setLines(TextComponent[] lines) {
        this.lines = lines;
    }

    public void setLines(String[] lines) {
        this.lines = new TextComponent[lines.length];

        for (int i = 0; i < lines.length; i++) {
            this.lines[i] = new TextComponent(lines[i]);
        }
    }

    public void setFooter(TextComponent footer) {
        this.footer = footer;
    }

    public void sendMessage(TextComponent line) {

        if (line != null) {

            if (sender instanceof Player) {
                ((Player) sender).spigot().sendMessage(line);

            } else {
                sender.sendMessage(line.getText());
            }
        }
    }
}

