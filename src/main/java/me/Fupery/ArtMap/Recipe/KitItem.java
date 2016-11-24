package me.Fupery.ArtMap.Recipe;

import me.Fupery.ArtMap.Utils.Item.CustomItem;
import org.bukkit.Material;

public class KitItem extends CustomItem {
    public KitItem(Material material) {
        super(material, ArtItem.KIT_KEY);
    }

    public KitItem(Material material, int durability) {
        super(material, ArtItem.KIT_KEY, durability);
    }

    public KitItem(Material material, String name) {
        super(material, ArtItem.KIT_KEY, name);
    }

    public KitItem(Material material, int durability, String name) {
        super(material, ArtItem.KIT_KEY, durability);
        name(name);
    }
}
