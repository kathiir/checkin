package objects;

import com.bedatadriven.jackson.datatype.jts.serialization.GeometryDeserializer;
import com.bedatadriven.jackson.datatype.jts.serialization.GeometrySerializer;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.vividsolutions.jts.geom.*;
import world.Tags;

import java.util.ArrayList;
import java.util.List;

//@JsonDeserialize(as = LineObject.class)
//@JsonTypeInfo(
//        use = JsonTypeInfo.Id.CLASS,
//        include = JsonTypeInfo.As.PROPERTY,
//        property = "@class"
//)
public class LineObject extends MapObject {
    @JsonSerialize(using = GeometrySerializer.class)
    @JsonDeserialize(contentUsing = GeometryDeserializer.class)
//    @JsonProperty("geometry")
    private LineString geometry;

    public LineObject() {
    }

    public LineObject(LineString geometry, String name, List<Tags> list, int peopleCount) {
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

//    @JsonIgnore
    @Override
    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(LineString geometry) {
        this.geometry = geometry;
    }

//    public LineString getLine() {
//        return line;
//    }
}
