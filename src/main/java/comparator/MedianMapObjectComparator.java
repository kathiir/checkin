package comparator;

import objects.MapObject;
import world.Tags;

import java.util.Comparator;
import java.util.List;

public class MedianMapObjectComparator implements Comparator<MapObject> {
    private List<Tags> tagList;

    public MedianMapObjectComparator(List<Tags> tagList) {
        this.tagList = tagList;
    }

    // TODO: 24.11.2019 сделать компаратор
    @Override
    public int compare(MapObject o1, MapObject o2) {
        if (getWeight(o1) > getWeight(o2)) {
            return 1;
        } else if (getWeight(o1) < getWeight(o2)) {
            return -1;
        } else {
            return o1.getPeopleCount() - o2.getPeopleCount();
        }
    }

    private int getWeight(MapObject object) {
        List<Tags> list = object.getTags();
        int weight = 0;
        for (int i = 0; i < tagList.size(); i++) {
            if (list.contains(tagList.get(i))) {
                weight += tagList.size() - i;
            }
        }
        return 0;
    }
}
