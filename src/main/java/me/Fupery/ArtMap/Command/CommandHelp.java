package me.Fupery.ArtMap.Command;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.Recipe.ArtMaterial;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.HashMap;

import static me.Fupery.ArtMap.Utils.Formatting.helpLine;

public class CommandHelp extends ArtMapCommand {

    private final TextComponent commands = getCommands();
    private final TextComponent adminRecipes = getRecipes(true);
    private final TextComponent playerRecipes = getRecipes(false);

    CommandHelp(ArtMap plugin) {
        super(null, "/artmap help", true);
        this.plugin = plugin;
    }

    @Override
    public boolean runCommand(final CommandSender sender, String[] args, ReturnMessage msg) {

        final MultiChatMessage header = new MultiChatMessage("§7░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░",
                ArtMap.Lang.HELP_HEADER.message());
        boolean admin = (sender.hasPermission("artmap.admin"));

        MultiChatMessage commands = new MultiChatMessage(this.commands);
        MultiChatMessage body = new MultiChatMessage(getRecipeText(admin));
//        MultiChatMessage recipes = new MultiChatMessage(admin ? adminRecipes : playerRecipes);

        header.add(body).add(commands);
        Bukkit.getScheduler().runTask(plugin, new Runnable() {
            @Override
            public void run() {
                header.send(sender);
            }
        });
        return true;
    }

    private TextComponent getCommands() {

        TextComponent commandList = new TextComponent(ChatColor.GOLD + "Commands: ");

        HashMap<String, String> commands = new HashMap<>();
        commands.put("save", helpLine("/artmap save <title>", "save your artwork"));
        commands.put("delete", helpLine("/artmap delete <title>", "delete your artwork"));
        commands.put("preview", helpLine("/artmap preview <title>", "preview an artwork"));
        commands.put("list", helpLine("/artmap list [player|all] [pg]", "list artworks"));
        TextComponent[] items = new TextComponent[commands.size()];

        String[] keys = commands.keySet().toArray(new String[commands.size()]);

        for (int i = 0; i < items.length; i++) {

            String title = keys[i];
            items[i] = ChatMessage.getChatButton(title, commands.get(keys[i]), null);
            commandList.addExtra(items[i]);
        }
        return commandList;
    }

    private TextComponent getRecipeText(boolean admin) {

        TextComponent leader = new TextComponent(ChatColor.DARK_AQUA + "Click on the items below to view their recipe§l ⤸\n");
        TextComponent[] text = new TextComponent[]{
                new TextComponent("§7Craft an "), getButtonFromRecipe(admin, "Easel"),
                new TextComponent(" §7and "), getButtonFromRecipe(admin, "Canvas"),
                new TextComponent(" §7to create artworks.\n" + "§7Use dyes §7and"),
                getButtonFromRecipe(admin, "Paint_Bucket"), new TextComponent(
                "§7to paint on your canvas."), new TextComponent("\n§7You can craft " +
                "your artworks with Maps to duplicate them,\n§7or combine them with a"),
                getButtonFromRecipe(admin, "Carbon_Paper"), new TextComponent("§7to edit them.")};

        for (TextComponent textComponent : text) {
            leader.addExtra(textComponent);
        }
        return new TextComponent(leader);
    }

    private static TextComponent getButtonFromRecipe(boolean admin, String keyWord) {

        String prettyTitle = keyWord.replace('_', ' ');

        String hoverMessage = admin ? ArtMap.Lang.GET_ITEM.rawMessage()
                : ArtMap.Lang.RECIPE_HOVER.rawMessage();

        return ChatMessage.getChatButton(prettyTitle,
                String.format(hoverMessage, prettyTitle), "/artmap recipe "+ keyWord);
    }

    private TextComponent getRecipes(boolean admin) {
        String hoverMessage = admin ?
                ArtMap.Lang.GET_ITEM.rawMessage() : ArtMap.Lang.RECIPE_HOVER.rawMessage();

        ArtMaterial[] recipes = ArtMaterial.values(true);

        TextComponent footer = new TextComponent(ChatColor.GOLD + "Recipes: ");
        TextComponent[] items = new TextComponent[recipes.length];

        for (int i = 0; i < items.length; i++) {
            String title = recipes[i].name().toLowerCase();
            String prettyTitle = title.replace('_', ' ');
            items[i] = ChatMessage.getChatButton(prettyTitle,
                    String.format(hoverMessage, prettyTitle),
                    "/artmap recipe "+ title);
            footer.addExtra(items[i]);
        }
        return footer;
    }
}
