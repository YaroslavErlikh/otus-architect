package erlikh.yaroslav.hw5

import java.util.function.Function

interface MyScope {

    Object resolve(String key, Object... args)

    boolean addDependency(String key, Function<Object[], Object> strategy)
}