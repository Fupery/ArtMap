package me.Fupery.ArtMap.Protocol.Brushes;

import me.Fupery.ArtMap.Protocol.CanvasRenderer;
import org.bukkit.inventory.ItemStack;

public abstract class Brush {

    protected CanvasRenderer canvas;
    protected int cooldownMilli = 150;

    Brush(CanvasRenderer canvas) {
        this.canvas = canvas;
    }

    public abstract void paint(BrushAction action, ItemStack brush, long strokeTime);

    public abstract boolean checkMaterial(ItemStack brush);

    public abstract void clean();

    public int getCooldown() {
        return cooldownMilli;
    }

    public enum BrushAction {
        LEFT_CLICK, RIGHT_CLICK;
    }
}
