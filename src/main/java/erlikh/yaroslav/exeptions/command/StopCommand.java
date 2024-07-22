package erlikh.yaroslav.exeptions.command;

import erlikh.yaroslav.exeptions.command.interfaces.MyCommand;
import org.slf4j.Logger;

import java.util.concurrent.atomic.AtomicBoolean;

public class StopCommand implements MyCommand {

    private Logger log;
    private AtomicBoolean stop;

    public StopCommand(AtomicBoolean stop, Logger log) {
        this.stop = stop;
        this.log = log;
    }

    @Override
    public void execute() {
        log.info("stop");
        stop.set(true);
    }
}
