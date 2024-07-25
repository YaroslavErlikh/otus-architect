package erlikh.yaroslav.hw11.ioc

import erlikh.yaroslav.hw11.ioc.interfaces.BaseScope

import java.util.function.Function

class ChildBaseScope implements BaseScope {

    private final Map<String, Function<Object[], Object>> dependencies
    private final BaseScope parentScope

    ChildBaseScope(Map<String, Function<Object[], Object>> dependencies, BaseScope parentScope) {
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
