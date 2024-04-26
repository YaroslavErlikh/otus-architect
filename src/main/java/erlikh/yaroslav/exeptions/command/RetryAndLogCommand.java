package erlikh.yaroslav.exeptions.command;

import erlikh.yaroslav.exeptions.command.interfaces.MyCommand;
import org.slf4j.Logger;

public class RetryAndLogCommand implements MyCommand {

    private Logger log;

    private MyCommand c;

    public RetryAndLogCommand(MyCommand c, Logger log) {
        this.c = c;
        this.log = log;
    }

    @Override
    public void execute() {
        log.info("retry and log");
        try {
            c.execute();
        } catch (RuntimeException ex) {
            log.error("retry failed", ex);
        }
    }
}
