package world;

import java.util.ArrayList;
import java.util.List;

public class UserData {

    private String name;
    private List<Tags> tags;

    public UserData() {
        name = "Unnamed";
        tags = new ArrayList<>();
    }

    public UserData(String name, List<Tags> tags) {
        this.name = name;
        this.tags = tags;
    }

    public String getName() {
        return name;
    }

    //?
    public List<Tags> getTags() {
        return tags;
    }

    @Override
    public boolean equals(Object obj) {
        if (getClass() != obj.getClass())
            return false;
        UserData user = (UserData) obj;
        return name.equals(user.name)
                && tags.equals(user.tags);
    }
}
