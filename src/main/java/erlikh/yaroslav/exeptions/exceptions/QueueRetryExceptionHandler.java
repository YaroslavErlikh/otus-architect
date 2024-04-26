package erlikh.yaroslav.exeptions.exceptions;

import erlikh.yaroslav.exeptions.command.QueueRetryCommand;
import erlikh.yaroslav.exeptions.command.interfaces.MyCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Queue;

public class QueueRetryExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(QueueRetryExceptionHandler.class);

    private Queue<MyCommand> queue;

    public QueueRetryExceptionHandler(Queue<MyCommand> queue) {
        this.queue = queue;
    }

    public QueueRetryCommand handle(MyCommand c, Exception ex) {
        return new QueueRetryCommand(c, queue, log);
    }
}
