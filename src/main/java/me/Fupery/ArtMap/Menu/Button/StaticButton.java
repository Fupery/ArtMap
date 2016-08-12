package me.Fupery.ArtMap.Menu.Button;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

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

    public StaticButton(Material material) {
        super(material);
    }

    public StaticButton(Material material, int durability) {
        super(material, durability);
    }

    @Override
    public void onClick(Player player, ClickType clickType) {
        //do nothing
    }
}
