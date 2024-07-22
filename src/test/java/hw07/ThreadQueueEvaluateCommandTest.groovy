package hw07

import erlikh.yaroslav.hw07.multitrading.command.ThreadQueueEvaluateCommand
import erlikh.yaroslav.hw07.multitrading.handler.queue.BaseQueueHandler
import erlikh.yaroslav.hw07.multitrading.ioc.IoC
import erlikh.yaroslav.hw07.multitrading.ioc.ScopeBaseStrategy
import erlikh.yaroslav.hw4.actions.basecommand.BaseCommand
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference

import static org.assertj.core.api.Assertions.assertThat
import static org.mockito.Mockito.doAnswer
import static org.mockito.Mockito.mock

class ThreadQueueEvaluateCommandTest {

    private BaseQueueHandler queueHandler

    @BeforeEach
    void setUp() {
        ScopeBaseStrategy scopeBasedStrategy = new ScopeBaseStrategy()
        (new ScopeBaseStrategy.InitScopeBaseIoCCommand(scopeBasedStrategy)).execute()

        queueHandler = mock(BaseQueueHandler.class)
        (IoC.resolve("IoC.Register", "Queue.Handler", args -> queueHandler) as BaseCommand).execute()
    }

    @Test
    void 'run_command_evaluate_queue_in_single_thread'() throws InterruptedException {
        CountDownLatch queueHandlerInvoked = new CountDownLatch(1)
        AtomicReference<String> queueHandlerThreadName = new AtomicReference<>()

        doAnswer(invocation -> {
            queueHandlerThreadName.set(Thread.currentThread().getName())
            queueHandlerInvoked.countDown()
            return null
        }).when(queueHandler).handle()

        ThreadQueueEvaluateCommand command = new ThreadQueueEvaluateCommand()
        command.execute()

        boolean invoked = queueHandlerInvoked.await(2, TimeUnit.SECONDS)
        assertThat(invoked).isEqualTo(true)

        assertThat(queueHandlerThreadName.get())
                .isNotNull()
                .isNotEqualTo(Thread.currentThread().getName())
    }
}
