package hw11

import erlikh.yaroslav.hw11.command.HardStopCommand
import erlikh.yaroslav.hw11.command.MoveToCommand
import erlikh.yaroslav.hw11.command.RunCommand
import erlikh.yaroslav.hw11.command.interfaces.CommandQueue
import erlikh.yaroslav.hw11.handler.exceptions.interfaces.ExceptionHandler
import erlikh.yaroslav.hw11.handler.queue.BasicQueueHandler
import erlikh.yaroslav.hw11.ioc.IoC
import erlikh.yaroslav.hw11.ioc.ScopeBasedStrategy
import erlikh.yaroslav.hw11.state.DefaultState
import erlikh.yaroslav.hw11.state.MoveToState
import erlikh.yaroslav.hw4.actions.basecommand.BaseCommand
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

import static org.assertj.core.api.Assertions.assertThat
import static org.mockito.ArgumentMatchers.any
import static org.mockito.ArgumentMatchers.eq
import static org.mockito.Mockito.*

class BasicQueueHandlerTest {

    private BasicQueueHandler basicQueueHandler
    private ExceptionHandler exceptionHandler
    private CommandQueue commandQueue

    @BeforeEach
    void setUp() {
        ScopeBasedStrategy scopeBasedStrategy = new ScopeBasedStrategy()
        (new ScopeBasedStrategy.InitScopeBasedIoCCommand(scopeBasedStrategy)).execute()

        commandQueue = mock(CommandQueue.class)

        exceptionHandler = mock(ExceptionHandler.class)
        ((BaseCommand) IoC.resolve("IoC.Register", "Exception.Handler", args1 -> exceptionHandler)).execute()
    }

    @Test
    void 'process_queue_infinity'() throws InterruptedException {
        // инициализация обработчика очереди с обычным режимом обработки команд
        basicQueueHandler = new BasicQueueHandler(new DefaultState(), commandQueue)

        BaseCommand command = mock(BaseCommand.class)
        when(commandQueue.readFirst()).thenReturn(command)

        CountDownLatch commandInvoked = new CountDownLatch(5)

        doAnswer(invocation -> {
            commandInvoked.countDown()
            return null
        }).when(command).execute()

        // запускаем в отдельном потоке
        new Thread(() -> basicQueueHandler.handle()).start()

        boolean invoked = commandInvoked.await(2, TimeUnit.SECONDS)
        assertThat(invoked).isEqualTo(true)
    }

    @Test
    void 'next_process_queue_if_throw_exception'() throws InterruptedException {
        // инициализация обработчика очереди с обычным режимом обработки команд
        basicQueueHandler = new BasicQueueHandler(new DefaultState(), commandQueue)

        BaseCommand command = mock(BaseCommand.class)
        when(commandQueue.readFirst()).thenReturn(command)

        CountDownLatch commandInvoked = new CountDownLatch(5)

        doAnswer(invocation -> {
            commandInvoked.countDown()
            throw new IllegalArgumentException("TEST")
        }).when(command).execute()

        // запускаем в отдельном потоке
        new Thread(() -> basicQueueHandler.handle()).start()

        boolean invoked = commandInvoked.await(2, TimeUnit.SECONDS)
        assertThat(invoked).isEqualTo(true)

        verify(exceptionHandler, atLeast(4)).handle(any(), any())
    }

    @Test
    void 'next_process_queue_if_queue_empty'() throws InterruptedException {
        // инициализация обработчика очереди с обычным режимом обработки команд
        basicQueueHandler = new BasicQueueHandler(new DefaultState(), commandQueue)

        BaseCommand command = mock(BaseCommand.class)
        when(commandQueue.readFirst()).thenReturn(command).thenReturn(command).thenReturn(null).thenReturn(command)

        CountDownLatch commandInvoked = new CountDownLatch(3)

        doAnswer(invocation -> {
            commandInvoked.countDown()
            return null;
        }).when(command).execute()

        // запускаем в отдельном потоке
        new Thread(() -> basicQueueHandler.handle()).start()

        boolean invokedCommand = commandInvoked.await(2, TimeUnit.SECONDS)
        assertThat(invokedCommand).isEqualTo(true)
    }

    @Test
    void 'exit_process_hard_stop_default_state'() throws InterruptedException {
        // инициализация обработчика очереди с обычным режимом обработки команд
        basicQueueHandler = new BasicQueueHandler(new DefaultState(), commandQueue)

        // очередь всегда возвращает command
        BaseCommand command = mock(BaseCommand.class)
        when(commandQueue.readFirst()).thenReturn(command)

        CountDownLatch commandInvoked = new CountDownLatch(3)

        doAnswer(invocation -> {
            commandInvoked.countDown()
            return null
        }).when(command).execute()

        // запускаем в отдельном потоке
        CountDownLatch queueHandlerStopped = new CountDownLatch(1)

        Thread thread = new Thread(() -> {
            basicQueueHandler.handle()
            queueHandlerStopped.countDown()
        })
        thread.start()

        // проверяем, что действительно всё время возвращается command
        boolean invoked = commandInvoked.await(2, TimeUnit.SECONDS)
        assertThat(invoked).isEqualTo(true)

        // посылаем hard stop -> выполнение прекращается (несмотря на наличие command в очереди)
        when(commandQueue.readFirst()).thenReturn(new HardStopCommand())
        boolean invokedQueueHandler = queueHandlerStopped.await(2, TimeUnit.SECONDS)
        assertThat(invokedQueueHandler).isEqualTo(true)
    }

    @Test
    void 'exit_process_hard_stop_moveto_state'() throws InterruptedException {
        // создаём очередь с режимом MoveTo
        CommandQueue otherQueue = mock(CommandQueue.class)
        basicQueueHandler = new BasicQueueHandler(new MoveToState(otherQueue), commandQueue)

        // очередь всегда возвращает command
        BaseCommand command = mock(BaseCommand.class)
        when(commandQueue.readFirst()).thenReturn(command)

        CountDownLatch commandInvoked = new CountDownLatch(3)

        doAnswer(invocation -> {
            commandInvoked.countDown()
            return null
        }).when(otherQueue).addLast(eq(command))

        // запускаем в отдельном потоке
        CountDownLatch queueHandlerStopped = new CountDownLatch(1)

        Thread thread = new Thread(() -> {
            basicQueueHandler.handle()
            queueHandlerStopped.countDown()
        })
        thread.start()

        // проверяем, что command всё время возвращаются и кладутся в другую очередь
        boolean invoked = commandInvoked.await(2, TimeUnit.SECONDS)
        assertThat(invoked).isEqualTo(true)

        // посылаем hard stop -> выполнение прекращается (несмотря на наличие command в очереди)
        when(commandQueue.readFirst()).thenReturn(new HardStopCommand()).thenReturn(command)
        boolean invokedQueueHandler = queueHandlerStopped.await(2, TimeUnit.SECONDS)
        assertThat(invokedQueueHandler).isEqualTo(true)
    }

    @Test
    void 'get_command_move_and_change_mode_to_moveto'() throws InterruptedException {
        // инициализация обработчика очереди с обычным режимом обработки команд
        basicQueueHandler = new BasicQueueHandler(new DefaultState(), commandQueue)

        // очередь сначала переходит в режим MoveTo, а затем всегда возвращает command
        BaseCommand command = mock(BaseCommand.class)
        CommandQueue otherQueue = mock(CommandQueue.class)
        BaseCommand moveToCommand = spy(new MoveToCommand(otherQueue))
        when(commandQueue.readFirst()).thenReturn(moveToCommand).thenReturn(command)

        CountDownLatch moveToCommandInvoked = new CountDownLatch(1)
        CountDownLatch commandInvoked = new CountDownLatch(3)

        doAnswer(invocation -> {
            moveToCommandInvoked.countDown()
            return invocation.callRealMethod()
        }).when(moveToCommand).execute()
        doAnswer(invocation -> {
            commandInvoked.countDown()
            return null
        }).when(otherQueue).addLast(eq(command))

        // запускаем в отдельном потоке
        Thread thread = new Thread(() -> basicQueueHandler.handle())
        thread.start()

        // проверяем, что обработалась команда MoveToCommand
        boolean moveToInvoked = commandInvoked.await(2, TimeUnit.SECONDS)
        assertThat(moveToInvoked).isEqualTo(true)

        // проверяем, что потом command всё время возвращаются и кладутся в другую очередь
        boolean invoked = commandInvoked.await(2, TimeUnit.SECONDS)
        assertThat(invoked).isEqualTo(true)
    }

    @Test
    void 'get_command_run_and_change_mode_to_default'() throws InterruptedException {
        // создаём очередь с режимом MoveTo
        CommandQueue otherQueue = mock(CommandQueue.class)
        basicQueueHandler = new BasicQueueHandler(new MoveToState(otherQueue), commandQueue)

        // очередь всегда возвращает command
        BaseCommand command = mock(BaseCommand.class)
        when(commandQueue.readFirst()).thenReturn(command)

        CountDownLatch commandMoveInvoked = new CountDownLatch(3)
        CountDownLatch commandExecInvoked = new CountDownLatch(3)

        doAnswer(invocation -> {
            commandMoveInvoked.countDown()
            return null
        }).when(otherQueue).addLast(eq(command))
        doAnswer(invocation -> {
            commandExecInvoked.countDown()
            return null
        }).when(command).execute()

        // запускаем в отдельном потоке
        Thread thread = new Thread(() -> basicQueueHandler.handle())
        thread.start()

        // проверяем, что command всё время возвращаются и кладутся в другую очередь
        boolean invoked = commandMoveInvoked.await(2, TimeUnit.SECONDS)
        assertThat(invoked).isEqualTo(true)

        // посылаем run, а затем снова command -> выполнение продолжается в обычном режиме
        when(commandQueue.readFirst()).thenReturn(new RunCommand()).thenReturn(command)
        boolean invokedQueueHandler = commandExecInvoked.await(2, TimeUnit.SECONDS)
        assertThat(invokedQueueHandler).isEqualTo(true)
    }
}
