package erlikh.yaroslav.hw13.ioc

import erlikh.yaroslav.hw13.ioc.interfaces.Scope
import erlikh.yaroslav.hw13.ioc.interfaces.Strategy

import java.util.function.Function

class RootScope implements Scope {

    private final Strategy strategy

    RootScope(Strategy strategy) {
        this.strategy = strategy
    }

    @Override
    Object resolve(String key, Object... args) {
        return strategy.resolve(key, args)
    }

    @Override
    boolean addDependency(String key, Function<Object[], Object> strategy) {
        return false
    }
}
