package me.Fupery.ArtMap.Protocol.Brushes;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.Protocol.CanvasRenderer;
import me.Fupery.ArtMap.Recipe.ArtItem;
import me.Fupery.ArtMap.Utils.ArtDye;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class Fill extends Brush {
    private final ArrayList<Pixel> lastFill;
    private int axisLength;

    public Fill(CanvasRenderer renderer) {
        super(renderer);
        lastFill = new ArrayList<>();
        this.axisLength = renderer.getAxisLength();
        cooldownMilli = 350;
    }

    @Override
    public void paint(BrushAction action, ItemStack brush, long strokeTime) {

        if (action == BrushAction.LEFT_CLICK) {
            ItemMeta meta = brush.getItemMeta();

            if (!meta.hasLore()) {
                return;
            }
            ArtDye colour = null;
            String[] lore = meta.getLore().toArray(new String[meta.getLore().size()]);

            for (ArtDye dye : ArtDye.values()) {
                if (lore[0].equals(ArtItem.paintBucketKey + " §7[" + dye.name() + "]")) {
                    colour = dye;
                    break;
                }
            }
            if (colour != null) {
                clean();
                fillPixel(colour.getData());
            }

        } else if (lastFill.size() > 0) {
            for (Pixel pixel : lastFill) {
                canvas.addPixel(pixel.x, pixel.y, pixel.colour);
            }
        }
    }

    @Override
    public boolean checkMaterial(ItemStack brush) {
        if (brush.getType() == Material.BUCKET && brush.hasItemMeta()) {

            ItemMeta meta = brush.getItemMeta();

            if (meta.hasLore()) {
                ArtDye colour = null;
                String[] lore = meta.getLore().toArray(new String[meta.getLore().size()]);

                for (ArtDye dye : ArtDye.values()) {

                    if (lore[0].equals(ArtItem.paintBucketKey + " §7[" + dye.name() + "]")) {
                        colour = dye;
                        break;
                    }
                }

                return colour != null;
            }
        }
        return false;
    }

    @Override
    public void clean() {
        lastFill.clear();
    }

    private void fillPixel(byte colour) {
        final byte[] pixel = canvas.getPixel();

        if (pixel != null) {

            final boolean[][] coloured = new boolean[axisLength][axisLength];
            final byte clickedColour = canvas.getPixelBuffer()[pixel[0]][pixel[1]];
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

        if (canvas.getPixelBuffer()[x][y] != source) {
            return;
        }
        canvas.addPixel(x, y, target);
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
