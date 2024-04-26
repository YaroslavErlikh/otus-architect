package erlikh.yaroslav.exeptions.command;

import erlikh.yaroslav.exeptions.command.interfaces.MyCommand;
import org.slf4j.Logger;

public class LogExceptionCommand implements MyCommand {

    private final Logger log;
    private MyCommand c;
    private Exception ex;

    public LogExceptionCommand(MyCommand c, Exception ex, Logger log) {
        this.c = c;
        this.ex = ex;
        this.log = log;
    }

    @Override
    public void execute() {
        log.info("log");
        log.error("error in command: {}", c.getClass().getSimpleName(), ex);
    }
}
