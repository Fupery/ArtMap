package me.Fupery.ArtMap.Painting.Brushes;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.Colour.ArtDye;
import me.Fupery.ArtMap.Colour.Palette;
import me.Fupery.ArtMap.Painting.Brush;
import me.Fupery.ArtMap.Painting.CanvasRenderer;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class Dye extends Brush {
    private ArrayList<CachedPixel> dirtyPixels;

    private Palette palette = ArtMap.getDyePalette();

    public Dye(CanvasRenderer renderer) {
        super(renderer);
        this.dirtyPixels = new ArrayList<>();
    }

    @Override
    public void paint(BrushAction action, ItemStack brush, long strokeTime) {
        ArtDye dye = palette.getDye(brush);
        if (dye == null) {
            return;
        }
        if (action == BrushAction.LEFT_CLICK) {
            clean();
            byte[] pixel = getCurrentPixel();
            if (pixel != null) {
//                addPixel(pixel[0], pixel[1], dye.getDye());
                dye.apply(getPixelAt(pixel[0], pixel[1]));
            }
        } else {
            if (strokeTime > 250) {
                clean();
            }
            byte[] pixel = getCurrentPixel();

            if (pixel != null) {

                if (dirtyPixels.size() > 0) {

                    CachedPixel lastFlowPixel = dirtyPixels.get(dirtyPixels.size() - 1);

                    if (!lastFlowPixel.getDye().equals(dye)) {
                        clean();
//                            lastFlowPixel[0] - pixel[0]) > 5
//                            || Math.abs(lastFlowPixel[1] - pixel[1]) > 5
//                            || lastFlowPixel[2] != dye) {
//                        lastFlowPixel = null;
//
                    } else {
                        flowBrush(lastFlowPixel.getX(), lastFlowPixel.getY(), pixel[0], pixel[1], dye);
                        dirtyPixels.add(new CachedPixel(pixel[0], pixel[1], dye));
                        return;
                    }
                }
                dye.apply(getPixelAt(pixel[0], pixel[1]));
                dirtyPixels.add(new CachedPixel(pixel[0], pixel[1], dye));
            }
        }
    }

    @Override
    public boolean checkMaterial(ItemStack brush) {
        return palette.getDye(brush) != null;
    }

    @Override
    public void clean() {
        dirtyPixels.clear();
    }

    private void flowBrush(int x, int y, int x2, int y2, ArtDye dye) {

        int w = x2 - x;
        int h = y2 - y;

        int dx1 = 0, dy1 = 0, dx2 = 0, dy2 = 0;

        if (w != 0) {
            dx1 = (w > 0) ? 1 : -1;
            dx2 = (w > 0) ? 1 : -1;
        }

        if (h != 0) {
            dy1 = (h > 0) ? 1 : -1;
        }

        int longest = Math.abs(w);
        int shortest = Math.abs(h);

        if (!(longest > shortest)) {
            longest = Math.abs(h);
            shortest = Math.abs(w);

            if (h < 0) {
                dy2 = -1;

            } else if (h > 0) {
                dy2 = 1;
            }
            dx2 = 0;
        }
        int numerator = longest >> 1;

        for (int i = 0; i <= longest; i++) {
            if (!dirtyPixels.contains(new CachedPixel(x, y, dye))) {
                dye.apply(getPixelAt(x, y));
            }
            numerator += shortest;

            if (!(numerator < longest)) {
                numerator -= longest;
                x += dx1;
                y += dy1;

            } else {
                x += dx2;
                y += dy2;
            }
        }
    }

    private static class CachedPixel {
        final int x, y;
        final ArtDye dye;

        CachedPixel(int x, int y, ArtDye dye) {
            this.x = x;
            this.y = y;
            this.dye = dye;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public ArtDye getDye() {
            return dye;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof CachedPixel)) return false;
            CachedPixel cachedPixel = (CachedPixel) obj;
            return cachedPixel.x == x && cachedPixel.y == y && cachedPixel.dye.equals(dye);
        }
    }
}
