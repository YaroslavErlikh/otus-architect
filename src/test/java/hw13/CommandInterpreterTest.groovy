package hw13

import erlikh.yaroslav.hw13.command.FinishMoveCommand
import erlikh.yaroslav.hw13.command.ShootCommand
import erlikh.yaroslav.hw13.command.StartMoveCommand
import erlikh.yaroslav.hw13.exception.InterpretException
import erlikh.yaroslav.hw13.interpreter.CommandInterpreter
import erlikh.yaroslav.hw13.ioc.IoC
import erlikh.yaroslav.hw13.ioc.ScopeBasedStrategy
import erlikh.yaroslav.hw4.actions.basecommand.BaseCommand
import erlikh.yaroslav.hw4.model.basemodel.BaseModel
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicReference

import static org.assertj.core.api.Assertions.assertThat
import static org.assertj.core.api.Assertions.assertThatThrownBy
import static org.mockito.Mockito.*

class CommandInterpreterTest {

    private final String gameId1 = "game1"
    private final String beginMoveKey = "beginMove"
    private final String endMoveKey = "endMove"
    private final String fireKey = "fire"
    private final String otherKey = "other"

    @BeforeEach
    void setUp() {
        ScopeBasedStrategy scopeBasedStrategy = new ScopeBasedStrategy()
        (new ScopeBasedStrategy.InitScopeBasedIoCCommand(scopeBasedStrategy)).execute()

        IoC.resolve("Games.Create", gameId1)

        IoC.resolve(String.format("Games.%s.Actions.Types.Add", gameId1), beginMoveKey, StartMoveCommand.class)
        IoC.resolve(String.format("Games.%s.Actions.Types.Add", gameId1), endMoveKey, FinishMoveCommand.class)
        IoC.resolve(String.format("Games.%s.Actions.Types.Add", gameId1), fireKey, ShootCommand.class)
        IoC.resolve(String.format("Games.%s.Actions.Types.Add", gameId1), otherKey, OtherCommand.class)

        IoC.resolve(String.format("Games.%s.Actions.Commands.Add", gameId1), beginMoveKey, args -> new StartMoveCommand((BaseModel) args[0], (Double) args[1]))
        IoC.resolve(String.format("Games.%s.Actions.Commands.Add", gameId1), endMoveKey, args -> new FinishMoveCommand((BaseModel) args[0]))
        IoC.resolve(String.format("Games.%s.Actions.Commands.Add", gameId1), fireKey, args -> new ShootCommand((BaseModel) args[0], (Integer) args[1]))
        IoC.resolve(String.format("Games.%s.Actions.Commands.Add", gameId1), otherKey, args -> new OtherCommand((Integer) args[0], (String) args[1]))
    }

    @Test
    void 'success_process_order_for_object'() throws InterruptedException {
        String username = "User1"
        String objectId1 = "object1"
        BaseModel object1 = mock(BaseModel.class)
        Map<String, Map<String, BaseModel>> objectsInGames = Map.of(gameId1, Map.of(objectId1, object1))

        BaseModel order = mock(BaseModel.class)
        double velocity = 1234.56
        when(order.getAttribute(eq("action"))).thenReturn(beginMoveKey)
        when(order.getAttribute(eq("id"))).thenReturn(objectId1)
        when(order.getAttribute(eq("initialVelocity"))).thenReturn(velocity)

        AtomicReference<BaseCommand> command = new AtomicReference<>()

        Thread thread = new Thread(() -> {
            initUserScope(username, objectsInGames)
            command.set(new CommandInterpreter(gameId1).interpret(order))
        })
        thread.start()
        thread.join()

        assertThat(command.get()).isNotNull().isInstanceOf(StartMoveCommand.class)
        command.get().execute()
        verify(object1, times(1)).setAttribute(eq("initialVelocity"), argThat(value -> Math.abs(velocity - (double) value) < 1e-3))
    }

    @Test
    void 'success_process_any_order'() throws InterruptedException {
        String username = "user1"

        BaseModel order = mock(BaseModel.class)
        Integer param1 = 123
        String param2 = "abc"
        when(order.getAttribute(eq("action"))).thenReturn(otherKey)
        when(order.getAttribute(eq("param1"))).thenReturn(param1)
        when(order.getAttribute(eq("param2"))).thenReturn(param2)

        AtomicReference<BaseCommand> command = new AtomicReference<>()

        Thread thread = new Thread(() -> {
            initUserScope(username, Map.of())
            command.set(new CommandInterpreter(gameId1).interpret(order))
        })
        thread.start()
        thread.join()

        assertThat(command.get()).isNotNull().isInstanceOf(OtherCommand.class)
        OtherCommand otherCommand = (OtherCommand) command.get()
        assertThat(otherCommand.param1).isEqualTo(param1)
        assertThat(otherCommand.param2).isEqualTo(param2)
    }

    @Test
    void 'process_user_order_to_self_objects'() throws InterruptedException {
        String username1 = "User1"
        String objectId1 = "object1"
        BaseModel object1 = mock(BaseModel.class)
        Map<String, Map<String, BaseModel>> objectsInGames1 = Map.of(gameId1, Map.of(objectId1, object1))
        String username2 = "User2"
        String objectId2 = "object2"
        BaseModel object2 = mock(BaseModel.class)
        Map<String, Map<String, BaseModel>> objectsInGames2 = Map.of(gameId1, Map.of(objectId2, object2))

        BaseModel order1 = mock(BaseModel.class)
        when(order1.getAttribute(eq("action"))).thenReturn(endMoveKey)
        when(order1.getAttribute(eq("id"))).thenReturn(objectId1)

        BaseModel order2 = mock(BaseModel.class)
        int fireDirection = 123
        when(order2.getAttribute(eq("action"))).thenReturn(fireKey)
        when(order2.getAttribute(eq("id"))).thenReturn(objectId2)
        when(order2.getAttribute(eq("fireDirection"))).thenReturn(fireDirection)

        AtomicReference<BaseCommand> command1 = new AtomicReference<>()
        AtomicReference<BaseCommand> command2 = new AtomicReference<>()

        Thread thread1 = new Thread(() -> {
            initUserScope(username1, objectsInGames1)
            command1.set(new CommandInterpreter(gameId1).interpret(order1))
        })
        thread1.start()

        Thread thread2 = new Thread(() -> {
            initUserScope(username2, objectsInGames2)
            command2.set(new CommandInterpreter(gameId1).interpret(order2))
        })
        thread2.start()

        thread1.join()
        thread2.join()

        assertThat(command1.get()).isNotNull().isInstanceOf(FinishMoveCommand.class)
        command1.get().execute()
        verify(object1, times(1)).setAttribute(eq("velocity"), eq(0))

        assertThat(command2.get()).isNotNull().isInstanceOf(ShootCommand.class)
        command2.get().execute()
        verify(object2, times(1)).setAttribute(eq("fireDirection"), eq(fireDirection))
    }

    @Test
    void 'not_process_order_another_objects'() throws InterruptedException {
        String username1 = "User1"
        String objectId1 = "object1"
        BaseModel object1 = mock(BaseModel.class)
        Map<String, Map<String, BaseModel>> objectsInGames1 = Map.of(gameId1, Map.of(objectId1, object1))
        String username2 = "User2"
        String objectId2 = "object2"
        BaseModel object2 = mock(BaseModel.class)
        Map<String, Map<String, BaseModel>> objectsInGames2 = Map.of(gameId1, Map.of(objectId2, object2))

        BaseModel order1 = mock(BaseModel.class)
        when(order1.getAttribute(eq("action"))).thenReturn(endMoveKey)
        when(order1.getAttribute(eq("id"))).thenReturn(objectId2)

        Thread thread2 = new Thread(() -> {
            initUserScope(username2, objectsInGames2)
        })
        thread2.start()
        thread2.join()

        AtomicReference<Exception> exception = new AtomicReference<>()
        Thread thread1 = new Thread(() -> {
            initUserScope(username1, objectsInGames1)
            try {
                new CommandInterpreter(gameId1).interpret(order1)
            } catch (Exception ex) {
                exception.set(ex)
            }
        })
        thread1.start()
        thread1.join()

        assertThat(exception.get()).isNotNull().isInstanceOf(InterpretException.class).hasMessage("Object not found")
    }

    @Test
    void 'thow_exc_for_wrong_order'() {
        BaseModel order = mock(BaseModel.class);

        when(order.getAttribute(eq("action"))).thenReturn("abc")
        assertThatThrownBy(() -> {
            new CommandInterpreter(gameId1).interpret(order)
        }).isInstanceOf(InterpretException.class).hasMessage("Action not found")

        IoC.resolve(String.format("Games.%s.Actions.Types.Add", gameId1), "abc", FinishMoveCommand.class)
        assertThatThrownBy(() -> {
            new CommandInterpreter(gameId1).interpret(order)
        }).isInstanceOf(InterpretException.class).hasMessage("Command not found")

    }

    private void initUserScope(String username, Map<String, Map<String, BaseModel>> objectsInGames) {
        String scopeName = String.format("User.%s", username)
        ((BaseCommand) IoC.resolve("Scopes.New", IoC.resolve("Scopes.Current"), scopeName)).execute()
        ((BaseCommand) IoC.resolve("Scopes.Current.Set", scopeName)).execute()
        Map<String, BaseModel> objects = new ConcurrentHashMap<>()
        objectsInGames.forEach((gameId, gameObjects) -> {
            ((BaseCommand) IoC.resolve("IoC.Register", String.format("Games.%s.Objects.Add", gameId), args1 -> objects.put((String) args1[0], (BaseModel) args1[1]))).execute()
            ((BaseCommand) IoC.resolve("IoC.Register", String.format("Games.%s.Objects.Get", gameId), args1 -> objects.get((String) args1[0]))).execute()
            gameObjects.forEach((objectId, object) -> {
                IoC.resolve(String.format("Games.%s.Objects.Add", gameId), objectId, object)
                IoC.resolve(String.format("Games.%s.Objects.Add", gameId), objectId, object)
            })
        })
    }

    private static class OtherCommand implements BaseCommand {
        Integer param1
        String param2

        OtherCommand(Integer param1, String param2) {
            this.param1 = param1
            this.param2 = param2
        }

        @Override
        void execute() {

        }
    }
}
