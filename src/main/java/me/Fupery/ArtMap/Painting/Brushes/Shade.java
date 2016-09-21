package me.Fupery.ArtMap.Painting.Brushes;

import me.Fupery.ArtMap.Painting.CanvasRenderer;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class Shade extends Brush {
    private ArrayList<Pixel> dirtyPixels;

    public Shade(CanvasRenderer renderer) {
        super(renderer);
        this.dirtyPixels = new ArrayList<>();
    }

    @Override
    public void paint(BrushAction action, ItemStack brush, long strokeTime) {
        if (strokeTime > 250) {
            clean();
        }
        boolean darken = brush.getType() == Material.COAL;
        if (action == BrushAction.LEFT_CLICK) {
            shadePixel(darken);
        } else {
            flowShade(darken);
        }
    }

    @Override
    public boolean checkMaterial(ItemStack brush) {
        return brush.getType() == Material.COAL || brush.getType() == Material.FEATHER;
    }

    @Override
    public void clean() {
        dirtyPixels.clear();
    }

    private void shadePixel(boolean darken) {
        byte[] pixel = canvas.getCurrentPixel();
        byte colour = getPixelShade(darken, canvas.getPixelBuffer()[pixel[0]][pixel[1]]);
        canvas.addPixel(pixel[0], pixel[1], colour);
    }

    private void shadePixel(int x, int y, boolean darken) {
        byte colour = getPixelShade(darken, canvas.getPixelBuffer()[x][y]);
        canvas.addPixel(x, y, colour);
    }

    private void flowShade(boolean darken) {

        byte[] pixel = canvas.getCurrentPixel();

        if (pixel != null) {

            if (dirtyPixels.size() > 0) {
                Pixel lastFlowPixel = dirtyPixels.get(dirtyPixels.size() - 1);

                if (lastFlowPixel.darken != darken) {
                    clean();
                } else {
                    flowBrush(darken, lastFlowPixel.x, lastFlowPixel.y, pixel[0], pixel[1]);
                    dirtyPixels.add(new Pixel(pixel[0], pixel[1], darken));
                    return;
                }
            }
            shadePixel(pixel[0], pixel[1], darken);
            dirtyPixels.add(new Pixel(pixel[0], pixel[1], darken));
        }
    }

    private void flowBrush(boolean darken, int x, int y, int x2, int y2) {

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
            if (!dirtyPixels.contains(new Pixel(x, y, darken))) {
                shadePixel(x, y, darken);
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

    private byte getPixelShade(boolean darken, byte colour) {

        if (colour < 4) {
            return colour;
        }
        byte shade = colour;
        byte shift;

        while (shade >= 4) {
            shade -= 4;
        }

        if (darken) {

            if (shade > 0 && shade < 3) {
                shift = -1;

            } else if (shade == 0) {
                shift = 3;

            } else {
                return colour;
            }

        } else {

            if (shade < 2 && shade >= 0) {
                shift = 1;

            } else if (shade == 3) {
                shift = -3;

            } else {
                return colour;
            }
        }
        return (byte) (colour + shift);
    }

    private static class Pixel {
        final int x, y;
        final boolean darken;

        Pixel(int x, int y, boolean darken) {
            this.x = x;
            this.y = y;
            this.darken = darken;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Pixel)) return false;
            Pixel pixel = (Pixel) obj;
            return pixel.x == x && pixel.y == y && pixel.darken == darken;
        }
    }
}
