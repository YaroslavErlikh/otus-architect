package erlikh.yaroslav.hw11.ioc.interfaces

import java.util.function.Function

interface BaseScope {

    Object resolve(String key, Object... args)

    boolean addDependency(String key, Function<Object[], Object> strategy)
}