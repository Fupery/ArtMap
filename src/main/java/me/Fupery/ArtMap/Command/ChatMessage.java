package me.Fupery.ArtMap.Command;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

class ChatMessage {

    TextComponent message;

    ChatMessage(String message) {
        this.message = new TextComponent(message);
    }

    ChatMessage(TextComponent message) {
        this.message = message;
    }

    public void send(CommandSender sender) {

        if (sender instanceof Player) {
            ((Player) sender).spigot().sendMessage(message);

        } else {
            sender.sendMessage(message.getText());
        }
    }
    public static TextComponent getChatButton(String text, String hoverText, String clickCommand) {
        TextComponent button = new TextComponent(String.format("§a﴾§2§l%s§a﴿", text));

        if (hoverText != null) {
            button.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                    new ComponentBuilder(hoverText).create()));
        }

        if (clickCommand != null) {
            button.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, clickCommand));
        }

        return button;
    }
}

class MultiChatMessage {

    TextComponent[] messages;

    MultiChatMessage(String ... messages) {
        this.messages = new TextComponent[messages.length];

        for (int i = 0; i < messages.length; i ++) {
            this.messages[i] = new TextComponent(messages[i]);
        }
    }

    MultiChatMessage(TextComponent ... messages) {
        this.messages = messages;
    }

    public MultiChatMessage add(MultiChatMessage message) {
        TextComponent[] additionalMessages = message.messages;
        int messageLength = messages.length, additionalLength = additionalMessages.length;
        TextComponent[] result = new TextComponent[messageLength + additionalLength];
        System.arraycopy(messages, 0, result, 0, messageLength);
        System.arraycopy(additionalMessages, 0, result, messageLength, additionalLength);
        messages = result;
        return this;
    }

    public void send(CommandSender sender) {

        for (TextComponent message : messages) {

            if (sender instanceof Player) {
                ((Player) sender).spigot().sendMessage(message);

            } else {
                sender.sendMessage(message.getText());
            }
        }
    }
}
