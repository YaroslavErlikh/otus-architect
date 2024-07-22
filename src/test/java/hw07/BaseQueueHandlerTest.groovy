package hw07

import erlikh.yaroslav.hw07.multitrading.command.BaseCommandQueue
import erlikh.yaroslav.hw07.multitrading.command.HardStopCommand
import erlikh.yaroslav.hw07.multitrading.command.QueueCommand
import erlikh.yaroslav.hw07.multitrading.command.SoftStopCommand
import erlikh.yaroslav.hw07.multitrading.handler.exception.ExceptionHandler
import erlikh.yaroslav.hw07.multitrading.handler.queue.BaseQueueHandler
import erlikh.yaroslav.hw07.multitrading.handler.queue.BaseQueueHandlerImpl
import erlikh.yaroslav.hw07.multitrading.ioc.IoC
import erlikh.yaroslav.hw07.multitrading.ioc.ScopeBaseStrategy
import erlikh.yaroslav.hw4.actions.basecommand.BaseCommand
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

import static org.assertj.core.api.Assertions.assertThat
import static org.mockito.Mockito.*

class BaseQueueHandlerTest {

    private BaseQueueHandler basicQueueHandler
    private ExceptionHandler exceptionHandler
    private BaseCommandQueue commandQueue
    private BaseCommand emptyCommand

    @BeforeEach
    void setUp() {
        ScopeBaseStrategy scopeBasedStrategy = new ScopeBaseStrategy()
        (new ScopeBaseStrategy.InitScopeBaseIoCCommand(scopeBasedStrategy)).execute()

        basicQueueHandler = new BaseQueueHandlerImpl()

        exceptionHandler = mock(ExceptionHandler.class)
        (IoC.resolve("IoC.Register", "Exception.Handler", args1 -> exceptionHandler) as BaseCommand).execute()

        commandQueue = mock(BaseCommandQueue.class)
        (IoC.resolve("IoC.Register", "BaseCommandQueue", args1 -> commandQueue) as BaseCommand).execute()

        new QueueCommand().execute()

        emptyCommand = mock(BaseCommand.class)
        (IoC.resolve("IoC.Register", "BaseCommandQueue.EmptyCommand", args1 -> emptyCommand) as BaseCommand).execute()
    }

    @Test
    void 'process_queue_infinity'() throws InterruptedException {
        CountDownLatch emptyCommandInvoked = new CountDownLatch(5)

        doAnswer(invocation -> {
            emptyCommandInvoked.countDown()
            return null
        }).when(emptyCommand).execute()

        new Thread(() -> basicQueueHandler.handle()).start()

        boolean invoked = emptyCommandInvoked.await(2, TimeUnit.SECONDS)
        assertThat(invoked).isEqualTo(true)
    }

    @Test
    void 'continue_process_if_throw_exception'() throws InterruptedException {
        BaseCommand command = mock(BaseCommand.class)
        when(commandQueue.readFirst()).thenReturn(command)

        CountDownLatch commandInvoked = new CountDownLatch(5)

        doAnswer(invocation -> {
            commandInvoked.countDown()
            throw new IllegalArgumentException("TEST")
        }).when(command).execute()

        new Thread(() -> basicQueueHandler.handle()).start()

        boolean invoked = commandInvoked.await(2, TimeUnit.SECONDS)
        assertThat(invoked).isEqualTo(true)

        verify(exceptionHandler, atLeast(4)).handle(any(), any())
    }

    @Test
    void 'evaluate_commands_and_return_empty'() throws InterruptedException {
        BaseCommand command = mock(BaseCommand.class)
        when(commandQueue.readFirst()).thenReturn(command).thenReturn(command).thenReturn(null)

        CountDownLatch commandInvoked = new CountDownLatch(2)
        CountDownLatch emptyCommandInvoked = new CountDownLatch(2)

        doAnswer(invocation -> {
            commandInvoked.countDown()
            return null
        }).when(command).execute()

        doAnswer(invocation -> {
            emptyCommandInvoked.countDown()
            return null
        }).when(emptyCommand).execute()

        new Thread(() -> basicQueueHandler.handle()).start()

        boolean invokedCommand = commandInvoked.await(2, TimeUnit.SECONDS)
        boolean invokedEmptyCommand = emptyCommandInvoked.await(2, TimeUnit.SECONDS)
        assertThat(invokedCommand).isEqualTo(true)
        assertThat(invokedEmptyCommand).isEqualTo(true)
    }

    @Test
    void 'exit_hard_stop'() throws InterruptedException {
        BaseCommand command = mock(BaseCommand.class)
        when(commandQueue.readFirst()).thenReturn(command)

        CountDownLatch commandInvoked = new CountDownLatch(3)

        doAnswer(invocation -> {
            commandInvoked.countDown()
            return null
        }).when(command).execute()

        CountDownLatch queueHandlerStopped = new CountDownLatch(1)

        Thread thread = new Thread(() -> {
            basicQueueHandler.handle()
            queueHandlerStopped.countDown()
        })
        thread.start()

        boolean invoked = commandInvoked.await(2, TimeUnit.SECONDS)
        assertThat(invoked).isEqualTo(true)

        new HardStopCommand().execute()
        boolean invokedQueueHandler = queueHandlerStopped.await(2, TimeUnit.SECONDS)
        assertThat(invokedQueueHandler).isEqualTo(true)
    }

    @Test
    void 'exit_soft_stop'() throws InterruptedException {
        BaseCommand command1 = mock(BaseCommand.class)
        when(commandQueue.readFirst()).thenReturn(command1)

        CountDownLatch command1Invoked = new CountDownLatch(3)

        doAnswer(invocation -> {
            command1Invoked.countDown()
            return null
        }).when(command1).execute()

        CountDownLatch queueHandlerStopped = new CountDownLatch(1)

        Thread thread = new Thread(() -> {
            basicQueueHandler.handle()
            queueHandlerStopped.countDown()
        })
        thread.start()

        boolean invokedCommand1 = command1Invoked.await(2, TimeUnit.SECONDS)
        assertThat(invokedCommand1).isEqualTo(true)

        new SoftStopCommand().execute()

        BaseCommand command2 = mock(BaseCommand.class)
        when(commandQueue.readFirst()).thenReturn(command2)

        CountDownLatch command2Invoked = new CountDownLatch(3)

        doAnswer(invocation -> {
            command2Invoked.countDown()
            return null
        }).when(command2).execute()

        boolean invokedCommand2 = command2Invoked.await(2, TimeUnit.SECONDS)
        assertThat(invokedCommand2).isEqualTo(true)

        when(commandQueue.readFirst()).thenReturn(null)

        boolean invokedQueueHandler = queueHandlerStopped.await(2, TimeUnit.SECONDS)
        assertThat(invokedQueueHandler).isEqualTo(true)
    }
}