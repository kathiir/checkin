package command;

import world.CheckMap;
import world.Request;

import java.util.Stack;

public class Invoker {

    private Stack<Command> commandStack;
    private CheckMap checkMap;

    public Invoker(CheckMap checkMap) {
        commandStack = new Stack<>();
        this.checkMap = checkMap;
    }

    //do command and push?
    public void doCommand(Command command) {
        command.execute(checkMap);
        commandStack.push(command);
    }

    public void undoLastCommand() {
        if (!commandStack.empty()) {
            Command command = commandStack.pop();
            command.unexecute(checkMap);
            if (!commandStack.empty()) {
                commandStack.peek().execute(checkMap);
            }

        }
    }

    public Request getRequest() {
        if (!commandStack.empty()) {
            return ((CheckCommand) commandStack.peek()).getRequest();
        }
        return null;
    }
}
