package objects;

import com.bedatadriven.jackson.datatype.jts.JtsModule;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import com.vividsolutions.jts.geom.*;
import org.locationtech.jts.linearref.LinearLocation;
import world.Tags;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MapObjectDeserializer extends StdDeserializer<MapObject> {

    public MapObjectDeserializer() {
        super(MapObject.class);
    }

    public MapObjectDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public MapObject deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
            throws IOException, JsonProcessingException {


//        ObjectMapper mapper = (ObjectMapper) jsonParser.getCodec();

        ObjectMapper mapper = new ObjectMapper();

        JsonNode node = mapper.readTree(jsonParser);

        String name = node.get("name").asText();

        List<Tags> tags = new ArrayList<>();
        JsonNode tagsNode = node.get("tags");
        for (int i = 0; i < tagsNode.size(); i++) {
            tags.add(Tags.valueOf(tagsNode.get(i).asText()));
            System.out.println(tags.get(i));
        }

        int peopleCount = node.get("peopleCount").asInt();

        String type = node.get("geometry").get("type").asText();

        JsonNode geometry = node.get("geometry");
        JsonNode array = geometry.get("coordinates");

        GeometryFactory factory = new GeometryFactory();

        switch (type) {
            case "Point":
                Coordinate coordinate = new Coordinate(array.get(0).asDouble(), array.get(1).asDouble());
                Point point = factory.createPoint(coordinate);

                return new PointObject(point, name, tags, peopleCount);

            case "LineString": {
                Coordinate[] coordinates = new Coordinate[array.size()];
                for (int i = 0; i < array.size(); i++) {
                    JsonNode coord = array.get(i);
                    coordinates[i] = new Coordinate(coord.get(0).asDouble(), coord.get(1).asDouble());
                }
                LineString lineString = factory.createLineString(coordinates);

                return new LineObject(lineString, name, tags, peopleCount);

            }
            case "Polygon": {
                JsonNode ring = array.get(0);
                Coordinate[] coordinates = new Coordinate[ring.size()];
                for (int i = 0; i < ring.size(); i++) {
                    coordinates[i] = new Coordinate(ring.get(0).asDouble(), ring.get(1).asDouble());
                }
                LinearRing shell = factory.createLinearRing(coordinates);

                LinearRing[] holes = new LinearRing[array.size() - 1];
                for (int i = 1; i < array.size(); i++) {
                    coordinates = new Coordinate[array.get(i).size()];
                    for (int j = 0; j < array.get(i).size(); j++) {
                        coordinates[j] = new Coordinate(array.get(i).get(0).asDouble(), array.get(i).get(1).asDouble());
                    }
                    holes[i - 1] = factory.createLinearRing(coordinates);
                }

                Polygon polygon = factory.createPolygon(shell, holes);

                return new PolygonObject(polygon, name, tags, peopleCount);

            }
        }

        return null;
    }
}
