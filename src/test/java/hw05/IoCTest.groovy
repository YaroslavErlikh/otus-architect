package hw05

import erlikh.yaroslav.hw4.actions.basecommand.BaseCommand
import erlikh.yaroslav.hw5.BasicIoC
import erlikh.yaroslav.hw5.ScopeBasedStrategy
import erlikh.yaroslav.hw5.Strategy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import java.util.function.Function

import static org.assertj.core.api.Assertions.assertThat
import static org.assertj.core.api.Assertions.assertThatThrownBy
import static erlikh.yaroslav.hw5.ScopeBasedStrategy.ROOT_SCOPE_NAME

class IoCTest {

    private BasicIoC basicIoC
    private ScopeBasedStrategy scopeBasedStrategy

    @BeforeEach
    void setUp() {
        basicIoC = new BasicIoC()
        scopeBasedStrategy = new ScopeBasedStrategy(basicIoC)
    }

    @Test
    void 'exception_register_dependency_if_strategy_not_init'() {
        assertThatThrownBy(() -> {
            basicIoC.resolve("IoC.Register", "Movable", (Function<Object[], Object>) (args) -> new Runner((String) args[0]))
        }).isInstanceOf(IllegalArgumentException.class).hasMessage("Unknown key IoC.Register")
    }

    @Test
    void 'set_strategy'() {
        // стратегия, которая независимо от ключа возвращает один и тот же объект
        Object object = new Object()
        Strategy myStrategy = new Strategy() {
            @Override
            Object resolve(String key, Object... args) {
                return object
            }
        }
        ((BaseCommand) basicIoC.resolve("IoC.SetupStrategy", myStrategy)).execute()
        Object test = basicIoC.resolve("TEST")
        assertThat(test).isEqualTo(object)
    }

    @Test
    void 'get_default_root_strategy'() {
        (new ScopeBasedStrategy.InitScopeBasedIoCCommand(scopeBasedStrategy)).execute()

        Strategy defaultStrategy = basicIoC.resolve("IoC.Default")
        assertThat(defaultStrategy).isInstanceOf(BasicIoC.DefaultStrategy.class)
        assertThat(defaultStrategy.resolve("IoC.Default")).isNotNull()
        assertThat(defaultStrategy.resolve("IoC.SetupStrategy", (Strategy) (key, args) -> null)).isInstanceOf(BasicIoC.SetupStrategyCommand.class)
        assertThatThrownBy(() -> {
            basicIoC.resolve("IoC.Resolve", "Test")
        }).isInstanceOf(IllegalArgumentException.class).hasMessage("Unknown key IoC.Resolve")
    }

    @Test
    void 'register_and_resolve_dependency'() {
        (new ScopeBasedStrategy.InitScopeBasedIoCCommand(scopeBasedStrategy)).execute()

        // регистрация зависимостей
        String movableName = "Happy runner"
        BaseCommand registerMovable = basicIoC.resolve("IoC.Register", "Movable", (Function<Object[], Object>) (args) -> new Runner((String) args[0]))
        registerMovable.execute()
        BaseCommand registerSlowMove = basicIoC.resolve("IoC.Register", "Move", (Function<Object[], Object>) (args) -> new MoveCommand(basicIoC.resolve("Movable", movableName)))
        registerSlowMove.execute()

        // разрешение зависимостей
        BaseCommand moveCommand = basicIoC.resolve("Move")
        assertThat(moveCommand).isInstanceOf(MoveCommand.class)
        Movable movable = ((MoveCommand) moveCommand).movable
        assertThat(movable).isInstanceOf(Runner.class)
        assertThat(movable.getName()).isEqualTo(movableName)
    }

    @Test
    void 'overwrite_dependency_with_same_name_in_scope'() {
        (new ScopeBasedStrategy.InitScopeBasedIoCCommand(scopeBasedStrategy)).execute()

        String movableName1 = "Happy runner"
        String movableName2 = "Happy swimmer"

        // регистрация зависимостей
        BaseCommand registerMovable1 = basicIoC.resolve("IoC.Register", "Movable", (Function<Object[], Object>) (args) -> new Runner((String) args[0]))
        registerMovable1.execute()

        // разрешение зависимостей
        Movable movable1 = basicIoC.resolve("Movable", movableName1)
        assertThat(movable1).isInstanceOf(Runner.class)
        assertThat(movable1.getName()).isEqualTo(movableName1)

        // повторная регистрация зависимостей с тем же именем
        BaseCommand registerMovable2 = basicIoC.resolve("IoC.Register", "Movable", (Function<Object[], Object>) (args) -> new Swimmer((String) args[0]))
        registerMovable2.execute()

        // разрешение зависимостей
        Movable movable2 = basicIoC.resolve("Movable", movableName2)
        assertThat(movable2).isInstanceOf(Swimmer.class)
        assertThat(movable2.getName()).isEqualTo(movableName2)
    }

    @Test
    void 'exception_not_find_scope'() {
        (new ScopeBasedStrategy.InitScopeBasedIoCCommand(scopeBasedStrategy)).execute()

        assertThatThrownBy(() -> {
            ((BaseCommand) basicIoC.resolve("Scopes.Current.Set", "scopeId")).execute()
        }).isInstanceOf(IllegalArgumentException.class).hasMessage("Scope scopeId not found")
    }

    @Test
    void 'get_current_scope_create_scope_change_scope'() {
        (new ScopeBasedStrategy.InitScopeBasedIoCCommand(scopeBasedStrategy)).execute()

        // текущий скоуп == рутовый
        String currentScope = basicIoC.resolve("Scopes.Current")
        assertThat(currentScope).isEqualTo(ROOT_SCOPE_NAME)

        // нельзя создать скоуп с несуществующим родительским
        assertThatThrownBy(() -> {
            ((BaseCommand) basicIoC.resolve("Scopes.New", "scopeId", "test")).execute()
        }).isInstanceOf(IllegalArgumentException.class).hasMessage("Parent scope scopeId not found")

        // создание нового скоупа
        String newScopeName = "ABC"
        ((BaseCommand) basicIoC.resolve("Scopes.New", ROOT_SCOPE_NAME, newScopeName)).execute()

        // нельзя создать скоуп с тем же именем
        assertThatThrownBy(() -> {
            ((BaseCommand) basicIoC.resolve("Scopes.New", ROOT_SCOPE_NAME, newScopeName)).execute()
        }).isInstanceOf(IllegalArgumentException.class).hasMessage("Scope ABC already exists")

        // установка нового скоупа
        ((BaseCommand) basicIoC.resolve("Scopes.Current.Set", newScopeName)).execute()
        String currentScope2 = basicIoC.resolve("Scopes.Current")
        assertThat(currentScope2).isEqualTo(newScopeName)

        // переключение обратно
        ((BaseCommand) basicIoC.resolve("Scopes.Current.Set", ROOT_SCOPE_NAME)).execute()
        String currentScope3 = basicIoC.resolve("Scopes.Current")
        assertThat(currentScope3).isEqualTo(ROOT_SCOPE_NAME)
    }

    @Test
    void 'find_dependency_in_parents_scopes'() {
        (new ScopeBasedStrategy.InitScopeBasedIoCCommand(scopeBasedStrategy)).execute()
        String movableName = "Happy runner"

        // создание нового скоупа
        String newScopeName1 = "ABC1"
        ((BaseCommand) basicIoC.resolve("Scopes.New", ROOT_SCOPE_NAME, newScopeName1)).execute()

        // создание дочернего скоупа
        String newScopeName2 = "ABC2"
        ((BaseCommand) basicIoC.resolve("Scopes.New", newScopeName1, newScopeName2)).execute()

        // установка нового скоупа и сохранение зависимости
        ((BaseCommand) basicIoC.resolve("Scopes.Current.Set", newScopeName1)).execute()
        String currentScope1 = basicIoC.resolve("Scopes.Current")
        assertThat(currentScope1).isEqualTo(newScopeName1)
        ((BaseCommand) basicIoC.resolve("IoC.Register", "Movable", (Function<Object[], Object>) (args) -> new Runner((String) args[0]))).execute()

        // переключение на дочерний и поиск зависимости
        ((BaseCommand) basicIoC.resolve("Scopes.Current.Set", newScopeName2)).execute()
        String currentScope2 = basicIoC.resolve("Scopes.Current")
        assertThat(currentScope2).isEqualTo(newScopeName2)
        Runner runner = basicIoC.resolve("Movable", movableName)
        assertThat(runner).isNotNull();
        assertThat(runner.getName()).isEqualTo(movableName)
    }

    @Test
    void 'not_find_dependency_from_another_scope'() {
        (new ScopeBasedStrategy.InitScopeBasedIoCCommand(scopeBasedStrategy)).execute()
        String movableName = "Happy runner"

        // создание нового скоупа
        String newScopeName1 = "ABC1"
        ((BaseCommand) basicIoC.resolve("Scopes.New", ROOT_SCOPE_NAME, newScopeName1)).execute()

        // создание другого скоупа (не дочернего)
        String newScopeName2 = "ABC2"
        ((BaseCommand) basicIoC.resolve("Scopes.New", ROOT_SCOPE_NAME, newScopeName2)).execute()

        // установка нового скоупа и сохранение зависимости
        ((BaseCommand) basicIoC.resolve("Scopes.Current.Set", newScopeName1)).execute()
        String currentScope1 = basicIoC.resolve("Scopes.Current")
        assertThat(currentScope1).isEqualTo(newScopeName1)
        ((BaseCommand) basicIoC.resolve("IoC.Register", "Movable", (Function<Object[], Object>) (args) -> new Runner((String) args[0]))).execute()

        // переключение на несвязанный скуп и поиск зависимости
        ((BaseCommand) basicIoC.resolve("Scopes.Current.Set", newScopeName2)).execute()
        String currentScope2 = basicIoC.resolve("Scopes.Current")
        assertThat(currentScope2).isEqualTo(newScopeName2)
        assertThatThrownBy(() -> {
            basicIoC.resolve("Movable", movableName)
        }).isInstanceOf(IllegalArgumentException.class).hasMessage("Unknown key Movable")
    }

    @Test
    void 'different_scopes_for_each_thread'() throws InterruptedException {
        (new ScopeBasedStrategy.InitScopeBasedIoCCommand(scopeBasedStrategy)).execute()
        String[] movableNames = ["Happy runner", "Happy swimmer"]
        String[] scopeNames = ["Scope1", "Scope2"]

        Function<Integer, Runnable> runnable = i -> {
            return () -> {
                ((BaseCommand) basicIoC.resolve("Scopes.New", ROOT_SCOPE_NAME, scopeNames[i])).execute()

                ((BaseCommand) basicIoC.resolve("Scopes.Current.Set", scopeNames[i])).execute()
                String currentScope = basicIoC.resolve("Scopes.Current")
                assertThat(currentScope).isEqualTo(scopeNames[i])

                ((BaseCommand) basicIoC.resolve("IoC.Register", "Movable", (Function<Object[], Object>) (args) -> {
                    if (i == 0) {
                        return new Runner((String) args[0])
                    } else {
                        return new Swimmer((String) args[0])
                    }
                })).execute()

                Movable movable = basicIoC.resolve("Movable", movableNames[i])
                if (i == 0) {
                    assertThat(movable).isInstanceOf(Runner.class)
                } else {
                    assertThat(movable).isInstanceOf(Swimmer.class)
                }
                assertThat(movable.getName()).isEqualTo(movableNames[i])
            }
        }

        Thread thread = new Thread(runnable.apply(0))
        thread.start()

        runnable.apply(1).run()

        thread.join()

        Movable movable = basicIoC.resolve("Movable", movableNames[1])
        assertThat(movable).isInstanceOf(Swimmer.class)
        assertThat(movable.getName()).isEqualTo(movableNames[1])
    }

    private interface Movable {
        void setPosition(int pos)
        int getPosition()

        String getName()
    }

    private static abstract class AbstractMover implements Movable {
        private final String name
        private int pos

        AbstractMover(String name) {
            this.name = name
        }

        @Override
        void setPosition(int pos) {
            this.pos = pos
        }

        @Override
        int getPosition() {
            return pos
        }

        @Override
        String getName() {
            return name
        }
    }

    private static class Runner extends AbstractMover {

        Runner(String name) {
            super(name)
        }
    }

    private static class Swimmer extends AbstractMover {

        Swimmer(String name) {
            super(name)
        }
    }

    private static class MoveCommand implements BaseCommand {

        private final Movable movable

        MoveCommand(Movable movable) {
            this.movable = movable
        }

        @Override
        void execute() {
            movable.setPosition(movable.getPosition() + 1)
        }
    }
}
