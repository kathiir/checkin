package view;

import java.util.Collection;

public class ScreenCoordinates {
    private double[] xx, yy;

    public ScreenCoordinates(Collection<ScreenPoint> points) {
        xx = new double[points.size()];
        yy = new double[points.size()];
        int i = 0;
        for (ScreenPoint p : points) {
            xx[i] = p.getX();
            yy[i] = p.getY();
            i++;
        }
    }

    public double[] getXx() {
        return xx;
    }

    public double[] getYy() {
        return yy;
    }

    public int size() {
        return xx.length;
    }
}
