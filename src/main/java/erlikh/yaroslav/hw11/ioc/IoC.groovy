package erlikh.yaroslav.hw11.ioc

import erlikh.yaroslav.hw11.ioc.interfaces.BaseStrategy
import erlikh.yaroslav.hw4.actions.basecommand.BaseCommand

class IoC {

    private static final BaseStrategy defaultStrategy = new DefaultStrategy()
    private static BaseStrategy strategy = defaultStrategy

    private IoC() {
    }

    static <T> T resolve(String key, Object... args) {
        return (T) strategy.resolve(key, args)
    }

    /**
     * Стратегия по умолчанию
     */
    static class DefaultStrategy implements BaseStrategy {

        @Override
        Object resolve(String key, Object... args) {
            if ("IoC.SetupStrategy".equals(key)) {
                return new SetupStrategyCommand((BaseStrategy) args[0])
            } else if ("IoC.Default".equals(key)) {
                return this
            } else {
                throw new IllegalArgumentException(String.format("Unknown key %s", key))
            }
        }
    }

    /**
     * Установка текущей стратегии
     */
    static class SetupStrategyCommand implements BaseCommand {

        private final BaseStrategy newStrategy

        SetupStrategyCommand(BaseStrategy newStrategy) {
            this.newStrategy = newStrategy
        }

        @Override
        void execute() {
            IoC.strategy = newStrategy
        }
    }
}
