package me.Fupery.ArtMap.Command;

import org.bukkit.command.CommandSender;

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

