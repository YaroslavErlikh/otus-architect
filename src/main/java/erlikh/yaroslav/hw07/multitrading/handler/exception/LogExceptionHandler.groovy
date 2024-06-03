package erlikh.yaroslav.hw07.multitrading.handler.exception

import erlikh.yaroslav.hw4.actions.basecommand.BaseCommand
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class LogExceptionHandler implements ExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogExceptionHandler.class)

    @Override
    void handle(Exception exception, BaseCommand command) {
        LOGGER.error("Exception type={};\nmessage={};\ncommand={}",
                exception.getClass().getName(), exception.getMessage(), command.getClass().getName())
    }
}
