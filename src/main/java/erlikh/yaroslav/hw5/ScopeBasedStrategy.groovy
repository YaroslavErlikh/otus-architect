package erlikh.yaroslav.hw5

import erlikh.yaroslav.hw4.actions.basecommand.BaseCommand

import java.util.concurrent.ConcurrentHashMap
import java.util.function.Function

class ScopeBasedStrategy implements Strategy {

    static final String ROOT_SCOPE_NAME = "ROOT"

    private final IoC ioC
    private final ThreadLocal<String> currentScope
    private final ThreadLocal<Map<String, MyScope>> scopes
    private MyScope rootScope = null

    ScopeBasedStrategy(IoC ioC) {
        this.ioC = ioC
        this.currentScope = ThreadLocal.withInitial(() -> ROOT_SCOPE_NAME)
        this.scopes = ThreadLocal.withInitial(HashMap::new)
    }

    @Override
    Object resolve(String key, Object... args) {
        if ("Scopes.Root".equals(key)) {
            return rootScope
        } else {
            MyScope scope = getCurrentOrRootScope()
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

    private MyScope getCurrentOrRootScope() {
        return getScope(getCurrentOrRootScopeName())
    }

    private MyScope getScope(String scope) {
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

            MyScope scope = new ChildScope(
                    dependencies,
                    new RootScope(ioC.resolve("IoC.Default"))
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
                    MyScope parentScope
                    if (parentScopeName == null || (parentScope = getScope(parentScopeName)) == null) {
                        throw new IllegalArgumentException(String.format("Parent scope %s not found", parentScopeName))
                    }
                    Map<String, Function<Object[], Object>> storage = ioC.resolve("Scopes.Storage")
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
                    MyScope currentScope = getCurrentOrRootScope();
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

            rootScope = scope

            ((BaseCommand) ioC.resolve("IoC.SetupStrategy", ScopeBasedStrategy.this)).execute()

            ScopeBasedStrategy.this.currentScope.set(ROOT_SCOPE_NAME)
        }
    }
}
