package erlikh.yaroslav.hw12.ioc

import erlikh.yaroslav.hw12.ioc.interfaces.Strategy
import erlikh.yaroslav.hw4.actions.basecommand.BaseCommand

class IoC {

    private static final Strategy defaultStrategy = new DefaultStrategy()
    private static Strategy strategy = defaultStrategy

    private IoC() {
    }

    static <T> T resolve(String key, Object... args) {
        return (T) strategy.resolve(key, args)
    }

    /**
     * Стратегия по умолчанию
     */
    static class DefaultStrategy implements Strategy {

        @Override
        Object resolve(String key, Object... args) {
            if ("IoC.SetupStrategy".equals(key)) {
                return new SetupStrategyCommand((Strategy) args[0])
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

        private final Strategy newStrategy

        SetupStrategyCommand(Strategy newStrategy) {
            this.newStrategy = newStrategy
        }

        @Override
        void execute() {
            IoC.strategy = newStrategy
        }
    }
}
