package erlikh.yaroslav.hw8.ioc

interface Strategy {

    Object resolve(String key, Object... args)
}