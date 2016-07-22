package me.Fupery.ArtMap.Protocol;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.Recipe.ArtItem;
import me.Fupery.ArtMap.Utils.ArtDye;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ArtBrush {

    private final CanvasRenderer renderer;
    private final int axisLength;
    private byte[] lastFlowPixel;

    public ArtBrush(CanvasRenderer renderer, int axisLength) {
        this.renderer = renderer;
        this.axisLength = axisLength;
    }

    public synchronized void paint(ItemStack item, boolean leftClick) {

        if (!renderer.isOffCanvas()) {

            //paint bucket tool
            if (item.getType() == Material.BUCKET && leftClick && item.hasItemMeta()) {

                ItemMeta meta = item.getItemMeta();

                if (meta.hasLore()) {
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
                //shade tool
            } else if (item.getType() == Material.FEATHER
                    || item.getType() == Material.COAL) {

                boolean darken = item.getType() == Material.COAL;

                if (leftClick) {
                    shadePixel(darken);

                } else {
                    fillShade(darken);
                }

                //brush tool
            } else {
                ArtDye dye = ArtDye.getArtDye(item);

                if (dye != null) {

                    if (leftClick) {
                        drawPixel(dye.getData());

                    } else {
                        flowPixel(dye.getData());
                        return;
                    }
                }
            }
        }
        lastFlowPixel = null;
    }

    private void drawPixel(byte colour) {
        byte[] pixel = renderer.getPixel();

        if (pixel != null) {
            renderer.addPixel(pixel[0], pixel[1], colour);
        }
    }

    private void fillPixel(byte colour) {
        final byte[] pixel = renderer.getPixel();

        if (pixel != null) {

            final boolean[][] coloured = new boolean[axisLength][axisLength];
            final byte clickedColour = renderer.getPixelBuffer()[pixel[0]][pixel[1]];
            final byte setColour = colour;

            ArtMap.getTaskManager().ASYNC.run(() -> fillBucket(coloured, pixel[0], pixel[1], clickedColour, setColour));
        }
    }

    private synchronized void fillBucket(boolean[][] coloured, int x, int y, byte source, byte target) {
        if (x < 0 || y < 0) {
            return;
        }
        if (x >= axisLength || y >= axisLength) {
            return;
        }

        if (coloured[x][y]) {
            return;
        }

        if (renderer.getPixelBuffer()[x][y] != source) {
            return;
        }
        renderer.addPixel(x, y, target);
        coloured[x][y] = true;

        fillBucket(coloured, x - 1, y, source, target);
        fillBucket(coloured, x + 1, y, source, target);
        fillBucket(coloured, x, y - 1, source, target);
        fillBucket(coloured, x, y + 1, source, target);
    }

    private void flowPixel(byte colour) {

        byte[] pixel = renderer.getPixel();

        if (pixel != null) {

            if (lastFlowPixel != null) {

                if (Math.abs(lastFlowPixel[0] - pixel[0]) > 5
                        || Math.abs(lastFlowPixel[1] - pixel[1]) > 5
                        || lastFlowPixel[2] != colour) {
                    lastFlowPixel = null;

                } else {
                    flowBrush(lastFlowPixel[0], lastFlowPixel[1], pixel[0], pixel[1], colour);
                    lastFlowPixel = new byte[]{pixel[0], pixel[1], colour};
                    return;
                }
            }
            renderer.addPixel(pixel[0], pixel[1], colour);
            lastFlowPixel = new byte[]{pixel[0], pixel[1], colour};
        }
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
            renderer.addPixel(x, y, colour);
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

    private void shadePixel(boolean darken) {
        byte[] pixel = renderer.getPixel();
        byte colour = getPixelShade(darken,
                renderer.getPixelBuffer()[pixel[0]][pixel[1]]);
        renderer.addPixel(pixel[0], pixel[1], colour);
    }

    private void fillShade(boolean darken) {
        byte[] pixel = renderer.getPixel();
        byte colour = getPixelShade(darken,
                renderer.getPixelBuffer()[pixel[0]][pixel[1]]);
        fillPixel(colour);
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
}
