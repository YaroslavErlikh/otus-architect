package erlikh.yaroslav.hw8.ioc

import erlikh.yaroslav.hw4.actions.basecommand.BaseCommand
import erlikh.yaroslav.hw4.model.basemodel.BaseModel
import erlikh.yaroslav.hw8.command.CommandConcurrentQueue
import erlikh.yaroslav.hw8.command.CommandQueue
import erlikh.yaroslav.hw8.command.GameCommand
import erlikh.yaroslav.hw8.command.InterpretCommand

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

    /**
     * Текущий скоуп (соответствующий потоку)
     * Если текущий не установлен, то возвращается рутовый скуп (из любого скупа)
     */
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

    /**
     * Команда для инициализации зависимостей данной стратегии
     */
    class InitScopeBasedIoCCommand implements BaseCommand {

        @Override
        synchronized void execute() {
            if (rootScope != null) {
                return
            }

            Map<String, Function<Object[], Object>> dependencies = new ConcurrentHashMap<>()

            // базовый скуп
            Scope scope = new ChildScope(
                    dependencies,
                    new RootScope(IoC.resolve("IoC.Default")) // последний скуп с дефолтной стратегией
            );

            // зависимость: создание хранилища зависимостей для скупа
            dependencies.put("Scopes.Storage", args -> {
                return new ConcurrentHashMap<String, Function<Object[], Object>>()
            })

            // зависимость: создание нового скупа
            dependencies.put("Scopes.New", args -> {
                return () -> {
                    // проверяем имя скупа
                    String newScopeName = (String) args[1]
                    if (newScopeName == null) {
                        throw new IllegalArgumentException("Not valid scope name")
                    }
                    if (scopes.get().containsKey(newScopeName)) {
                        throw new IllegalArgumentException(String.format("Scope %s already exists", newScopeName))
                    }
                    // создаём скуп с родительским из параметров
                    String parentScopeName = (String) args[0]
                    Scope parentScope
                    if (parentScopeName == null || (parentScope = getScope(parentScopeName)) == null) {
                        throw new IllegalArgumentException(String.format("Parent scope %s not found", parentScopeName))
                    }
                    // получаем хранилище зависимостей
                    Map<String, Function<Object[], Object>> storage = IoC.resolve("Scopes.Storage")
                    // создаём скуп, помещаем в хранилище скупов
                    ChildScope childScope = new ChildScope(storage, parentScope)
                    scopes.get().put((String) args[1], childScope)
                }
            })

            // зависимость: получение текущего скупа (или дефолтного)
            dependencies.put("Scopes.Current", args -> {
                return getCurrentOrRootScopeName()
            });

            // зависимость: установка текущего скупа
            dependencies.put("Scopes.Current.Set", args -> {
                return () -> {
                    String scopeName = (String) args[0]
                    if (getScope(scopeName) == null) {
                        throw new IllegalArgumentException(String.format("Scope %s not found", scopeName))
                    }
                    ScopeBasedStrategy.this.currentScope.set(scopeName)
                }
            })

            // зависимость: регистрация зависимостей
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

            // зависимость: работа с играми; игры хранятся в корневом скупе, тогда доступ будет из всех потоков
            Map<String, GameCommand> games = new ConcurrentHashMap<>()

            // получить игру по id
            dependencies.put("Games.GetById", args -> games.get((String) args[0]))

            // создать очередь игры
            dependencies.put("Games.CreateQueue", args -> new CommandConcurrentQueue())

            // зависимость: создание игры с очередью и объектами
            dependencies.put("Games.Create", args -> {
                String gameId = (String) args[0]
                return games.compute(gameId, (id, oldGame) -> {
                    if (oldGame != null) {
                        throw new IllegalArgumentException("Game already exists")
                    }
                    CommandQueue commandQueue = IoC.resolve("Games.CreateQueue", gameId)
                    ((BaseCommand) IoC.resolve("IoC.Register", String.format("Games.%s.CommandQueue", id), args1 -> commandQueue)).execute()
                    Map<String, BaseModel> objects = new ConcurrentHashMap<>()
                    ((BaseCommand) IoC.resolve("IoC.Register", String.format("Games.%s.Objects.Add", id), args1 -> objects.put((String) args1[0], (BaseModel) args1[1]))).execute()
                    ((BaseCommand) IoC.resolve("IoC.Register", String.format("Games.%s.Objects.Get", id), args1 -> objects.get((String) args1[0]))).execute()
                    Map<String, Boolean> allowedOperations = new ConcurrentHashMap<>();
                    ((BaseCommand) IoC.resolve("IoC.Register", String.format("Games.%s.AllowedOperations.Add", id), args1 -> allowedOperations.put((String) args1[0], true))).execute()
                    ((BaseCommand) IoC.resolve("IoC.Register", String.format("Games.%s.AllowedOperations.Remove", id), args1 -> allowedOperations.remove((String) args1[0]))).execute()
                    ((BaseCommand) IoC.resolve("IoC.Register", String.format("Games.%s.AllowedOperations.Get", id), args1 -> allowedOperations.getOrDefault((String) args1[0], false))).execute()
                    return new GameCommand(gameId)
                })
            })

            // зависимость: команда интерпретатор
            dependencies.put("InterpretCommand", args -> new InterpretCommand((String) args[0], (String) args[1], (String) args[2], (Object[]) args[3]))

            // устанавливаем рутовый скуп
            rootScope = scope

            // устанавливаем стратегию
            ((BaseCommand) IoC.resolve("IoC.SetupStrategy", ScopeBasedStrategy.this)).execute()

            // устанавливаем текущий скуп как рутовый
            ScopeBasedStrategy.this.currentScope.set(ROOT_SCOPE_NAME)
        }
    }
}