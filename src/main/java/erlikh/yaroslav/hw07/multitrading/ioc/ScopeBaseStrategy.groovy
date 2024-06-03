package erlikh.yaroslav.hw07.multitrading.ioc

import erlikh.yaroslav.hw4.actions.basecommand.BaseCommand

import java.util.concurrent.ConcurrentHashMap
import java.util.function.Function

class ScopeBaseStrategy implements Strategy {

    static final String ROOT_SCOPE_NAME = "ROOT"

    private final ThreadLocal<String> currentScope
    private final ThreadLocal<Map<String, BaseScope>> scopes
    private BaseScope rootScope = null

    ScopeBaseStrategy() {
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

    class InitScopeBaseIoCCommand implements BaseCommand {

        @Override
        synchronized void execute() {
            if (rootScope != null) {
                return
            }

            Map<String, Function<Object[], Object>> dependencies = new ConcurrentHashMap<>()

            BaseScope scope = new ChildScope(
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
                    BaseScope parentScope
                    if (parentScopeName == null || (parentScope = getScope(parentScopeName)) == null) {
                        throw new IllegalArgumentException(String.format("Parent scope %s not found", parentScopeName))
                    }
                    Map<String, Function<Object[], Object>> storage = IoC.resolve("Scopes.Storage")
                    ChildScope childScope = new ChildScope(storage, parentScope)
                    scopes.get().put((String) args[1], childScope)
                } as BaseCommand
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
                    ScopeBaseStrategy.this.currentScope.set(scopeName)
                } as BaseCommand
            })

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
                } as BaseCommand
            })

            rootScope = scope

            (IoC.resolve("IoC.SetupStrategy", ScopeBaseStrategy.this) as BaseCommand).execute()

            ScopeBaseStrategy.this.currentScope.set(ROOT_SCOPE_NAME)
        }
    }
}