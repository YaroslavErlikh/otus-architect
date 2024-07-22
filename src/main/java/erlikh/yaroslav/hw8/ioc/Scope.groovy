package erlikh.yaroslav.hw8.ioc

import java.util.function.Function

interface Scope {

    Object resolve(String key, Object... args)

    boolean addDependency(String key, Function<Object[], Object> strategy)
}