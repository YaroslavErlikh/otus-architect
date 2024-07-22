package erlikh.yaroslav.exeptions;

import erlikh.yaroslav.exeptions.command.RuntimeExceptionCommand;
import erlikh.yaroslav.exeptions.command.StopCommand;
import erlikh.yaroslav.exeptions.command.WaitCommand;
import erlikh.yaroslav.exeptions.command.interfaces.MyCommand;
import erlikh.yaroslav.exeptions.exceptions.ExceptionHandler;
import erlikh.yaroslav.exeptions.exceptions.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class MainExceptions {

    public static void main(String[] args) {

        Logger log = LoggerFactory.getLogger(MainExceptions.class);

        AtomicBoolean stop = new AtomicBoolean(false);
        BlockingQueue<MyCommand> queue = new ArrayBlockingQueue<>(16);

        ExceptionHandler.register(
                RuntimeExceptionCommand.class,
                RuntimeException.class,
                new QueueRetryExceptionHandler(queue)::handle
        );

        ExceptionHandler.register(
                RuntimeExceptionCommand.class,
                RuntimeException.class,
                new QueueLogExceptionHandler(queue, log)::handle
        );

        ExceptionHandler.register(
                RuntimeExceptionCommand.class,
                RuntimeException.class,
                new RetryExceptionHandler()::handle
        );

        ExceptionHandler.register(
                RuntimeExceptionCommand.class,
                RuntimeException.class,
                new RetryAndLogExceptionHandler(log)::handle
        );

        ExceptionHandler.register(
                RuntimeExceptionCommand.class,
                RuntimeException.class,
                new DoubleRetryAndLogExceptionHandler(log)::handle
        );

        queue.offer(new RuntimeExceptionCommand(log));

        for (int i = 0; i < 3; i++) {
            queue.offer(new WaitCommand(log));
        }


        Thread thread = new Thread(() -> {
            while (!stop.get()) {
                MyCommand command = queue.poll() != null ? queue.poll() : new StopCommand(stop, log);
                try {
                    command.execute();
                } catch (Exception e) {
                    ExceptionHandler.handle(command, e).execute();
                }
            }
        });
        try {
            thread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
