package me.Fupery.ArtMap.Command;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.Recipe.ArtMaterial;
import me.Fupery.ArtMap.Utils.Preview;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class CommandRecipe extends ArtMapCommand {

    public CommandRecipe(ArtMap plugin) {
        super(null, "/artmap recipe <item>", false);
        this.plugin = plugin;
    }

    @Override
    public boolean runCommand(CommandSender sender, String[] args, ReturnMessage msg) {

        Player player = (Player) sender;

        for (ArtMaterial recipe : ArtMaterial.values()) {

            if (args[1].equalsIgnoreCase(recipe.name())) {

                if (player.hasPermission("artmap.admin")) {
                    ItemStack leftOver =
                            player.getInventory().addItem(recipe.getItem()).get(0);

                    if (leftOver != null) {
                        player.getWorld().dropItemNaturally(player.getLocation(), leftOver);
                    }

                } else {
                    Preview.inventory(plugin, player, recipePreview(player, recipe));
                    player.updateInventory();
                }
            }
        }
        return false;
    }

    public static Inventory recipePreview(Player player, ArtMaterial recipe) {
        ItemStack[] ingredients = recipe.getPreview();

        Inventory inventory = Bukkit.createInventory(player, InventoryType.WORKBENCH,
                String.format(ArtMap.Lang.RECIPE_HEADER.rawMessage(),
                        recipe.name().toLowerCase()));

        for (int i = 0; i < ingredients.length; i++) {
            inventory.setItem(i + 1, ingredients[i]);
        }
        inventory.setItem(0, recipe.getItem());
        return inventory;
    }
}
