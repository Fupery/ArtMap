package me.Fupery.ArtMap.Painting.Brushes;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.Painting.Brush;
import me.Fupery.ArtMap.Painting.CanvasRenderer;
import me.Fupery.ArtMap.Recipe.ArtDye;
import me.Fupery.ArtMap.Recipe.ArtItem;
import me.Fupery.ArtMap.Recipe.Palette;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class Fill extends Brush {
    private final ArrayList<Pixel> lastFill;
    private int axisLength;
    private Palette palette = ArtMap.getColourPalette();

    public Fill(CanvasRenderer renderer) {
        super(renderer);
        lastFill = new ArrayList<>();
        this.axisLength = getAxisLength();
        cooldownMilli = 350;
    }

    @Override
    public void paint(BrushAction action, ItemStack bucket, long strokeTime) {

        if (action == BrushAction.LEFT_CLICK) {
            ItemMeta meta = bucket.getItemMeta();

            if (!meta.hasLore()) {
                return;
            }
            ArtDye colour = ArtItem.DyeBucket.getColour(palette, bucket);

            if (colour != null) {
                clean();
                fillPixel(colour.getColour());
            }

        } else if (lastFill.size() > 0) {
            for (Pixel pixel : lastFill) {
                addPixel(pixel.x, pixel.y, pixel.colour);
            }
        }
    }

    @Override
    public boolean checkMaterial(ItemStack bucket) {
        return ArtItem.DyeBucket.getColour(palette, bucket) != null;
    }

    @Override
    public void clean() {
        lastFill.clear();
    }

    private void fillPixel(byte colour) {
        final byte[] pixel = getCurrentPixel();

        if (pixel != null) {

            final boolean[][] coloured = new boolean[axisLength][axisLength];
            final byte clickedColour = getPixelBuffer()[pixel[0]][pixel[1]];
            final byte setColour = colour;

            ArtMap.getTaskManager().ASYNC.run(() -> fillBucket(coloured, pixel[0], pixel[1], clickedColour, setColour));
        }
    }

    private void fillBucket(boolean[][] coloured, int x, int y, byte source, byte target) {
        if (x < 0 || y < 0) {
            return;
        }
        if (x >= axisLength || y >= axisLength) {
            return;
        }

        if (coloured[x][y]) {
            return;
        }

        if (getPixelBuffer()[x][y] != source) {
            return;
        }
        addPixel(x, y, target);
        coloured[x][y] = true;
        lastFill.add(new Pixel(x, y, source));

        fillBucket(coloured, x - 1, y, source, target);
        fillBucket(coloured, x + 1, y, source, target);
        fillBucket(coloured, x, y - 1, source, target);
        fillBucket(coloured, x, y + 1, source, target);
    }

    private static class Pixel {
        final int x, y;
        final byte colour;

        Pixel(int x, int y, byte colour) {
            this.x = x;
            this.y = y;
            this.colour = colour;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Pixel)) return false;
            Pixel pixel = (Pixel) obj;
            return pixel.x == x && pixel.y == y && pixel.colour == colour;
        }
    }
}
