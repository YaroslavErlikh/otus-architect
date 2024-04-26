package erlikh.yaroslav.exeptions.command;

import erlikh.yaroslav.exeptions.command.interfaces.MyCommand;
import org.slf4j.Logger;

public class RetryCommand implements MyCommand {

    private Logger log;

    private MyCommand c;

    public RetryCommand(MyCommand c, Logger log) {
        this.c = c;
        this.log = log;
    }

    @Override
    public void execute() {
        log.info("retry");
        c.execute();
    }
}
