package erlikh.yaroslav.exeptions.command;

import erlikh.yaroslav.exeptions.command.interfaces.MyCommand;
import org.slf4j.Logger;

public class DoubleRetryAndLogCommand implements MyCommand {

    private Logger log;

    private MyCommand c;

    public DoubleRetryAndLogCommand(MyCommand c, Logger log) {
        this.c = c;
        this.log = log;
    }

    @Override
    public void execute() {
        log.info("double retry and log");
        try {
            c.execute();
        } catch (RuntimeException ex) {
            try {
                c.execute();
            } catch (RuntimeException ex1) {
                log.error("double retry failed", ex1);
            }
        }
    }
}
