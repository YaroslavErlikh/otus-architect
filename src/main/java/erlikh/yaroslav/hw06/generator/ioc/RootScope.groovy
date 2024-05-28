package erlikh.yaroslav.hw06.generator.ioc

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
