package erlikh.yaroslav.hw13.ioc.interfaces

interface Strategy {

    Object resolve(String key, Object... args)
}