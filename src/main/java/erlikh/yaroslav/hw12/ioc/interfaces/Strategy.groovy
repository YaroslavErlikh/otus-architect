package erlikh.yaroslav.hw12.ioc.interfaces

interface Strategy {

    Object resolve(String key, Object... args)
}