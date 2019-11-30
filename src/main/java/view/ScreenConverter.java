package view;

import com.vividsolutions.jts.geom.Coordinate;

public class ScreenConverter {

    private double xr, yr, wr, hr;
    private double ws, hs;

    //width and height on onresize;

    public ScreenConverter(double xr, double yr, double wr, double hr, double ws, double hs) {
        this.xr = xr;
        this.yr = yr;
        this.wr = wr;
        this.hr = hr;
        this.ws = ws;
        this.hs = hs;
    }

    public ScreenPoint r2s(Coordinate c) {
        double x = (c.x - xr) * ws / wr;
        double y = (yr - c.y) * hs / hr;
        return new ScreenPoint(x, y);
    }

    public Coordinate s2r(ScreenPoint p) {
        double x = xr + p.getX() * wr / ws;
        double y = yr - p.getY() * hr / hs;
        return new Coordinate(x, y);
    }

    public void setScreenSize(int w, int h) {
        wr = wr / ws * w;
        hr = hr / hs * h;

        setWs(w);
        setHs(h);
    }

    public double getXr() {
        return xr;
    }

    public void setXr(double xr) {
        this.xr = xr;
    }

    public double getYr() {
        return yr;
    }

    public void setYr(double yr) {
        this.yr = yr;
    }

    public double getWr() {
        return wr;
    }

    public void setWr(double wr) {
        this.wr = wr;
    }

    public double getHr() {
        return hr;
    }

    public void setHr(double hr) {
        this.hr = hr;
    }

    public double getWs() {
        return ws;
    }

    public void setWs(double ws) {
        this.ws = ws;
    }

    public double getHs() {
        return hs;
    }

    public void setHs(double hs) {
        this.hs = hs;
    }
}
