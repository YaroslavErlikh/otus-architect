package erlikh.yaroslav.exeptions.command;

import erlikh.yaroslav.exeptions.command.interfaces.MyCommand;
import org.slf4j.Logger;

public class WaitCommand implements MyCommand {

    private Logger log;

    public WaitCommand(Logger log) {
        this.log = log;
    }

    @Override
    public void execute() {
        log.info("wait");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
