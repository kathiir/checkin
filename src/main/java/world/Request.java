package world;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.util.GeometricShapeFactory;

public class Request {
//    private Polygon area;

    private double x, y, r;
    private UserData user;

    public Request(double x, double y, double r, UserData user) {
        this.user = user;
        this.x = x;
        this.y = y;
        this.r = r;
    }


    public UserData getUser() {
        return user;
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
