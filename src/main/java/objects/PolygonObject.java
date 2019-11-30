package objects;

import com.bedatadriven.jackson.datatype.jts.serialization.GeometryDeserializer;
import com.bedatadriven.jackson.datatype.jts.serialization.GeometrySerializer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Polygon;
import world.Tags;

import java.util.ArrayList;
import java.util.List;

@JsonDeserialize(using =  MapObjectDeserializer.class)
public class PolygonObject extends MapObject {
    @JsonSerialize(using = GeometrySerializer.class)
//    @JsonDeserialize(contentUsing = GeometryDeserializer.class)
    private Polygon geometry;

    public PolygonObject() {
    }

    public PolygonObject(Polygon geometry, String name, List<Tags> list, int peopleCount) {
        this.geometry = geometry;
        this.setName(name);
        if (list != null) {
            this.setTags(list);
        } else {
            this.setTags(new ArrayList<>());
        }
        this.setPeopleCount(peopleCount);
        this.setChildren(new ArrayList<>());
    }

    @JsonIgnore
    @Override
    public Geometry getGeometry() {
        return geometry;
    }
}
