package me.Fupery.ArtMap.Protocol;

import me.Fupery.ArtMap.ArtMap;

class Cursor {

    private float[] yawTable;
    private Object[] pitchTables;
    private int x, y;
    private float pitch, yaw;
    private float leftBound, rightBound, upBound, downBound;
    private int limit;
    private int yawOffset;

    Cursor(ArtMap plugin, int yawOffset) {

        yawTable = plugin.getPixelTable().getYawBounds();
        pitchTables = plugin.getPixelTable().getPitchBounds();
        this.yawOffset = yawOffset;

        limit = (128 / plugin.getMapResolutionFactor()) - 1;
        int mid = limit / 2;
        x = mid;
        y = mid;

        updateYawBounds();
        updatePitchBounds();
    }

    public void setPitch(float pitch) {
        if (this.pitch != pitch) {
            this.pitch = pitch;
            updateYPos();
        }
    }

    public void setYaw(float yaw) {
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

        if (yaw > 45) {
            return 45;

        } else if (yaw < -45) {
            return -45;

        } else {
            return yaw;
        }
    }

    private float getAdjustedPitch() {

        if (pitch > 45) {
            return 45;

        } else if (pitch < -45) {
            return -45;

        } else {
            return pitch;
        }
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

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
