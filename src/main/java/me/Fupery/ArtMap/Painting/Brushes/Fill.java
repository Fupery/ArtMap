package me.Fupery.ArtMap.Painting.Brushes;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.Colour.ArtDye;
import me.Fupery.ArtMap.Painting.Brush;
import me.Fupery.ArtMap.Painting.CanvasRenderer;
import me.Fupery.ArtMap.Recipe.ArtItem;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class Fill extends Brush {
    private final ArrayList<CachedPixel> lastFill;
    private int axisLength;

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
            ArtDye colour = ArtItem.DyeBucket.getColour(bucket);

            if (colour != null) {
                clean();
                fillPixel(colour);
            }

        } else if (lastFill.size() > 0) {
            for (CachedPixel cachedPixel : lastFill) {
                addPixel(cachedPixel.x, cachedPixel.y, cachedPixel.colour);
            }
        }
    }

    @Override
    public boolean checkMaterial(ItemStack bucket) {
        return ArtItem.DyeBucket.getColour(bucket) != null;
    }

    @Override
    public void clean() {
        lastFill.clear();
    }

    private void fillPixel(ArtDye colour) {
        final byte[] pixel = getCurrentPixel();

        if (pixel != null) {

            final boolean[][] coloured = new boolean[axisLength][axisLength];
            final byte clickedColour = getPixelBuffer()[pixel[0]][pixel[1]];
            final byte setColour = colour.getDyeColour(clickedColour);

            ArtMap.getScheduler().ASYNC.run(() -> fillBucket(coloured, pixel[0], pixel[1], clickedColour, setColour));
        }
    }

    private void fillBucket(boolean[][] coloured, int x, int y, byte sourceColour, byte newColour) {
        if (x < 0 || y < 0) {
            return;
        }
        if (x >= axisLength || y >= axisLength) {
            return;
        }

        if (coloured[x][y]) {
            return;
        }

        if (getPixelBuffer()[x][y] != sourceColour) {
            return;
        }
        addPixel(x, y, newColour);
        coloured[x][y] = true;
        lastFill.add(new CachedPixel(x, y, sourceColour));

        fillBucket(coloured, x - 1, y, sourceColour, newColour);
        fillBucket(coloured, x + 1, y, sourceColour, newColour);
        fillBucket(coloured, x, y - 1, sourceColour, newColour);
        fillBucket(coloured, x, y + 1, sourceColour, newColour);
    }

    private static class CachedPixel {
        final int x, y;
        final byte colour;

        CachedPixel(int x, int y, byte colour) {
            this.x = x;
            this.y = y;
            this.colour = colour;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof CachedPixel)) return false;
            CachedPixel cachedPixel = (CachedPixel) obj;
            return cachedPixel.x == x && cachedPixel.y == y && cachedPixel.colour == colour;
        }
    }
}
