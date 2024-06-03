package erlikh.yaroslav.hw07.multitrading.ioc

interface Strategy {

    Object resolve(String key, Object... args)
}