package command;

import objects.MapObject;
import world.CheckMap;
import world.Request;

public class CheckCommand implements Command {

    private Request request;
    private MapObject previous;

    public CheckCommand(Request request) {
        this.request = request;
    }

    @Override
    public void execute(CheckMap checkMap) {
        checkMap.getMapObject(request.getUser());
        checkMap.checkIn(request);
    }

    @Override
    public void unexecute(CheckMap checkMap) {
//        checkMap.checkout(request.getUser());
        checkMap.checkIn(request.getUser(), previous);
    }

}
