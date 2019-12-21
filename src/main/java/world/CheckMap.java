package world;

import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.util.GeometricShapeFactory;
import comparator.ComparatorFactory;
import comparator.MapObjectComparatorType;
import comparator.SimpleMapObjectComparator;
import objects.MapObject;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CheckMap {
    private List<MapObject> objects;
    private Map<MapObject, Set<UserData>> map;

    private MapObjectComparatorType comparatorType;

    public CheckMap() {
        objects = new ArrayList<>();
        comparatorType = MapObjectComparatorType.SIMPLE;
        map = new HashMap<>();
    }

    public CheckMap(List<MapObject> objects) {
        map = new HashMap<>();
        this.objects = new ArrayList<>();
        setObjects(objects);
        comparatorType = MapObjectComparatorType.SIMPLE;

    }

    public void checkout(UserData userData) {
        for (Set<UserData> set :
                map.values()) {
            if (set.contains(userData)) {
                set.remove(userData);
                return;
            }
        }
    }

    public void checkIn(Request request) {
        checkout(request.getUser());

        GeometricShapeFactory gsc = new GeometricShapeFactory();
        gsc.setCentre(new Coordinate(request.getX(), request.getY()));
        gsc.setSize(2 * request.getR());
        gsc.setNumPoints(32);
        Geometry area = gsc.createCircle();


        Stream<MapObject> stream = objects.stream()
                .filter(object -> area.intersects(object.getGeometry()));

        List<MapObject> list = stream.collect(Collectors.toList());
        int n = list.size();

        for (int i = 0; i < n; i++) {
            list.addAll(list.get(i).getAllChildren());
        }

        ComparatorFactory factory = new ComparatorFactory();

        Comparator<MapObject> c = factory.getComparator(comparatorType, request.getUser().getTags());
        if (list.isEmpty()) {
            return;
        }

        MapObject o = list.get(0);


        for (int i = 1; i < list.size(); i++) {
            if (c.compare(list.get(i), o) > 0) {
                o = list.get(i);
            }
        }

        map.get(o).add(request.getUser());
    }

    public void checkIn(UserData user, MapObject mapObject) {
        checkout(user);
        map.get(mapObject).add(user);
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

    private void setObjects(List<MapObject> list) {
        for (MapObject object :
                list) {
            System.out.println(object.getName());
            map.put(object, new HashSet<>());
            addObjectToMap(object);
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

    public MapObject getMapObject(UserData userData) {
        for (Map.Entry<MapObject, Set<UserData>> entry :
                map.entrySet()) {
            if (entry.getValue().contains(userData))
                return entry.getKey();
        }
        return null;
    }

    public MapObject getMapObjectByCoordinate(Coordinate coordinate) {
        GeometricShapeFactory gsc = new GeometricShapeFactory();
        gsc.setCentre(coordinate);
        gsc.setSize(3);
        gsc.setNumPoints(12);
        Geometry area = gsc.createCircle();

        for (MapObject object :
                objects) {
            if (object.getGeometry().intersects(area)) {
                return getMapObject(area, object);
            }
        }

        return null;
    }

    private MapObject getMapObject(Geometry point, MapObject mapObject) {
        if (!mapObject.getGeometry().intersects(point)) {
            return null;
        }
        for (MapObject object :
                mapObject.getChildren()) {
            if (object.getGeometry().intersects(point)) {
                return getMapObject(point, object);
            }
        }

        return mapObject;
    }
    //--------------//

    public void setComparatorType(MapObjectComparatorType comparatorType) {
        this.comparatorType = comparatorType;
    }
}
