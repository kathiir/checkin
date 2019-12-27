package view;

import javafx.scene.paint.Color;

public class PaintInformation {
    double x, y, r;

    public PaintInformation( double x, double y, double r) {
        this.x = x;
        this.y = y;
        this.r = r;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getR() {
        return r;
    }
}
