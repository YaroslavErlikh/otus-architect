package hw08

import erlikh.yaroslav.hw4.actions.basecommand.BaseCommand
import erlikh.yaroslav.hw4.model.basemodel.BaseModel
import erlikh.yaroslav.hw8.command.CommandQueue
import erlikh.yaroslav.hw8.command.InterpretCommand
import erlikh.yaroslav.hw8.handler.queue.QueueHandler
import erlikh.yaroslav.hw8.ioc.IoC
import erlikh.yaroslav.hw8.ioc.ScopeBasedStrategy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import static org.mockito.Mockito.*
import static org.assertj.core.api.Assertions.assertThat
import static org.assertj.core.api.Assertions.assertThatThrownBy

class InterpretCommandTest {

    private String gameId
    private String objectId
    private BaseModel object
    private CommandQueue queue
    private String moveId
    private TestCommand moveCommand
    private String flyId
    private TestCommand flyCommand

    @BeforeEach
    void setUp() {
        ScopeBasedStrategy scopeBasedStrategy = new ScopeBasedStrategy()
        (new ScopeBasedStrategy.InitScopeBasedIoCCommand(scopeBasedStrategy)).execute()

        QueueHandler queueHandler = mock(QueueHandler.class)
        ((BaseCommand) IoC.resolve("IoC.Register", "Queue.Handler", args -> queueHandler)).execute()

        moveId = "Move"
        ((BaseCommand) IoC.resolve("IoC.Register", moveId, args -> {
            moveCommand = spy(new MoveCommand((BaseModel) args[0], (Object[]) args[1]))
            return moveCommand
        })).execute()
        flyId = "Fly"
        ((BaseCommand) IoC.resolve("IoC.Register", flyId, args -> {
            flyCommand = spy(new FlyCommand((BaseModel) args[0], (Object[]) args[1]))
            return flyCommand
        })).execute()

        ((BaseCommand) IoC.resolve("IoC.Register", "Games.CreateQueue", args1 -> queue)).execute()

        // инициализация игры, подменяем очередь
        gameId = "game"
        objectId = "123"
        object = mock(BaseModel.class)
        queue = mock(CommandQueue.class)
        IoC.resolve("Games.Create", gameId)
        IoC.resolve(String.format("Games.%s.Objects.Add", gameId), objectId, object)
        IoC.resolve(String.format("Games.%s.AllowedOperations.Add", gameId), moveId)
    }

    @Test
    void 'good_work_correct_data'() {
        Object[] args = new Object[]{ "123", 1 }
        new InterpretCommand(gameId, objectId, moveId, args).execute()
        verify(queue, times(1)).addLast(refEq(moveCommand))
        assertThat(moveCommand.obj).isSameAs(object)
        assertThat(moveCommand.args).isSameAs(args)
    }

    @Test
    void 'throw_exc_if_object_not_exist'() {
        Object[] args = new Object[]{ "123", 1 }
        assertThatThrownBy(() -> {
            new InterpretCommand(gameId, "567", moveId, args).execute()
        }).isInstanceOf(IllegalArgumentException.class).hasMessage("Object not found")
    }

    @Test
    void 'throw_exc_if_operation_not_allowed'() {
        Object[] args = new Object[]{ "123", 1 }
        assertThatThrownBy(() -> {
            new InterpretCommand(gameId, objectId, flyId, args).execute()
        }).isInstanceOf(IllegalStateException.class).hasMessage("Operation not allowed")
    }

    private abstract static class TestCommand implements BaseCommand {

        protected final BaseModel obj
        final Object[] args

        TestCommand(BaseModel obj, Object[] args) {
            this.obj = obj
            this.args = args
        }

        @Override
        void execute() {

        }
    }

    private static class MoveCommand extends TestCommand {

        MoveCommand(BaseModel obj, Object[] args) {
            super(obj, args)
        }
    }

    private static class FlyCommand extends TestCommand {

        FlyCommand(BaseModel obj, Object[] args) {
            super(obj, args)
        }
    }
}
