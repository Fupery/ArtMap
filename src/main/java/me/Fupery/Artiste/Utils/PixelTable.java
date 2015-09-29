package me.Fupery.Artiste.Utils;

import java.io.Serializable;

public class PixelTable implements Serializable {

    int resolutionFactor;
    float[] yawBounds;
    Object[] pitchBounds;

    public PixelTable(int resolutionFactor) {
        this.resolutionFactor = resolutionFactor;
        yawBounds = null;
        pitchBounds = null;
    }

    public void generate() {
        PixelTableX xTable = new PixelTableX(resolutionFactor);
        PixelTableY yTable = new PixelTableY(xTable, resolutionFactor);

        yawBounds = xTable.getValues();
        pitchBounds = yTable.getPitchValueList();
    }

    public float[] getYawBounds() {
        return yawBounds;
    }

    public Object[] getPitchBounds() {
        return pitchBounds;
    }

    class PixelPoint {
        float yaw;
        float pitch;
        boolean xflip;

        PixelPoint(float yaw, float pitch) {
            this.yaw = yaw;
            this.pitch = pitch;

            if (yaw > 0) {
                xflip = true;
                this.yaw = -yaw;
            }
        }

        double getPixelX() {
            double x = yaw;
            double xValue = 0.0003*Math.pow(x, 3)
                    + 0.0043*Math.pow(x, 2)
                    + 1.423*x
                    + 64.171;
            return (xflip)? 128 - xValue : xValue;
        }

        double getPixelY() {
            double x = pitch;

            //y = ax^3 + bx^2 + cx + d
            return a()*Math.pow(x, 3)
                    + b()*Math.pow(x, 2)
                    + c()*x
                    + d();
        }

        double a() {
            return 0.0002;
        }

        double b() {
            double x = yaw;
            return 2E-06*x + 0.0001;
        }

        double c() {
            double x = yaw;
            return 0.0003*Math.pow(x, 2)
                    + 0.0018*x
                    + 1.369;
        }

        double d() {
            double x = yaw;
            return 1E-05*Math.pow(x, 3)
                    + 0.0008*Math.pow(x, 2)
                    + 0.0089*x
                    + 64.155;
        }
    }

    class PixelTableX {

        float[] values;
        int previousValue = -666;
        int counter = 0;
        int resolutionFactor;

        PixelTableX(int resolutionFactor) {
            this.resolutionFactor = resolutionFactor;
            values = new float[128 / resolutionFactor + 1];

            for (double yaw = -39.0; yaw < 38.0; yaw += 0.0001) {
                PixelPoint point = new PixelPoint((float) yaw, (float) 0.0);
                double x = point.getPixelX();

                if (x - Math.floor(x) < 0.005) {

                    if (!addValue((int) Math.floor(x), (float) yaw)) {
                        break;
                    }

                } else if (Math.ceil(x) - x < 0.005) {

                    if (!addValue((int) Math.ceil(x), (float) yaw)) {
                        break;
                    }
                }
            }
        }

        boolean addValue(int x, float yaw) {

            if (counter == values.length) {
                return false;
            }

            if (x < 0) {
                return true;
            }

            if (x != previousValue
                    && x % resolutionFactor == 0) {
                values[counter] = yaw;
                counter ++;
                previousValue = x;
                return true;
            }
            return true;
        }
        public float[] getValues() {
            return values;
        }
    }

    class PixelTableY {

        float[] yawValues;
        Object[] pitchValueList;
        int previousValue = -666;
        int counter = 0;
        int resolutionFactor;

        PixelTableY(PixelTableX xTable, int resolutionFactor) {

            this.resolutionFactor = resolutionFactor;
            yawValues = xTable.getValues();
            pitchValueList = new Object[yawValues.length];

            for (int j = 0; j < yawValues.length; j ++ ) {

                float yaw = yawValues[j];
                float[] pitchValues = new float[yawValues.length];

                for (double pitch = -39.0; pitch < 39.0; pitch += 0.0001) {

                    PixelPoint point = new PixelPoint(yaw, (float) pitch);
                    double y = point.getPixelY();


                    if (y - Math.floor(y) < 0.005) {

                        if (!addValue(pitchValues, (int) Math.floor(y), (float) pitch)) {
                            break;
                        }

                    } else if (Math.ceil(y) - y < 0.005) {

                        if (!addValue(pitchValues, (int) Math.ceil(y), (float) pitch)) {
                            break;
                        }
                    }

                }
                pitchValueList[j] = pitchValues;
                previousValue = -666;
                counter = 0;
            }
        }

        boolean addValue(float[] pitchValue, int y, float yaw) {

            if (counter == yawValues.length) {
                return false;
            }

            if (y < 0) {
                return true;
            }

            if (y != previousValue
                    && y % resolutionFactor == 0) {
                pitchValue[counter] = yaw;
                counter ++;
                previousValue = y;
                return true;
            }
            return true;
        }

        public Object[] getPitchValueList() {
            return pitchValueList;
        }
    }
}