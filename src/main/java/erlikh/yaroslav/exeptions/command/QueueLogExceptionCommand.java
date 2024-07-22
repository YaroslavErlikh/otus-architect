package erlikh.yaroslav.exeptions.command;

import erlikh.yaroslav.exeptions.command.interfaces.MyCommand;
import org.slf4j.Logger;

import java.util.Queue;

public class QueueLogExceptionCommand implements MyCommand {

    private Logger log;

    private MyCommand c;
    private Exception ex;
    private Queue<MyCommand> queue;

    public QueueLogExceptionCommand(MyCommand c, Exception ex, Queue<MyCommand> queue, Logger log) {
        this.c = c;
        this.ex = ex;
        this.queue = queue;
        this.log = log;
    }

    @Override
    public void execute() {
        log.info("put log in queue");
        queue.offer(new LogExceptionCommand(c, ex, log));
    }
}
