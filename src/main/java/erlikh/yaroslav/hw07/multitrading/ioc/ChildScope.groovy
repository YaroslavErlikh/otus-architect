package erlikh.yaroslav.hw07.multitrading.ioc

import java.util.function.Function

class ChildScope implements BaseScope {

    private final Map<String, Function<Object[], Object>> dependencies
    private final BaseScope parentScope

    ChildScope(Map<String, Function<Object[], Object>> dependencies, BaseScope parentScope) {
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
