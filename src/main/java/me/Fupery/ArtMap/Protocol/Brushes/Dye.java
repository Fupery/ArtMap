package me.Fupery.ArtMap.Protocol.Brushes;

import me.Fupery.ArtMap.Protocol.CanvasRenderer;
import me.Fupery.ArtMap.Utils.ArtDye;
import org.bukkit.inventory.ItemStack;

public class Dye extends Brush {
    private byte[] lastFlowPixel;

    public Dye(CanvasRenderer renderer) {
        super(renderer);
        lastFlowPixel = null;
    }

    @Override
    public void paint(BrushAction action, ItemStack brush, long strokeTime) {
        ArtDye dye = ArtDye.getArtDye(brush);
        if (dye == null) {
            return;
        }
        if (action == BrushAction.LEFT_CLICK) {
            clean();
            byte[] pixel = canvas.getPixel();
            if (pixel != null) {
                canvas.addPixel(pixel[0], pixel[1], dye.getData());
            }
        } else {
            if (strokeTime > 250) {
                clean();
            }
            byte colour = dye.getData();
            byte[] pixel = canvas.getPixel();

            if (pixel != null) {

                if (lastFlowPixel != null) {

                    if (lastFlowPixel[2] != colour) {
                        clean();
//                            lastFlowPixel[0] - pixel[0]) > 5
//                            || Math.abs(lastFlowPixel[1] - pixel[1]) > 5
//                            || lastFlowPixel[2] != colour) {
//                        lastFlowPixel = null;
//
                    } else {
                        flowBrush(lastFlowPixel[0], lastFlowPixel[1], pixel[0], pixel[1], colour);
                        lastFlowPixel = new byte[]{pixel[0], pixel[1], colour};
                        return;
                    }
                }
                canvas.addPixel(pixel[0], pixel[1], colour);
                lastFlowPixel = new byte[]{pixel[0], pixel[1], colour};
            }
        }
    }

    @Override
    public boolean checkMaterial(ItemStack brush) {
        return ArtDye.getArtDye(brush) != null;
    }

    @Override
    public void clean() {
        lastFlowPixel = null;
    }

    private void flowBrush(int x, int y, int x2, int y2, byte colour) {

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
            canvas.addPixel(x, y, colour);
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
}
