package erlikh.yaroslav.exeptions.exceptions;

import erlikh.yaroslav.exeptions.command.RetryAndLogCommand;
import erlikh.yaroslav.exeptions.command.interfaces.MyCommand;
import org.slf4j.Logger;

public class RetryAndLogExceptionHandler {

    private final Logger log;

    public RetryAndLogExceptionHandler(Logger log) {
        this.log = log;
    }

    public RetryAndLogCommand handle(MyCommand c, Exception ex) {
        return new RetryAndLogCommand(c, log);
    }
}
