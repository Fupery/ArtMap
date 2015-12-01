package me.Fupery.ArtMap.Command;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.Recipe.ArtMaterial;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static me.Fupery.ArtMap.Utils.Formatting.helpLine;

public class CommandHelp extends ArtMapCommand {

    private final String[] text = getLines();
    private final TextComponent adminFooter = getFooter(true);
    private final TextComponent playerFooter = getFooter(false);

    CommandHelp(ArtMap plugin) {
        super(null, "/artmap help", true);
        this.plugin = plugin;
    }

    @Override
    public boolean runCommand(CommandSender sender, String[] args, ReturnMessage msg) {

        MultiLineReturnMessage multiMsg =
                new MultiLineReturnMessage(sender, ArtMap.Lang.HELP_HEADER.message());

        multiMsg.setLines(text);

        if (sender instanceof Player) {
            multiMsg.setFooter(getFooter(sender.hasPermission("artmap.admin")));
        }
        Bukkit.getScheduler().runTask(plugin, multiMsg);
        return true;
    }

    private String[] getLines() {
        return new String[]{
                helpLine("/artmap save <title>", "save your artwork"),
                helpLine("/artmap delete <title>", "delete your artwork"),
                helpLine("/artmap preview <title>", "preview an artwork"),
                helpLine("/artmap list [player|all] [pg]", "list artworks"),
                ArtMap.Lang.HELP_MESSAGE.rawMessage()};
    }

    private TextComponent getFooter(boolean admin) {
        String hoverMessage = admin ?
                ArtMap.Lang.GET_ITEM.rawMessage() : ArtMap.Lang.RECIPE_HOVER.rawMessage();

        ArtMaterial[] recipes = ArtMaterial.values(true);

        TextComponent footer = new TextComponent(ChatColor.AQUA + "Recipes: ");
        TextComponent[] items = new TextComponent[recipes.length];

        for (int i = 0; i < items.length; i++) {
            String title = recipes[i].name().toLowerCase();
            items[i] = new TextComponent(String.format(ArtMap.Lang.RECIPE_BUTTON.rawMessage(),
                    title) + ChatColor.GOLD + " | ");
            items[i].setClickEvent(
                    new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/artmap get " + title));
            items[i].setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                    new ComponentBuilder(String.format(hoverMessage, title)).create()));
            footer.addExtra(items[i]);
        }
        return footer;
    }
}
