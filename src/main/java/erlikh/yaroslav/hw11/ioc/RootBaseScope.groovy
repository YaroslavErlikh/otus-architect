package erlikh.yaroslav.hw11.ioc

import erlikh.yaroslav.hw11.ioc.interfaces.BaseScope
import erlikh.yaroslav.hw11.ioc.interfaces.BaseStrategy

import java.util.function.Function

class RootBaseScope implements BaseScope {

    private final BaseStrategy strategy

    RootBaseScope(BaseStrategy strategy) {
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
