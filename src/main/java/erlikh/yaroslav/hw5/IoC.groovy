package erlikh.yaroslav.hw5

interface IoC {

    <T> T resolve(String key, Object... args)
}
