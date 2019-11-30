import com.bedatadriven.jackson.datatype.jts.JtsModule;
import com.bedatadriven.jackson.datatype.jts.serialization.GeometryDeserializer;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import com.sun.javafx.geom.Area;
import command.CheckCommand;
import command.Invoker;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import objects.*;
import org.controlsfx.control.CheckComboBox;
import com.vividsolutions.jts.geom.*;
import view.ResizableCanvas;
import view.ScreenConverter;
import view.ScreenPoint;
import world.CheckMap;
import world.Request;
import world.Tags;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class Controller implements Initializable, ResizableCanvas.PaintListener {
    public ResizableCanvas canvas;
    public TextField radiusTextField;
    public TextField nameTextField;
    public CheckComboBox<Tags> tagsCheckComboBox;

    private ScreenConverter sc;
    private CheckMap checkMap;

    private Invoker invoker;


    private void paint() {
        GraphicsContext context = canvas.getGraphicsContext2D();
        context.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        context.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        checkMap = new CheckMap();
        sc = new ScreenConverter(0, 0, 500, 500,
                canvas.getWidth(), canvas.getHeight());

        canvas.setPaintListener(this);

        tagsCheckComboBox.getItems().setAll(Tags.values());

        invoker = new Invoker(checkMap);

    }

    public void checkIn(ActionEvent actionEvent) {
        generateStreetMap(checkMap);

//        Request request = new Request(x, y, r, userData);
//        invoker.addCommand(new CheckCommand(request)); //do command
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
//            repaint(new Rectangle(getWidth(), getHeight()));
            paint();

        }

    }

    public void getPoint(MouseEvent e) {
        last = new ScreenPoint(e.getX(), e.getY());
    }

    public void zoom(ScrollEvent scrollEvent) {

    }

    public void openFile(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("JSON files", "*.json"));
        File file = fileChooser.showOpenDialog(null);

        if (file != null) {

            JsonFactory jsonFactory = new JsonFactory();
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {

                paint();


            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }



    @Override
    public void paint(GraphicsContext context) {
        context.setFill(Color.WHITE);
        context.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }


    //------------------------//


    private void generateStreetMap(CheckMap checkMap) {
        GeometryFactory geometryFactory = new GeometryFactory();
        Point point;
        LineString lineString;
        Polygon polygon;

        Random random = new Random();

        List<MapObject> objectList = new ArrayList<>();

        for (int i = 0; i < (int) canvas.getHeight() / 100; i++) {
            for (int j = 0; j < (int) canvas.getWidth() / 100; j++) {

                if (i % 2 == 0 && j % 2 == 0) {
                    lineString = geometryFactory.createLineString(new Coordinate[]{new Coordinate(j * 100 + 10, i * 100), new Coordinate(j * 100 + 10, i * 100 + 90)});
                    objectList.add(new LineObject(lineString, "Street " + i + ":" + j + "-v", createRandomTagList(random), random.nextInt(30)));

                    lineString = geometryFactory.createLineString(new Coordinate[]{new Coordinate(j * 100 + 10, i * 100 + 90), new Coordinate(j * 100 + 100, i * 100 + 90)});
                    objectList.add(new LineObject(lineString, "Street " + i + ":" + j + "-h", createRandomTagList(random), random.nextInt(30)));

                    polygon = geometryFactory.createPolygon(new Coordinate[]{
                            new Coordinate(j * 100 + 40, i * 100 + 10),
                            new Coordinate(j * 100 + 40, i * 100 + 70),
                            new Coordinate(j * 100 + 90, i * 100 + 70),
                            new Coordinate(j * 100 + 90, i * 100 + 10),
                            new Coordinate(j * 100 + 40, i * 100 + 10)});
                    objectList.add(new PolygonObject(polygon, "House " + i + ":" + j, createRandomTagList(random), random.nextInt(70)));

                    point = geometryFactory.createPoint(new Coordinate(j * 100 + 25, i * 100 + 20));
                    objectList.add(new PointObject(point, "Tree " + i + ":" + j + "-1", createRandomTagList(random), random.nextInt(40)));

                    point = geometryFactory.createPoint(new Coordinate(j * 100 + 25, i * 100 + 40));
                    objectList.add(new PointObject(point, "Tree " + i + ":" + j + "-2", createRandomTagList(random), random.nextInt(40)));

                    point = geometryFactory.createPoint(new Coordinate(j * 100 + 25, i * 100 + 60));
                    objectList.add(new PointObject(point, "Tree " + i + ":" + j + "-3", createRandomTagList(random), random.nextInt(40)));

                    point = geometryFactory.createPoint(new Coordinate(j * 100 + 50, i * 100 + 20));
                    objectList.add(new PointObject(point, "Inner-1 " + i + ":" + j + "-1", createRandomTagList(random), random.nextInt(40)));

                    point = geometryFactory.createPoint(new Coordinate(j * 100 + 50, i * 100 + 40));
                    objectList.add(new PointObject(point, "Inner-1 " + i + ":" + j + "-2", createRandomTagList(random), random.nextInt(40)));

                    point = geometryFactory.createPoint(new Coordinate(j * 100 + 50, i * 100 + 60));
                    objectList.add(new PointObject(point, "Inner-1 " + i + ":" + j + "-3", createRandomTagList(random), random.nextInt(40)));

                    point = geometryFactory.createPoint(new Coordinate(j * 100 + 75, i * 100 + 20));
                    objectList.add(new PointObject(point, "Inner-1 " + i + ":" + j + "-1", createRandomTagList(random), random.nextInt(40)));

                    point = geometryFactory.createPoint(new Coordinate(j * 100 + 75, i * 100 + 40));
                    objectList.add(new PointObject(point, "Inner-1 " + i + ":" + j + "-2", createRandomTagList(random), random.nextInt(40)));

                    point = geometryFactory.createPoint(new Coordinate(j * 100 + 75, i * 100 + 60));
                    objectList.add(new PointObject(point, "Inner-1 " + i + ":" + j + "-3", createRandomTagList(random), random.nextInt(40)));


                } else if (i % 2 == 0) {
                    lineString = geometryFactory.createLineString(new Coordinate[]{new Coordinate(j * 100 + 90, i * 100), new Coordinate(j * 100 + 90, i * 100 + 90)});
                    objectList.add(new LineObject(lineString, "Street " + i + ":" + j + "-v", createRandomTagList(random), random.nextInt(30)));

                    lineString = geometryFactory.createLineString(new Coordinate[]{new Coordinate(j * 100, i * 100 + 90), new Coordinate(j * 100 + 90, i * 100 + 90)});
                    objectList.add(new LineObject(lineString, "Street " + i + ":" + j + "-h", createRandomTagList(random), random.nextInt(30)));

                    polygon = geometryFactory.createPolygon(new Coordinate[]{
                            new Coordinate(j * 100 + 10, i * 100 + 10),
                            new Coordinate(j * 100 + 10, i * 100 + 70),
                            new Coordinate(j * 100 + 60, i * 100 + 70),
                            new Coordinate(j * 100 + 60, i * 100 + 10),
                            new Coordinate(j * 100 + 10, i * 100 + 10)});
                    objectList.add(new PolygonObject(polygon, "House " + i + ":" + j, createRandomTagList(random), random.nextInt(70)));

                    point = geometryFactory.createPoint(new Coordinate(j * 100 + 75, i * 100 + 20));
                    objectList.add(new PointObject(point, "Tree " + i + ":" + j + "-1", createRandomTagList(random), random.nextInt(40)));

                    point = geometryFactory.createPoint(new Coordinate(j * 100 + 75, i * 100 + 40));
                    objectList.add(new PointObject(point, "Tree " + i + ":" + j + "-2", createRandomTagList(random), random.nextInt(40)));

                    point = geometryFactory.createPoint(new Coordinate(j * 100 + 75, i * 100 + 60));
                    objectList.add(new PointObject(point, "Tree " + i + ":" + j + "-3", createRandomTagList(random), random.nextInt(40)));

                    point = geometryFactory.createPoint(new Coordinate(j * 100 + 50, i * 100 + 20));
                    objectList.add(new PointObject(point, "Inner-1 " + i + ":" + j + "-1", createRandomTagList(random), random.nextInt(40)));

                    point = geometryFactory.createPoint(new Coordinate(j * 100 + 50, i * 100 + 40));
                    objectList.add(new PointObject(point, "Inner-1 " + i + ":" + j + "-2", createRandomTagList(random), random.nextInt(40)));

                    point = geometryFactory.createPoint(new Coordinate(j * 100 + 50, i * 100 + 60));
                    objectList.add(new PointObject(point, "Inner-1 " + i + ":" + j + "-3", createRandomTagList(random), random.nextInt(40)));

                    point = geometryFactory.createPoint(new Coordinate(j * 100 + 25, i * 100 + 20));
                    objectList.add(new PointObject(point, "Inner-2 " + i + ":" + j + "-1", createRandomTagList(random), random.nextInt(40)));

                    point = geometryFactory.createPoint(new Coordinate(j * 100 + 25, i * 100 + 40));
                    objectList.add(new PointObject(point, "Inner-2 " + i + ":" + j + "-2", createRandomTagList(random), random.nextInt(40)));

                    point = geometryFactory.createPoint(new Coordinate(j * 100 + 25, i * 100 + 60));
                    objectList.add(new PointObject(point, "Inner-2 " + i + ":" + j + "-3", createRandomTagList(random), random.nextInt(40)));


                } else if (j % 2 == 0) {
                    lineString = geometryFactory.createLineString(new Coordinate[]{new Coordinate(j * 100 + 10, i * 100 + 10), new Coordinate(j * 100 + 10, i * 100 + 100)});
                    objectList.add(new LineObject(lineString, "Street " + i + ":" + j + "-v", createRandomTagList(random), random.nextInt(30)));

                    lineString = geometryFactory.createLineString(new Coordinate[]{new Coordinate(j * 100 + 10, i * 100 + 10), new Coordinate(j * 100 + 100, i * 100 + 10)});
                    objectList.add(new LineObject(lineString, "Street " + i + ":" + j + "-h", createRandomTagList(random), random.nextInt(30)));

                    polygon = geometryFactory.createPolygon(new Coordinate[]{
                            new Coordinate(j * 100 + 40, i * 100 + 30),
                            new Coordinate(j * 100 + 40, i * 100 + 90),
                            new Coordinate(j * 100 + 90, i * 100 + 90),
                            new Coordinate(j * 100 + 90, i * 100 + 30),
                            new Coordinate(j * 100 + 40, i * 100 + 30)});
                    objectList.add(new PolygonObject(polygon, "House " + i + ":" + j, createRandomTagList(random), random.nextInt(70)));

                    point = geometryFactory.createPoint(new Coordinate(j * 100 + 25, i * 100 + 40));
                    objectList.add(new PointObject(point, "Tree " + i + ":" + j + "-1", createRandomTagList(random), random.nextInt(40)));

                    point = geometryFactory.createPoint(new Coordinate(j * 100 + 25, i * 100 + 60));
                    objectList.add(new PointObject(point, "Tree " + i + ":" + j + "-2", createRandomTagList(random), random.nextInt(40)));

                    point = geometryFactory.createPoint(new Coordinate(j * 100 + 25, i * 100 + 80));
                    objectList.add(new PointObject(point, "Tree " + i + ":" + j + "-3", createRandomTagList(random), random.nextInt(40)));

                    point = geometryFactory.createPoint(new Coordinate(j * 100 + 50, i * 100 + 40));
                    objectList.add(new PointObject(point, "Inner-1 " + i + ":" + j + "-1", createRandomTagList(random), random.nextInt(40)));

                    point = geometryFactory.createPoint(new Coordinate(j * 100 + 50, i * 100 + 60));
                    objectList.add(new PointObject(point, "Inner-1 " + i + ":" + j + "-2", createRandomTagList(random), random.nextInt(40)));

                    point = geometryFactory.createPoint(new Coordinate(j * 100 + 50, i * 100 + 80));
                    objectList.add(new PointObject(point, "Inner-1 " + i + ":" + j + "-3", createRandomTagList(random), random.nextInt(40)));

                    point = geometryFactory.createPoint(new Coordinate(j * 100 + 75, i * 100 + 40));
                    objectList.add(new PointObject(point, "Inner-2 " + i + ":" + j + "-1", createRandomTagList(random), random.nextInt(40)));

                    point = geometryFactory.createPoint(new Coordinate(j * 100 + 75, i * 100 + 60));
                    objectList.add(new PointObject(point, "Inner-2 " + i + ":" + j + "-2", createRandomTagList(random), random.nextInt(40)));

                    point = geometryFactory.createPoint(new Coordinate(j * 100 + 75, i * 100 + 80));
                    objectList.add(new PointObject(point, "Inner-2 " + i + ":" + j + "-3", createRandomTagList(random), random.nextInt(40)));

                } else {
                    lineString = geometryFactory.createLineString(new Coordinate[]{new Coordinate(j * 100 + 90, i * 100 + 10), new Coordinate(j * 100 + 90, i * 100 + 100)});
                    objectList.add(new LineObject(lineString, "Street " + i + ":" + j + "-v", createRandomTagList(random), random.nextInt(30)));

                    lineString = geometryFactory.createLineString(new Coordinate[]{new Coordinate(j * 100, i * 100 + 10), new Coordinate(j * 100 + 90, i * 100 + 10)});
                    objectList.add(new LineObject(lineString, "Street " + i + ":" + j + "-h", createRandomTagList(random), random.nextInt(30)));

                    polygon = geometryFactory.createPolygon(new Coordinate[]{
                            new Coordinate(j * 100 + 10, i * 100 + 30),
                            new Coordinate(j * 100 + 10, i * 100 + 90),
                            new Coordinate(j * 100 + 60, i * 100 + 90),
                            new Coordinate(j * 100 + 60, i * 100 + 30),
                            new Coordinate(j * 100 + 10, i * 100 + 30)});
                    objectList.add(new PolygonObject(polygon, "House " + i + ":" + j, createRandomTagList(random), random.nextInt(70)));

                    point = geometryFactory.createPoint(new Coordinate(j * 100 + 75, i * 100 + 40));
                    objectList.add(new PointObject(point, "Tree " + i + ":" + j + "-1", createRandomTagList(random), random.nextInt(40)));

                    point = geometryFactory.createPoint(new Coordinate(j * 100 + 75, i * 100 + 60));
                    objectList.add(new PointObject(point, "Tree " + i + ":" + j + "-2", createRandomTagList(random), random.nextInt(40)));

                    point = geometryFactory.createPoint(new Coordinate(j * 100 + 75, i * 100 + 80));
                    objectList.add(new PointObject(point, "Tree " + i + ":" + j + "-3", createRandomTagList(random), random.nextInt(40)));

                    point = geometryFactory.createPoint(new Coordinate(j * 100 + 50, i * 100 + 40));
                    objectList.add(new PointObject(point, "Inner-1 " + i + ":" + j + "-1", createRandomTagList(random), random.nextInt(40)));

                    point = geometryFactory.createPoint(new Coordinate(j * 100 + 50, i * 100 + 60));
                    objectList.add(new PointObject(point, "Inner-1 " + i + ":" + j + "-2", createRandomTagList(random), random.nextInt(40)));

                    point = geometryFactory.createPoint(new Coordinate(j * 100 + 50, i * 100 + 80));
                    objectList.add(new PointObject(point, "Inner-1 " + i + ":" + j + "-3", createRandomTagList(random), random.nextInt(40)));

                    point = geometryFactory.createPoint(new Coordinate(j * 100 + 25, i * 100 + 40));
                    objectList.add(new PointObject(point, "Inner-2 " + i + ":" + j + "-1", createRandomTagList(random), random.nextInt(40)));

                    point = geometryFactory.createPoint(new Coordinate(j * 100 + 25, i * 100 + 60));
                    objectList.add(new PointObject(point, "Inner-2 " + i + ":" + j + "-2", createRandomTagList(random), random.nextInt(40)));

                    point = geometryFactory.createPoint(new Coordinate(j * 100 + 25, i * 100 + 80));
                    objectList.add(new PointObject(point, "Inner-2 " + i + ":" + j + "-3", createRandomTagList(random), random.nextInt(40)));
                }


            }
        }

        checkMap.setObjects(objectList);

    }


    private ArrayList<Tags> createRandomTagList(Random random) {
        ArrayList<Tags> list = new ArrayList<>();
//        Random random = new Random();

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
