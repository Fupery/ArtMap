package me.Fupery.Artiste.Artist;

import me.Fupery.Artiste.Artiste;

class Cursor {

    float[] yawTable;
    Object[] pitchTables;

    private int x, y;
    private float pitch, yaw;
    private float leftBound, rightBound, upBound, downBound;

    int resolutionFactor;

    private int limit;
    private int yawOffset;

    Cursor(Artiste plugin, int yawOffset) {

        resolutionFactor = plugin.getMapResolutionFactor();
        yawTable = plugin.getPixelTable().getYawBounds();
        pitchTables = plugin.getPixelTable().getPitchBounds();
        this.yawOffset = yawOffset;

        limit = (128 / resolutionFactor) - 1;
        int mid = limit / 2;
        x = mid; y = mid;

        updateYawBounds();
        updatePitchBounds();
    }

    public void setPitch(float pitch) {
        if (pitch > 45 || pitch < -45) {
            return;
        }
        this.pitch = pitch;
        updatePosition();
    }

    public void setYaw(float yaw) {
        float adjYaw = yaw;
        adjYaw %= 360;

        if (adjYaw > 0) {
            adjYaw -= yawOffset;

        } else {
            adjYaw += yawOffset;
        }

        if (adjYaw > 45 || adjYaw < -45) {
            return;
        }
        this.yaw = adjYaw;
        updatePosition();
    }

    private void updatePosition() {

        while (yaw < leftBound && x > 0) {
            left();
        }
        while (yaw > rightBound && x < limit) {
            right();
        }
        while (pitch < upBound && y > 0) {
            up();
        }
        while (pitch > downBound && y < limit) {
            down();
        }
    }

    private void up() {
        y --;
        updatePitchBounds();
    }
    private void down() {
        y ++;
        updatePitchBounds();
    }

    private void left() {
        x --;
        updateYawBounds();
    }
    private void right() {
        x ++;
        updateYawBounds();
    }
    void updateYawBounds() {
        leftBound = yawTable[x];
        rightBound = yawTable[x + 1];
    }
    void updatePitchBounds() {
        upBound = ((float[]) pitchTables[x])[y];
        downBound = ((float[]) pitchTables[x])[y + 1];
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
