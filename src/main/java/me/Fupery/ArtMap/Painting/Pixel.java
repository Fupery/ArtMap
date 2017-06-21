package me.Fupery.ArtMap.Painting;

public class Pixel {
    private CanvasRenderer canvas;
    private final int x, y;

    public Pixel(CanvasRenderer canvas, int x, int y) {
        this.canvas = canvas;
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public byte getColour() {
        return canvas.getPixel(x, y);
    }

    public void setColour(byte colour) {
        canvas.addPixel(x, y, colour);
    }

}
