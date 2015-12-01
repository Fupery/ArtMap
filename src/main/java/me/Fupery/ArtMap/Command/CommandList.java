package me.Fupery.ArtMap.Command;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.IO.MapArt;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static me.Fupery.ArtMap.Utils.Formatting.extractListTitle;
import static me.Fupery.ArtMap.Utils.Formatting.playerError;

public class CommandList extends ArtMapCommand {

    CommandList(ArtMap plugin) {
        super(null, "/artmap list [playername|all] [pg]", true);
        this.plugin = plugin;
    }

    @Override
    public boolean runCommand(CommandSender sender, String[] args, ReturnMessage msg) {
        String artist;
        int pg = 1;

        //checks args are valid
        if (args.length > 1) {
            artist = args[1];

        } else {
            artist = (sender instanceof Player) ? sender.getName() : "all";
        }
        if (args.length == 3) {

            for (char c : args[2].toCharArray()) {

                if (!Character.isDigit(c)) {
                    msg.message = playerError(usage);
                    return false;
                }
            }
            pg = Integer.parseInt(args[2]);
        }

        //fetches artworks by 'artist'
        String[] list = MapArt.listArtworks(plugin, artist.toLowerCase());

        //returns if no artworks found
        if (list == null || list.length == 0) {
            msg.message = String.format(ArtMap.Lang.NO_ARTWORKS_FOUND.message(), artist);
            return false;
        }

        //index of the last page
        int totalPages = (list.length / 8) + 1;

        if (pg > totalPages) {
            pg = totalPages;

        } else if (pg < 1) {
            pg = 1;
        }

        MultiLineReturnMessage multiMsg = new MultiLineReturnMessage(sender,
                String.format(ArtMap.Lang.LIST_HEADER.message(), artist));

        TextComponent[] lines = new TextComponent[8];
        int msgIndex = (pg - 1) * 8;
        String title, cmd, hover;

        for (int i = 0; i < lines.length && (i + msgIndex) < list.length; i++) {
            title = extractListTitle(list[i + msgIndex]);
            cmd = String.format("/artmap preview %s", title);
            hover = String.format(ArtMap.Lang.LIST_LINE_HOVER.rawMessage(), title);

            lines[i] = new TextComponent(list[i + msgIndex]);
            lines[i].setHoverEvent(
                    new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                            new ComponentBuilder(hover).create()));
            lines[i].setClickEvent(
                    new ClickEvent(ClickEvent.Action.RUN_COMMAND, cmd));
        }

        //footer shows current page number, footerButton links to next page
        TextComponent footer = new TextComponent(String.format(
                ArtMap.Lang.LIST_FOOTER_PAGE.rawMessage(), pg, totalPages));

        //attaches a clickable button to open the next page to the footer
        if (totalPages > pg) {
            TextComponent footerButton = new TextComponent(
                    ArtMap.Lang.LIST_FOOTER_BUTTON.rawMessage());
            cmd = String.format("/artmap list %s %s", artist, pg + 1);

            footerButton.setClickEvent(
                    new ClickEvent(ClickEvent.Action.RUN_COMMAND, cmd));
            footerButton.setHoverEvent(
                    new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(
                            ArtMap.Lang.LIST_FOOTER_NXT.rawMessage()).create()));
            footer.addExtra(footerButton);
        }
        multiMsg.setLines(lines);
        multiMsg.setFooter(footer);

        //sends the list to the player
        Bukkit.getScheduler().runTask(plugin, multiMsg);
        return true;
    }
}
