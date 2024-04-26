package erlikh.yaroslav.exeptions.exceptions;

import erlikh.yaroslav.exeptions.command.DoubleRetryAndLogCommand;
import erlikh.yaroslav.exeptions.command.interfaces.MyCommand;
import org.slf4j.Logger;

public class DoubleRetryAndLogExceptionHandler {

    private final Logger log;

    public DoubleRetryAndLogExceptionHandler(Logger log) {
        this.log = log;
    }

    public DoubleRetryAndLogCommand handle(MyCommand c, Exception ex) {
        return new DoubleRetryAndLogCommand(c, log);
    }
}
