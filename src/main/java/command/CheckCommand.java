package command;

import world.CheckMap;
import world.Request;
import world.UserData;

public class CheckCommand implements Command {

    private Request request;

    public CheckCommand(Request request) {
        this.request = request;
    }

    @Override
    public void execute(CheckMap checkMap) {
        checkMap.checkIn(request);
    }

    @Override
    public void unexecute(CheckMap checkMap) {
        checkMap.checkout(request.getUser());
    }

    public Request getRequest() {
        return request;
    }
}
