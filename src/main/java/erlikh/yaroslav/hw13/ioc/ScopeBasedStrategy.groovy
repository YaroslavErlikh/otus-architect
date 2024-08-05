package erlikh.yaroslav.hw13.ioc

import erlikh.yaroslav.hw13.command.InterpretCommand
import erlikh.yaroslav.hw13.interpreter.CommandInterpreter
import erlikh.yaroslav.hw13.ioc.interfaces.Scope
import erlikh.yaroslav.hw13.ioc.interfaces.Strategy
import erlikh.yaroslav.hw13.model.Game
import erlikh.yaroslav.hw4.actions.basecommand.BaseCommand
import erlikh.yaroslav.hw4.model.basemodel.BaseModel

import java.util.concurrent.ConcurrentHashMap
import java.util.function.Function

class ScopeBasedStrategy implements Strategy {

    static final String ROOT_SCOPE_NAME = "ROOT"

    private final ThreadLocal<String> currentScope
    private final ThreadLocal<Map<String, Scope>> scopes
    private Scope rootScope = null

    ScopeBasedStrategy() {
        this.currentScope = ThreadLocal.withInitial(() -> ROOT_SCOPE_NAME)
        this.scopes = ThreadLocal.withInitial(HashMap::new)
    }

    @Override
    Object resolve(String key, Object... args) {
        if ("Scopes.Root".equals(key)) {
            return rootScope
        } else {
            Scope scope = getCurrentOrRootScope()
            if (scope == null) {
                throw new IllegalStateException("Scope not found")
            }
            return scope.resolve(key, args)
        }
    }

    private String getCurrentOrRootScopeName() {
        String scope = currentScope.get()
        if (scope == null) {
            scope = ROOT_SCOPE_NAME
        }
        return scope
    }

    private Scope getCurrentOrRootScope() {
        return getScope(getCurrentOrRootScopeName())
    }

    private Scope getScope(String scope) {
        if (ROOT_SCOPE_NAME.equals(scope)) {
            return rootScope
        }
        return scopes.get().get(scope)
    }

    class InitScopeBasedIoCCommand implements BaseCommand {

        @Override
        synchronized void execute() {
            if (rootScope != null) {
                return
            }

            Map<String, Function<Object[], Object>> dependencies = new ConcurrentHashMap<>()

            Scope scope = new ChildScope(
                    dependencies,
                    new RootScope(IoC.resolve("IoC.Default"))
            )

            dependencies.put("Scopes.Storage", args -> {
                return new ConcurrentHashMap<String, Function<Object[], Object>>()
            })

            dependencies.put("Scopes.New", args -> {
                return () -> {
                    String newScopeName = (String) args[1]
                    if (newScopeName == null) {
                        throw new IllegalArgumentException("Not valid scope name")
                    }
                    if (scopes.get().containsKey(newScopeName)) {
                        throw new IllegalArgumentException(String.format("Scope %s already exists", newScopeName))
                    }
                    String parentScopeName = (String) args[0]
                    Scope parentScope
                    if (parentScopeName == null || (parentScope = getScope(parentScopeName)) == null) {
                        throw new IllegalArgumentException(String.format("Parent scope %s not found", parentScopeName))
                    }
                    Map<String, Function<Object[], Object>> storage = IoC.resolve("Scopes.Storage")
                    ChildScope childScope = new ChildScope(storage, parentScope)
                    scopes.get().put((String) args[1], childScope)
                }
            })

            dependencies.put("Scopes.Current", args -> {
                return getCurrentOrRootScopeName()
            })

            dependencies.put("Scopes.Current.Set", args -> {
                return () -> {
                    String scopeName = (String) args[0]
                    if (getScope(scopeName) == null) {
                        throw new IllegalArgumentException(String.format("Scope %s not found", scopeName))
                    }
                    ScopeBasedStrategy.this.currentScope.set(scopeName)
                }
            })

            dependencies.put("IoC.Register", args -> {
                return () -> {
                    Scope currentScope = getCurrentOrRootScope()
                    boolean success
                    if (currentScope == null) {
                        success = false
                    } else {
                        success = currentScope.addDependency((String) args[0], (Function<Object[], Object>) args[1])
                    }
                    if (!success) {
                        throw new IllegalArgumentException("Can not register dependency")
                    }
                }
            })

            Map<String, Game> games = new ConcurrentHashMap<>()

            dependencies.put("Games.Create", args -> {
                String gameId = (String) args[0]
                return games.compute(gameId, (id, oldGame) -> {
                    if (oldGame != null) {
                        throw new IllegalArgumentException("Game already exists")
                    }
                    Map<String, Class<BaseCommand>> actionTypes = new ConcurrentHashMap<>()
                    ((BaseCommand) IoC.resolve("IoC.Register", String.format("Games.%s.Actions.Types.Add", id), args1 -> actionTypes.put((String) args1[0], (Class<BaseCommand>) args1[1]))).execute()
                    ((BaseCommand) IoC.resolve("IoC.Register", String.format("Games.%s.Actions.Types.Get", id), args1 -> actionTypes.get((String) args1[0]))).execute()
                    Map<String, Function<Object[], BaseCommand>> actions = new ConcurrentHashMap<>()
                    ((BaseCommand) IoC.resolve("IoC.Register", String.format("Games.%s.Actions.Commands.Add", id), args1 -> actions.put((String) args1[0], (Function<Object[], BaseCommand>) args1[1]))).execute()
                    ((BaseCommand) IoC.resolve("IoC.Register", String.format("Games.%s.Actions.Commands.Get", id), args1 -> {
                        Function<Object[], BaseCommand> commandFunction = actions.get((String) args1[0])
                        if (commandFunction == null) {
                            return null
                        }
                        return commandFunction.apply((Object[]) args1[1])
                    })).execute()
                    return new Game(gameId)
                })
            })

            dependencies.put("InterpretCommand", args -> new InterpretCommand((String) args[0], (BaseModel) args[1]))

            // зависимость: выполнение интерпретации
            dependencies.put("Interpreter.Command.Execute", args -> new CommandInterpreter((String) args[0]))

            rootScope = scope

            ((BaseCommand) IoC.resolve("IoC.SetupStrategy", ScopeBasedStrategy.this)).execute()

            ScopeBasedStrategy.this.currentScope.set(ROOT_SCOPE_NAME)
        }
    }
}