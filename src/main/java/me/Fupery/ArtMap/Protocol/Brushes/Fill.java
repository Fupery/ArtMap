package me.Fupery.ArtMap.Protocol.Brushes;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.Protocol.CanvasRenderer;
import me.Fupery.ArtMap.Recipe.ArtItem;
import me.Fupery.ArtMap.Utils.ArtDye;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Fill extends Brush {
    private int axisLength;

    public Fill(CanvasRenderer renderer) {
        super(renderer);
        this.axisLength = renderer.getAxisLength();
        cooldownMilli = 350;
    }

    @Override
    public void paint(BrushAction action, ItemStack brush, long strokeTime) {
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
            fillPixel(colour.getData());
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
    }

    private void fillPixel(byte colour) {
        final byte[] pixel = canvas.getPixel();

        if (pixel != null) {

            final boolean[][] coloured = new boolean[axisLength][axisLength];
            final byte clickedColour = canvas.getPixelBuffer()[pixel[0]][pixel[1]];
            final byte setColour = colour;

            ArtMap.runTaskAsync(new Runnable() {
                @Override
                public void run() {
                    fillBucket(coloured, pixel[0], pixel[1], clickedColour, setColour);
                }
            });
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

        fillBucket(coloured, x - 1, y, source, target);
        fillBucket(coloured, x + 1, y, source, target);
        fillBucket(coloured, x, y - 1, source, target);
        fillBucket(coloured, x, y + 1, source, target);
    }
}
