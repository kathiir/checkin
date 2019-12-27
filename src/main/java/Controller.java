import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.sun.javafx.geom.Area;
import command.CheckCommand;
import command.Invoker;
import comparator.MapObjectComparatorType;
import javafx.animation.AnimationTimer;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import objects.*;
import org.controlsfx.control.CheckComboBox;
import com.vividsolutions.jts.geom.*;
import org.controlsfx.control.CheckModel;
import view.PaintInformation;
import view.ResizableCanvas;
import view.ScreenConverter;
import view.ScreenPoint;
import world.CheckMap;
import world.Request;
import world.Tags;
import world.UserData;

import javax.swing.Timer;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Controller implements Initializable, ResizableCanvas.PaintListener {
    public ResizableCanvas canvas;


    public TextArea objectInf;

    private ScreenConverter sc;
    private volatile CheckMap checkMap;

    private volatile Invoker invoker;

    private MapObject selectedMapObject;

    private AnimationTimer timer;

    Map<String, PaintInformation> paintInformationMap;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        checkMap = new CheckMap();
        sc = new ScreenConverter(0, 500, 500, 500,
                500, 500);

        paintInformationMap = new ConcurrentHashMap<>();

        canvas.setPaintListener(this);


        invoker = new Invoker(checkMap);

        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {


                canvas.paint();
            }
        };

        timer.start();

//        canvas.paint();
    }

    UserData user1;
    UserData user2;
    UserData user3;
    UserData user4;
    UserData user5;

    CheckThread thread1;
    CheckThread thread2;
    CheckThread thread3;
    CheckThread thread4;
    CheckThread thread5;


    public void checkIn(ActionEvent actionEvent) {
        try {
            try {
                thread1.stopCheckIn();
                thread2.stopCheckIn();
                thread3.stopCheckIn();
                thread4.stopCheckIn();
                thread5.stopCheckIn();
            } catch (NullPointerException e) {

            }
//            invoker.doCommand(new CheckCommand(request));

//            checkedMapObject = checkMap.getMapObject(userData);

            user1 = new UserData("First", createRandomTagList());
            thread1 = new CheckThread(user1, checkMap, this, 300, 300, 200, 1200);

            user2 = new UserData("Second", createRandomTagList());
            thread2 = new CheckThread(user2, checkMap, this, 120, 320, 150, 1400);

            user3 = new UserData("Third", createRandomTagList());
            thread3 = new CheckThread(user3, checkMap, this, 400, 120, 90, 900);

            user4 = new UserData("Fourth", createRandomTagList());
            thread4 = new CheckThread(user4, checkMap, this, 200, 400, 250, 1000);

            user5 = new UserData("Fifth", createRandomTagList());
            thread5 = new CheckThread(user5, checkMap, this, 300, 100, 180, 800);

            thread1.start();
            thread2.start();
            thread3.start();
            thread4.start();
            thread5.start();


//            canvas.paint();
        } catch (Exception ignored) {
        }
    }


    private ScreenPoint last = null;

    public void move(MouseEvent e) {
        if (last != null) {
            ScreenPoint cur = new ScreenPoint(e.getX(), e.getY());
            double dx = -cur.getX() + last.getX();
            double dy = -cur.getY() + last.getY();
            Coordinate d = sc.s2r(new ScreenPoint(dx, dy));
            sc.setXr(d.x);
            sc.setYr(d.y);
            last = cur;
            canvas.paint();

        }
    }

    public void getPoint(MouseEvent e) {
        last = new ScreenPoint(e.getX(), e.getY());
    }

    public void zoom(ScrollEvent scrollEvent) {
        //not today
    }

    public void getSelectedObject(ContextMenuEvent contextMenuEvent) {
        try {
            ScreenPoint screenPoint = new ScreenPoint(contextMenuEvent.getX(), contextMenuEvent.getY());
            selectedMapObject = checkMap.getMapObjectByCoordinate(sc.s2r(screenPoint));
            System.out.println(selectedMapObject.getName());
            canvas.paint();

            setObjectInf(selectedMapObject);
        } catch (NullPointerException e) {
            selectedMapObject = null;
            objectInf.setText("");
        }

    }

    public void openFile(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("JSON files", "*.json"));
        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            ObjectMapper mapper = new ObjectMapper();
            SimpleModule module = new SimpleModule();
            module.addDeserializer(MapObject.class, new MapObjectDeserializer(MapObject.class));
            mapper.registerModule(module);

            JsonFactory jsonFactory = new JsonFactory();
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {

                MapObject[] objects = mapper.readValue(jsonFactory.createParser(br),
                        MapObject[].class);
                checkMap = new CheckMap(Arrays.asList(objects));
                invoker = new Invoker(checkMap);
                canvas.paint();

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

//        canvas.paint();
    }


    //------------------//

    @Override
    public void paint(GraphicsContext context) {
        context.setFill(Color.rgb(255, 254, 232));
        context.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        drawMapObjects(context);
        try {
            for (Map.Entry<String, PaintInformation> entry :
                    paintInformationMap.entrySet()) {
                PaintInformation p = entry.getValue();
                ScreenPoint topLeft = sc.r2s(new Coordinate(p.getX() - p.getR(), p.getY() - p.getR()));
                ScreenPoint bottomRight = sc.r2s(new Coordinate(p.getX() + p.getR(), p.getY() + p.getR()));

                if (entry.getKey().equals(thread1.getName())) {
                    context.setStroke(Color.rgb(68, 215, 0));
                } else if (entry.getKey().equals(thread2.getName())) {
                    context.setStroke(Color.rgb(140, 0, 255));
                } else if (entry.getKey().equals(thread3.getName())) {
                    context.setStroke(Color.rgb(255, 12, 0));
                } else if (entry.getKey().equals(thread4.getName())) {
                    context.setStroke(Color.rgb(255, 255, 0));
                } else {
                    context.setStroke(Color.rgb(0, 255, 255));
                }

                context.strokeOval(topLeft.getX(), bottomRight.getY(),
                        -topLeft.getX() + bottomRight.getX(), topLeft.getY() - bottomRight.getY());
            }


            context.setStroke(Color.BLACK);
        } catch (NullPointerException e) {
            e.printStackTrace();

        }

    }

    private void drawMapObjects(GraphicsContext context) {
        List<Geometry> geometries = new ArrayList<>();
        List<MapObject> mapObjects = checkMap.getAllObjects();
        for (MapObject mapObject : mapObjects) {
            if (mapObject.equals(selectedMapObject)) {
                drawMapObject(context, mapObject, Color.rgb(164, 212, 255), Color.rgb(0, 41, 255));
            } else if (mapObject.equals(checkMap.getMapObject(user1))) {
                drawMapObject(context, mapObject, Color.rgb(195, 255, 195), Color.rgb(68, 215, 0));
            } else if (mapObject.equals(checkMap.getMapObject(user2))) {
                drawMapObject(context, mapObject, Color.rgb(235, 181, 255), Color.rgb(140, 0, 255));
            } else if (mapObject.equals(checkMap.getMapObject(user3))) {
                drawMapObject(context, mapObject, Color.rgb(255, 207, 200), Color.rgb(255, 12, 0));
            } else if (mapObject.equals(checkMap.getMapObject(user4))) {
                drawMapObject(context, mapObject, Color.rgb(255, 255, 200), Color.rgb(255, 255, 0));
            } else if (mapObject.equals(checkMap.getMapObject(user5))) {
                drawMapObject(context, mapObject, Color.rgb(200, 255, 255), Color.rgb(0, 255, 255));
            } else {
                drawMapObject(context, mapObject, Color.WHITE, Color.BLACK);
            }

        }
    }

    private void drawMapObject(GraphicsContext context, MapObject mapObject, Paint fillPaint, Paint strokePaint) {
        switch (mapObject.getGeometry().getGeometryType()) {
            case "Point":
                drawPoint(context, (Point) mapObject.getGeometry(), fillPaint, strokePaint);
                break;
            case "LineString":
                drawLineString(context, (LineString) mapObject.getGeometry(), fillPaint, strokePaint);
                break;
            case "Polygon":
                drawPolygon(context, (Polygon) mapObject.getGeometry(), fillPaint, strokePaint);
                break;
        }
    }

    private void drawPoint(GraphicsContext context, Point point, Paint fillPaint, Paint strokePaint) {
        ScreenPoint screenPoint = sc.r2s(point.getCoordinate());
        context.setFill(fillPaint);
        context.setStroke(strokePaint);

        context.strokeLine(screenPoint.getX(), screenPoint.getY(), screenPoint.getX() + 1, screenPoint.getY() + 1);

        context.setFill(Color.WHITE);
        context.setStroke(Color.BLACK);
    }

    private void drawLineString(GraphicsContext context, LineString lineString, Paint fillPaint, Paint strokePaint) {
        context.setFill(fillPaint);
        context.setStroke(strokePaint);

        Coordinate[] coordinates = lineString.getCoordinates();
        for (int i = 0; i < coordinates.length - 1; i++) {
            ScreenPoint point1 = sc.r2s(coordinates[i]);
            ScreenPoint point2 = sc.r2s(coordinates[i + 1]);
            context.strokeLine(point1.getX(), point1.getY(), point2.getX(), point2.getY());
        }

        context.setFill(Color.WHITE);
        context.setStroke(Color.BLACK);
    }

    //without holes
    private void drawPolygon(GraphicsContext context, Polygon polygon, Paint fillPaint, Paint strokePaint) {
        context.setFill(fillPaint);
        context.setStroke(strokePaint);

        Coordinate[] shell = polygon.getExteriorRing().getCoordinates();

        ScreenPoint point = sc.r2s(shell[0]);
        context.beginPath();
        context.moveTo(point.getX(), point.getY());
        for (int i = 0; i < shell.length - 1; i++) {
            point = sc.r2s(shell[i + 1]);
            context.lineTo(point.getX(), point.getY());
        }
        context.closePath();
        context.fill();
        context.stroke();


        context.setFill(Color.WHITE);
        context.setStroke(Color.BLACK);
    }


//    public void changeComparator(ActionEvent actionEvent) {
//        checkMap.setComparatorType(comparatorSelector.getValue());
//    }

    private void setObjectInf(MapObject mapObject) {
        StringBuilder sb = new StringBuilder();
        sb.append("Название: ").append(mapObject.getName()).append("\n")
                .append("Тэги: ").append(mapObject.getTags().toString());


        objectInf.setText(sb.toString());
    }

    public Invoker getInvoker() {
        return invoker;
    }

    private ArrayList<Tags> createRandomTagList() {
        ArrayList<Tags> list = new ArrayList<>();
        Random random = new Random();

        int n = random.nextInt(4) + 1;
        for (int i = 0; i < n; i++) {
            int r = random.nextInt(Tags.values().length);
            if (!list.contains(Tags.values()[r])) {
                list.add(Tags.values()[r]);
            }
        }

        return list;
    }
}
