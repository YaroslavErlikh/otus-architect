package erlikh.yaroslav.hw11.ioc

import erlikh.yaroslav.hw11.ioc.interfaces.BaseScope
import erlikh.yaroslav.hw11.ioc.interfaces.BaseStrategy
import erlikh.yaroslav.hw4.actions.basecommand.BaseCommand

import java.util.concurrent.ConcurrentHashMap
import java.util.function.Function

class ScopeBasedStrategy implements BaseStrategy {

    static final String ROOT_SCOPE_NAME = "ROOT"

    private final ThreadLocal<String> currentScope
    private final ThreadLocal<Map<String, BaseScope>> scopes
    private BaseScope rootScope = null

    ScopeBasedStrategy() {
        this.currentScope = ThreadLocal.withInitial(() -> ROOT_SCOPE_NAME)
        this.scopes = ThreadLocal.withInitial(HashMap::new)
    }

    @Override
    Object resolve(String key, Object... args) {
        if ("Scopes.Root".equals(key)) {
            return rootScope
        } else {
            BaseScope scope = getCurrentOrRootScope()
            if (scope == null) {
                throw new IllegalStateException("Scope not found")
            }
            return scope.resolve(key, args)
        }
    }

    /**
     * Текущий скоуп (соответствующий потоку)
     * Если текущий не установлен, то возвращается рутовый скоуп (из любого скоупа)
     */
    private String getCurrentOrRootScopeName() {
        String scope = currentScope.get()
        if (scope == null) {
            scope = ROOT_SCOPE_NAME
        }
        return scope
    }

    private BaseScope getCurrentOrRootScope() {
        return getScope(getCurrentOrRootScopeName())
    }

    private BaseScope getScope(String scope) {
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

            // базовый скоуп
            BaseScope scope = new ChildBaseScope(
                    dependencies,
                    new RootBaseScope(IoC.resolve("IoC.Default")) // крайний скоуп с дефолтной стратегией
            )

            // зависимость: создание хранилища зависимостей для скоупа
            dependencies.put("Scopes.Storage", args -> {
                return new ConcurrentHashMap<String, Function<Object[], Object>>()
            })

            // зависимость: создание нового скоупа
            dependencies.put("Scopes.New", args -> {
                return () -> {
                    // проверяем имя скоупа
                    String newScopeName = (String) args[1]
                    if (newScopeName == null) {
                        throw new IllegalArgumentException("Not valid scope name")
                    }
                    if (scopes.get().containsKey(newScopeName)) {
                        throw new IllegalArgumentException(String.format("Scope %s already exists", newScopeName))
                    }
                    // создаём скоуп с родительским из параметров
                    String parentScopeName = (String) args[0]
                    BaseScope parentScope
                    if (parentScopeName == null || (parentScope = getScope(parentScopeName)) == null) {
                        throw new IllegalArgumentException(String.format("Parent scope %s not found", parentScopeName))
                    }
                    // получаем хранилище зависимостей
                    Map<String, Function<Object[], Object>> storage = IoC.resolve("Scopes.Storage")
                    // создаём скоуп, помещаем в хранилище скоупов
                    ChildBaseScope childScope = new ChildBaseScope(storage, parentScope)
                    scopes.get().put((String) args[1], childScope)
                }
            })

            // зависимость: получение текущего скоупа (или дефолтного, т.е. корневого)
            dependencies.put("Scopes.Current", args -> {
                return getCurrentOrRootScopeName()
            })

            // зависимость: установка текущего скоупа
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
                    BaseScope currentScope = getCurrentOrRootScope()
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

            // устанавливаем рутовый скоуп
            rootScope = scope

            // устанавливаем стратегию
            ((BaseCommand) IoC.resolve("IoC.SetupStrategy", ScopeBasedStrategy.this)).execute()

            // устанавливаем текущий скоуп как рутовый
            ScopeBasedStrategy.this.currentScope.set(ROOT_SCOPE_NAME)
        }
    }
}
