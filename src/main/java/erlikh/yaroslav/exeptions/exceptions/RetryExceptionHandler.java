package erlikh.yaroslav.exeptions.exceptions;

import erlikh.yaroslav.exeptions.command.RetryCommand;
import erlikh.yaroslav.exeptions.command.interfaces.MyCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RetryExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(RetryExceptionHandler.class);

    public RetryCommand handle(MyCommand c, Exception ex) {
        return new RetryCommand(c, log);
    }
}
