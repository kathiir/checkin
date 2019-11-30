package objects;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.vividsolutions.jts.geom.Geometry;
import world.Tags;

import java.util.ArrayList;
import java.util.List;

//@JsonTypeInfo(
//        use = JsonTypeInfo.Id.CLASS,
//        include = JsonTypeInfo.As.PROPERTY,
//        property = "@class"
//)
@JsonSubTypes({
        @JsonSubTypes.Type(value = LineObject.class),
        @JsonSubTypes.Type(value = PointObject.class),
        @JsonSubTypes.Type(value = PolygonObject.class)
})
@JsonDeserialize(using =  MapObjectDeserializer.class)
public class MapObject {
    private String name;
    private List<Tags> tags;
    private int peopleCount;
    @JsonIgnoreProperties("children")
    private List<MapObject> children;

    public String getName() {
        return name;
    }

    public List<Tags> getTags() {
        return tags;
    }

    public int getPeopleCount() {
        return peopleCount;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTags(List<Tags> tags) {
        this.tags = tags;
    }

    public void setPeopleCount(int peopleCount) {
        this.peopleCount = peopleCount;
    }

    @JsonIgnore
    public List<MapObject> getChildren() {
        return children;
    }

    public void setChildren(List<MapObject> children) {
        this.children = children;
    }

//    @JsonIgnore
    public Geometry getGeometry() {
        return null;
    }

    @JsonIgnore
    public List<MapObject> getAllChildren() {
        List<MapObject> list = new ArrayList<>();
        for (MapObject object : children) {
            list.add(object);
            list.addAll(object.getAllChildren());
        }
        return list;
    }
}
