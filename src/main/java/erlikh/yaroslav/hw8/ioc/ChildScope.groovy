package erlikh.yaroslav.hw8.ioc

import java.util.function.Function

class ChildScope implements Scope {

    private final Map<String, Function<Object[], Object>> dependencies
    private final Scope parentScope

    ChildScope(Map<String, Function<Object[], Object>> dependencies, Scope parentScope) {
        this.dependencies = dependencies
        this.parentScope = parentScope
    }

    @Override
    Object resolve(String key, Object... args) {
        Function<Object[], Object> func = dependencies.get(key)
        if (func != null) {
            return func.apply(args)
        }
        return parentScope.resolve(key, args)
    }

    @Override
    boolean addDependency(String key, Function<Object[], Object> strategy) {
        dependencies.put(key, strategy)
        return true
    }
}