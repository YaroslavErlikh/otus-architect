package command.TestMyCommand

import erlikh.yaroslav.exeptions.command.LogExceptionCommand
import erlikh.yaroslav.exeptions.command.RetryCommand
import erlikh.yaroslav.exeptions.command.interfaces.MyCommand
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.slf4j.Logger

class TestMyCommand {

    @Test
    void 'have_a_command_record_to_log_exception'() {
        MyCommand c = Mockito.mock(MyCommand.class)
        Exception ex = Mockito.mock(Exception.class)
        Logger log = Mockito.mock(Logger) //LoggerFactory.getLogger(TestMyCommand.class)

        LogExceptionCommand command = new LogExceptionCommand(c, ex, log)

        command.execute()

        Mockito.verify(log).error("error in command: {}", c.getClass().getSimpleName(), ex)
    }

    @Test
    void 'have_a_command_retry_command_thrown_exception'() {
        MyCommand c = Mockito.mock(MyCommand.class)
        Logger log = Mockito.mock(Logger)

        RetryCommand command = new RetryCommand(c, log)

        command.execute()

        Mockito.verify(c).execute()
    }
}
