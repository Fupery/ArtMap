package me.Fupery.ArtMap.Painting;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.IO.PixelTableManager;

class Cursor {

    private final float[] yawTable;
    private final Object[] pitchTables;
    private final int limit;
    private final int yawOffset;
    private int x, y;
    private float pitch, yaw;
    private float leftBound, rightBound, upBound, downBound;
    private boolean yawOffCanvas;
    private boolean pitchOffCanvas;

    Cursor(int yawOffset) {
        PixelTableManager pixelTable = ArtMap.getPixelTable();

        yawTable = pixelTable.getYawBounds();
        pitchTables = pixelTable.getPitchBounds();
        this.yawOffset = yawOffset;
        limit = (128 / pixelTable.getResolutionFactor()) - 1;
        yawOffCanvas = false;
        pitchOffCanvas = false;
        int mid = limit / 2;
        x = mid;
        y = mid;

        updateYawBounds();
        updatePitchBounds();
    }

    void setPitch(float pitch) {
        if (this.pitch != pitch) {
            this.pitch = pitch;
            updateYPos();
        }
    }

    void setYaw(float yaw) {
        if (this.yaw != yaw) {
            this.yaw = yaw;
            updateXPos();
        }
    }

    private void updateXPos() {
        float yaw = getAdjustedYaw();

        while (yaw < leftBound && x > 0) {
            x--;
            updateYawBounds();
        }
        while (yaw > rightBound && x < limit) {
            x++;
            updateYawBounds();
        }
    }

    private void updateYPos() {
        float pitch = getAdjustedPitch();

        while (pitch < upBound && y > 0) {
            y--;
            updatePitchBounds();
        }
        while (pitch > downBound && y < limit) {
            y++;
            updatePitchBounds();
        }
    }

    private float getAdjustedYaw() {
        float yaw = this.yaw;
        float start = -180;
        float end = 180;

        float width = end - start;
        float offsetValue = yaw - start;

        yaw = (float) (offsetValue - (Math.floor(offsetValue / width) * width)) + start;

        yaw += (yaw > 0) ? -yawOffset : yawOffset;

        yawOffCanvas = (yaw > 45 || yaw < -45);
        return checkBounds(yaw);
    }

    private float getAdjustedPitch() {
        pitchOffCanvas = (pitch > 45 || pitch < -45);
        return checkBounds(pitch);
    }

    private float checkBounds(float value) {

        if (value > 40) {
            return 40;

        } else if (value < -40) {
            return -40;
        }
        return value;
    }

    private void updateYawBounds() {
        leftBound = yawTable[x];
        rightBound = yawTable[x + 1];
        updatePitchBounds();
    }

    private void updatePitchBounds() {
        upBound = ((float[]) pitchTables[x])[y];
        downBound = ((float[]) pitchTables[x])[y + 1];
    }

    int getX() {
        return x;
    }

    int getY() {
        return y;
    }

    boolean isOffCanvas() {
        return yawOffCanvas || pitchOffCanvas;
    }
}
