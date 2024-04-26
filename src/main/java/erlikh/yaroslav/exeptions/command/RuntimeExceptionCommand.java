package erlikh.yaroslav.exeptions.command;

import erlikh.yaroslav.exeptions.command.interfaces.MyCommand;
import org.slf4j.Logger;

public class RuntimeExceptionCommand implements MyCommand {

    private Logger log;

    public RuntimeExceptionCommand(Logger log) {
        this.log = log;
    }

    @Override
    public void execute() {
        log.info("runtime exception");
        throw new RuntimeException("error");
    }
}
