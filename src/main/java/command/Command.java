package command;

import world.CheckMap;

public interface Command {
    void execute(CheckMap checkMap);
    void unexecute(CheckMap checkMap);
}
