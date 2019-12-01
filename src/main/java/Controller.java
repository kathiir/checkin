import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.sun.javafx.geom.Area;
import command.CheckCommand;
import command.Invoker;
import comparator.MapObjectComparatorType;
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
import javafx.stage.FileChooser;
import objects.*;
import org.controlsfx.control.CheckComboBox;
import com.vividsolutions.jts.geom.*;
import org.controlsfx.control.CheckModel;
import view.ResizableCanvas;
import view.ScreenConverter;
import view.ScreenPoint;
import world.CheckMap;
import world.Request;
import world.Tags;
import world.UserData;

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
    public TextField xTextField;
    public TextField yTextField;
    public ComboBox<MapObjectComparatorType> comparatorSelector;
    public TextArea objectInf;

    private ScreenConverter sc;
    private CheckMap checkMap;

    private Invoker invoker;

    private MapObject selectedMapObject, checkedMapObject;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        checkMap = new CheckMap();
        sc = new ScreenConverter(0, 500, 500, 500,
                500, 500);

        canvas.setPaintListener(this);

        tagsCheckComboBox.getItems().setAll(Tags.values());

        comparatorSelector.getItems().setAll(MapObjectComparatorType.values());

        invoker = new Invoker(checkMap);
        canvas.paint();
    }

    public void checkIn(ActionEvent actionEvent) {
        try {
            double r = Double.parseDouble(radiusTextField.getText());
            double x = Double.parseDouble(xTextField.getText());
            double y = Double.parseDouble(yTextField.getText());

            UserData userData = new UserData(nameTextField.getText(),
                    Arrays.asList(tagsCheckComboBox.getCheckModel().getCheckedItems().toArray(new Tags[0])));
            request = new Request(x, y, r, userData);
            invoker.doCommand(new CheckCommand(request));

            checkedMapObject = checkMap.getMapObject(userData);

            setObjectInf(checkedMapObject);

            canvas.paint();
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

        canvas.paint();
    }


    //------------------//

    @Override
    public void paint(GraphicsContext context) {
        context.setFill(Color.rgb(255, 254, 232));
        context.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        drawMapObjects(context);
        try {
            ScreenPoint topLeft = sc.r2s(new Coordinate(request.getX() - request.getR(), request.getY() - request.getR()));
            ScreenPoint bottomRight = sc.r2s(new Coordinate(request.getX() + request.getR(), request.getY() + request.getR()));

            context.setStroke(Color.RED);
            context.strokeOval(topLeft.getX(), bottomRight.getY(),
                    - topLeft.getX() + bottomRight.getX() , topLeft.getY() - bottomRight.getY());

            context.setStroke(Color.BLACK);
        } catch (NullPointerException e) {

        }

    }

    private void drawMapObjects(GraphicsContext context) {
        List<Geometry> geometries = new ArrayList<>();
        List<MapObject> mapObjects = checkMap.getAllObjects();
        for (MapObject mapObject : mapObjects) {
            if (mapObject.equals(selectedMapObject)) {
                drawMapObject(context, mapObject, Color.rgb(164, 212, 255), Color.rgb(0, 41, 255));
            } else if (mapObject.equals(checkedMapObject)) {
                drawMapObject(context, mapObject, Color.rgb(195, 255, 195), Color.rgb(68, 215, 0));
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

    private Request request;

    public void undoCheckin(ActionEvent actionEvent) {
        try {
            invoker.undoLastCommand();

            request = invoker.getRequest();
            checkedMapObject = checkMap.getMapObject(request.getUser());

            nameTextField.setText(request.getUser().getName());
            xTextField.setText(Double.toString(request.getX()));
            yTextField.setText(Double.toString(request.getY()));
            radiusTextField.setText(Double.toString(request.getR()));

            CheckModel<Tags> checkModel = tagsCheckComboBox.getCheckModel();
            checkModel.clearChecks();
            //not working
            for (int i = 0; i < request.getUser().getTags().size(); i++) {
                checkModel.check(request.getUser().getTags().get(i));
            }

        } catch (NullPointerException e) {
        }

    }

    public void changeComparator(ActionEvent actionEvent) {
        checkMap.setComparatorType(comparatorSelector.getValue());
    }


    private void setObjectInf(MapObject mapObject) {
        StringBuilder sb = new StringBuilder();
        sb.append("Название: ").append(mapObject.getName()).append("\n")
                .append("Тэги: ").append(mapObject.getTags().toString());


        objectInf.setText(sb.toString());
    }
}
