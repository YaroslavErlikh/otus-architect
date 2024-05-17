package erlikh.yaroslav.hw5

import java.util.function.Function

class RootScope implements MyScope {

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
