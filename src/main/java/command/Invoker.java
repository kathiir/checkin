package command;

import world.CheckMap;

import java.util.Stack;

public class Invoker {

    private Stack<Command> commandStack;
    private CheckMap checkMap;

    public Invoker(CheckMap checkMap) {
        commandStack = new Stack<>();
    }

    //do command and push?
    public void addCommand(Command command) {
        commandStack.push(command);
    }

    public void undoCommand() {
        Command command = commandStack.pop();
        command.unexecute(checkMap);
    }
}
