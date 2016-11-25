package me.Fupery.ArtMap.Painting;

import org.bukkit.inventory.ItemStack;

public abstract class Brush {

    protected int cooldownMilli = 150;
    private CanvasRenderer canvas;

    protected Brush(CanvasRenderer canvas) {
        this.canvas = canvas;
    }

    public abstract void paint(BrushAction action, ItemStack brush, long strokeTime);

    public abstract boolean checkMaterial(ItemStack brush);

    public abstract void clean();

    protected int getCooldown() {
        return cooldownMilli;
    }

    protected void addPixel(int x, int y, byte colour) {
        canvas.addPixel(x, y, colour);
    }

    protected byte getPixel(int x, int y) {
        return canvas.getPixel(x, y);
    }

    protected byte[] getCurrentPixel() {
        return canvas.getCurrentPixel();
    }

    protected boolean isOffCanvas() {
        return canvas.isOffCanvas();
    }

    protected byte[][] getPixelBuffer() {
        return canvas.getPixelBuffer();
    }

    protected int getAxisLength() {
        return canvas.getAxisLength();
    }

    protected enum BrushAction {
        LEFT_CLICK, RIGHT_CLICK
    }
}
