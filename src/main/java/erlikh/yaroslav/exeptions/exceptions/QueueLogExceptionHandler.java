package erlikh.yaroslav.exeptions.exceptions;

import erlikh.yaroslav.exeptions.command.QueueLogExceptionCommand;
import erlikh.yaroslav.exeptions.command.interfaces.MyCommand;
import org.slf4j.Logger;

import java.util.Queue;

public class QueueLogExceptionHandler {

    private final Logger log;

    private Queue<MyCommand> queue;

    public QueueLogExceptionHandler(Queue<MyCommand> queue, Logger log) {
        this.queue = queue;
        this.log = log;
    }

    public QueueLogExceptionCommand handle(MyCommand c, Exception ex) {
        return new QueueLogExceptionCommand(c, ex, queue, log);
    }
}
