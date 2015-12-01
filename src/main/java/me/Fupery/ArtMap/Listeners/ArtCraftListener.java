package me.Fupery.ArtMap.Listeners;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.IO.MapArt;
import me.Fupery.ArtMap.Recipe.ArtMaterial;
import me.Fupery.ArtMap.Recipe.ShapelessArtRecipe;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class ArtCraftListener implements Listener {

    private final ArtMap plugin;

    public ArtCraftListener(ArtMap plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onArtCraft(final InventoryClickEvent event) {
        final Inventory inventory = event.getClickedInventory();
        final CraftAction action = getAction(event);

        if (action == CraftAction.PLACE) {

            Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
                @Override
                public void run() {

                    ItemStack cursor = event.getCurrentItem();
                    ArtMaterial cursorMaterial = ArtMaterial.getCraftItemType(cursor);

                    if (cursor == null) {
                        return;
                    }
                    placeItem(cursor, cursorMaterial, event);
                }
            });

        } else if (action == CraftAction.CRAFT) {

            ArtMaterial cursor = ArtMaterial.getCraftItemType(event.getCurrentItem());

            if (cursor == null || !(cursor.getRecipe() instanceof ShapelessArtRecipe)) {
                return;
            }
            Bukkit.getScheduler().runTask(plugin, new Runnable() {
                @Override
                public void run() {
                    ItemStack result = event.getCursor().clone();
                    event.getWhoClicked().getOpenInventory().setCursor(result);
                    inventory.setContents(new ItemStack[inventory.getContents().length]);
                    ((Player) event.getWhoClicked()).updateInventory();
                }
            });
        }
    }

    private void placeItem(ItemStack cursor, ArtMaterial cursorMaterial, InventoryClickEvent event) {

        final ItemStack result = evaluateRecipes(processInventory(cursor,
                cursorMaterial, event.getClickedInventory()));

        if (result != null) {
            final Player player = (Player) event.getWhoClicked();
            final Inventory inventory = event.getClickedInventory();
            Bukkit.getScheduler().runTask(plugin, new Runnable() {
                @Override
                public void run() {
                    inventory.setItem(0, result);
                    inventory.getItem(0).setAmount(result.getAmount());
                    player.updateInventory();
                }
            });
        }
    }

    private HashMap<ArtMaterial, ItemStack> processInventory(
            ItemStack cursor, ArtMaterial cursorMaterial, Inventory inventory) {

        HashMap<ArtMaterial, ItemStack> contents = new HashMap<>();

        for (ItemStack itemStack : inventory.getContents()) {

            if (itemStack.getType() == Material.AIR) {
                continue;
            }
            ArtMaterial material = ArtMaterial.getCraftItemType(itemStack);

            if (material != null && !(contents.containsKey(material))) {
                contents.put(material, itemStack);

            } else {
                return null;
            }
        }
        contents.put(cursorMaterial, cursor);
        return contents;
    }

    private ItemStack evaluateRecipes(HashMap<ArtMaterial, ItemStack> materials) {

        if (materials == null) {
            return null;
        }
        ArtMaterial resultMaterial = null;

        recipes:
        for (ArtMaterial material : ArtMaterial.values(false)) {
            ShapelessArtRecipe recipe = ((ShapelessArtRecipe) material.getRecipe());

            for (ArtMaterial ingredient : recipe.getIngredients()) {

                if (!(materials.containsKey(ingredient))) {
                    continue recipes;
                }
            }
            resultMaterial = material;
        }

        if (resultMaterial == ArtMaterial.CARBON_PAPER_FILLED) {
            MapArt artwork = MapArt.getArtwork(plugin, materials.get(ArtMaterial.MAP_ART).getDurability());
            return ArtMaterial.fillCarbonPaper(artwork);

        } else {
            return (resultMaterial == null) ? null : resultMaterial.getItem();
        }
    }

    private CraftAction getAction(InventoryClickEvent event) {
        InventoryAction action = event.getAction();
        CraftAction craftAction = CraftAction.NONE;

        if (event.getClickedInventory() == null
                || event.getClickedInventory().getType() != InventoryType.WORKBENCH) {
            return craftAction;
        }

        if (event.getSlot() == 0) {
            switch (action) {
                case PICKUP_ALL:
                case PICKUP_SOME:
                case PICKUP_HALF:
                case PICKUP_ONE:
                case COLLECT_TO_CURSOR:
                case MOVE_TO_OTHER_INVENTORY:
                    craftAction = CraftAction.CRAFT;
                default:
                    break;
            }
        } else {
            switch (action) {
                case PLACE_ALL:
                case PLACE_SOME:
                case PLACE_ONE:
                case SWAP_WITH_CURSOR:
                case DROP_ALL_CURSOR:
                case DROP_ONE_CURSOR:
                case DROP_ALL_SLOT:
                case DROP_ONE_SLOT:
                case PICKUP_ALL:
                case PICKUP_SOME:
                case PICKUP_HALF:
                case PICKUP_ONE:
                case COLLECT_TO_CURSOR:
                case MOVE_TO_OTHER_INVENTORY:
                    craftAction = CraftAction.PLACE;
                default:
                    break;
            }
        }
        return craftAction;
    }

    private enum CraftAction {
        PLACE, CRAFT, NONE
    }
}
