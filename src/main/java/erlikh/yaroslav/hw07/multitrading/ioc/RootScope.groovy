package erlikh.yaroslav.hw07.multitrading.ioc

import java.util.function.Function

class RootScope implements BaseScope {

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