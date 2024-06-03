package erlikh.yaroslav.hw06.generator.ioc

import java.util.function.Function

interface Scope {

    /**
     * Получение зависимости
     */
    Object resolve(String key, Object... args)

    /**
     * Добавление зависимости
     */
    boolean addDependency(String key, Function<Object[], Object> strategy)
}