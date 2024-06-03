package erlikh.yaroslav.hw06.generator.ioc

interface Strategy {

    Object resolve(String key, Object... args)
}