package me.Fupery.ArtMap.Painting.Brushes;

import me.Fupery.ArtMap.Painting.Brush;
import me.Fupery.ArtMap.Painting.CanvasRenderer;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class Flip extends Brush {
    public Flip(CanvasRenderer canvas) {
        super(canvas);
        cooldownMilli = 750;
    }

    @Override
    public void paint(BrushAction action, ItemStack brush, long strokeTime) {
        byte[][] buffer = getPixelBuffer();

        byte[][] matrix = new byte[buffer.length][];
        for (int i = 0; i < buffer.length; i++) {
            byte[] mat = buffer[i];
            int len = mat.length;
            matrix[i] = new byte[len];
            System.arraycopy(mat, 0, matrix[i], 0, len);
        }

        if (action == BrushAction.LEFT_CLICK) {
            for (int x = 0; x < matrix.length; x++) {
                for (int y = 0; y < matrix[0].length; y++) {
                    addPixel(matrix.length - 1 - x, y, matrix[x][y]);
                }
            }
        } else {
            for (int x = 0; x < matrix.length; x++) {
                for (int y = 0; y < matrix[0].length; y++) {
                    addPixel(x, matrix[0].length - 1 - y, matrix[x][y]);
                }
            }
        }
    }

    @Override
    public boolean checkMaterial(ItemStack brush) {
        return brush.getType() == Material.COMPASS;
    }

    @Override
    public void clean() {

    }
}
