package me.Fupery.ArtMap.Menu.Button;

import me.Fupery.ArtMap.Menu.Handler.CacheableMenu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class StaticButton extends Button {

    public StaticButton(Material material, String displayName, String... lore) {
        super(material, displayName, lore);
    }

    public StaticButton(Material material, int durability, String displayName, String... lore) {
        super(material, durability, displayName, lore);
    }

    public StaticButton(Material material, int durability, String... text) {
        super(material, durability, text);
    }

    public StaticButton(Material material, String... text) {
        super(material, text);
    }

    public StaticButton(Material material, String displayName) {
        super(material, displayName);
    }

    public StaticButton(Material material) {
        super(material);
    }

    public StaticButton(Material material, int durability) {
        super(material, durability);
    }

    public StaticButton(ItemStack itemStack) {
        super(itemStack.getType(), itemStack.getDurability());
        setAmount(itemStack.getAmount());
        setItemMeta(itemStack.getItemMeta().clone());
    }

    @Override
    public void onClick(CacheableMenu menu, Player player, ClickType clickType) {
        //do nothing
    }
}
