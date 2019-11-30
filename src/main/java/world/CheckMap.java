package world;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.util.GeometricShapeFactory;
import comparator.ComparatorFactory;
import comparator.MapObjectComparatorType;
import comparator.SimpleMapObjectComparator;
import objects.MapObject;

import java.util.*;

public class CheckMap {
    private List<MapObject> objects;
    private Map<MapObject, Set<UserData>> map;

    private MapObjectComparatorType comparatorType;

    public CheckMap() {
        objects = new ArrayList<>();
        comparatorType = MapObjectComparatorType.SIMPLE;
    }

    public void checkout(UserData userData) {
        for (Set<UserData> set:
                map.values()){
            if (set.contains(userData)) {
                set.remove(userData);
                return;
            }
        }
    }

    public void checkIn(Request request) {
        GeometricShapeFactory gsc = new GeometricShapeFactory();
        gsc.setCentre(new Coordinate(request.getX(), request.getY()));
        gsc.setSize(2 * request.getR());
        gsc.setNumPoints(32);
        Geometry area = gsc.createCircle();

        List<MapObject> list = new ArrayList<>();
        for (MapObject object : objects) {
            if (area.intersects(object.getGeometry())) {
                list.add(object);
                list.addAll(object.getAllChildren());
            }
        }

        ComparatorFactory factory = new ComparatorFactory();

        Comparator<MapObject> c = factory.getComparator(comparatorType, request.getUser().getTags());
        if (list.isEmpty()) {
            return;
        }

        MapObject o = list.get(0);
        //поправить на случай если ничего не подойдет

        //добавить команду

        for (int i = 1; i < list.size(); i++) {
            if (c.compare(list.get(i), o) > 0) {
                o = list.get(i);
            }
        }

        map.get(o).add(request.getUser());

//        return o;
    }

    //--------------------//

    public List<MapObject> getAllObjects() {
        List<MapObject> list = new ArrayList<>();
        for (MapObject object : objects) {
            list.add(object);
            list.addAll(object.getAllChildren());
        }
        return list;
    }

    public void setObjects(List<MapObject> list) {
        for (MapObject object :
                list) {
            addObjectToMap(object);
            map.put(object, new HashSet<>());
        }
    }

    private void addObjectToMap(MapObject object) {
        for (int i = 0; i < objects.size(); i++) {
            MapObject cur = objects.get(i);
            if (object.getGeometry().covers(cur.getGeometry())) {
                addChild(object, cur);
                objects.remove(i);
                i--;
            } else if (cur.getGeometry().covers(object.getGeometry())) {
                addChild(cur, object);
                return;
            }
        }
        objects.add(object);
    }

    private void addChild(MapObject parent, MapObject child) {
        for (int i = 0; i < parent.getChildren().size(); i++) {
            MapObject cur = parent.getChildren().get(i);
            if (child.getGeometry().covers(cur.getGeometry())) {
                addChild(child, cur);
                parent.getChildren().remove(i);
                i--;
            } else if (cur.getGeometry().covers(child.getGeometry())) {
                addChild(cur, child);
                return;
            }
        }
        parent.getChildren().add(child);
    }

    //--------------//

    public void setComparatorType(MapObjectComparatorType comparatorType) {
        this.comparatorType = comparatorType;
    }
}
