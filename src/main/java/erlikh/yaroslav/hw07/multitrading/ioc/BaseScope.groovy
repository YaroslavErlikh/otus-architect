package erlikh.yaroslav.hw07.multitrading.ioc

import java.util.function.Function

interface BaseScope {

        Object resolve(String key, Object... args)

        boolean addDependency(String key, Function<Object[], Object> strategy)
}