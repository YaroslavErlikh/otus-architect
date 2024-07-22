package command.TestExceptionHandler

import erlikh.yaroslav.exeptions.command.LogExceptionCommand
import erlikh.yaroslav.exeptions.command.RetryCommand
import erlikh.yaroslav.exeptions.command.interfaces.MyCommand
import erlikh.yaroslav.exeptions.exceptions.DoubleRetryAndLogExceptionHandler
import erlikh.yaroslav.exeptions.exceptions.QueueLogExceptionHandler
import erlikh.yaroslav.exeptions.exceptions.QueueRetryExceptionHandler
import erlikh.yaroslav.exeptions.exceptions.RetryAndLogExceptionHandler
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.slf4j.Logger

class TestExceptionHandler {

    @Test
    void 'put_command_write_log_into_queue'() {
        Queue<MyCommand> queue = Mockito.mock(Queue)
        Logger log = Mockito.mock(Logger)
        MyCommand command = Mockito.mock(MyCommand)
        Exception ex = Mockito.mock(Exception)

        QueueLogExceptionHandler handler = new QueueLogExceptionHandler(queue, log)

        handler.handle(command, ex).execute()

        Mockito.verify(queue).offer(Mockito.any(LogExceptionCommand.class))
    }

    @Test
    void 'command_retry_into_queue'() {
        Queue<MyCommand> queue = Mockito.mock(Queue)
        Exception ex = Mockito.mock(Exception)
        MyCommand command = Mockito.mock(MyCommand)
        QueueRetryExceptionHandler handler = new QueueRetryExceptionHandler(queue)

        handler.handle(command, ex).execute()

        Mockito.verify(queue).offer(Mockito.any(RetryCommand.class))
    }

    @Test
    void 'retry_command_and_log'() {
        Logger log = Mockito.mock(Logger)
        MyCommand command = Mockito.mock(MyCommand)
        RuntimeException ex = Mockito.mock(RuntimeException)
        RetryAndLogExceptionHandler handler = new RetryAndLogExceptionHandler(log)

        Mockito.when(command.execute()).thenThrow(ex)

        handler.handle(command, ex).execute()

        Mockito.inOrder(command, log).verify(command).execute()
        Mockito.verify(log).info(Mockito.any(String.class))
        Mockito.verify(log).error("retry failed", ex)
    }

    @Test
    void  'twice_retry_command_and_log_error'() {
        MyCommand command = Mockito.mock(MyCommand)
        Logger log = Mockito.mock(Logger)
        RuntimeException ex = Mockito.mock(RuntimeException)
        DoubleRetryAndLogExceptionHandler handler = new DoubleRetryAndLogExceptionHandler(log)
        Mockito.doThrow(ex).when(command).execute()

        handler.handle(command, ex).execute()

        Mockito.verify(log).error("double retry failed", ex);
    }
}
