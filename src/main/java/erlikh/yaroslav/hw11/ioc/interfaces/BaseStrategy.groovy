package erlikh.yaroslav.hw11.ioc.interfaces

interface BaseStrategy {

    Object resolve(String key, Object... args)
}