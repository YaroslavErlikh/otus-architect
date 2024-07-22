package erlikh.yaroslav.exeptions.command;

import erlikh.yaroslav.exeptions.command.interfaces.MyCommand;
import org.slf4j.Logger;

import java.util.Queue;

public class QueueRetryCommand implements MyCommand {

    private Logger log;

    private MyCommand c;
    private Queue<MyCommand> queue;

    public QueueRetryCommand(MyCommand c, Queue<MyCommand> queue, Logger log) {
        this.c = c;
        this.queue = queue;
        this.log = log;
    }

    @Override
    public void execute() {
        log.info("queue retry");
        queue.offer(new RetryCommand(c, log));
    }
}
