package comparator;

import objects.MapObject;
import world.Tags;

import java.util.Comparator;
import java.util.List;

public class SimpleMapObjectComparator implements Comparator<MapObject> {
    private List<Tags> tagList;

    public SimpleMapObjectComparator(List<Tags> tagList) {
        this.tagList = tagList;
    }

    @Override
    public int compare(MapObject o1, MapObject o2) {
        for (Tags tag : tagList) {
            if (o1.getTags().contains(tag) && o2.getTags().contains(tag)) {
                return o1.getPeopleCount() - o2.getPeopleCount();
            } else if (o1.getTags().contains(tag)) {
                return 1;
            } else if (o2.getTags().contains(tag)) {
                return -1;
            }
        }
        return o1.getPeopleCount() - o2.getPeopleCount();
    }
}
