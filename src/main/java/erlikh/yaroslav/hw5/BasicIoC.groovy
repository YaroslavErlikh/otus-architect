package erlikh.yaroslav.hw5

import erlikh.yaroslav.hw4.actions.basecommand.BaseCommand

@SuppressWarnings("unchecked")
class BasicIoC implements IoC {

    private final Strategy defaultStrategy = new DefaultStrategy()
    private Strategy strategy

    BasicIoC() {
        this.strategy = defaultStrategy
    }

    @Override
    <T> T resolve(String key, Object... args) {
        return (T) strategy.resolve(key, args)
    }

    class DefaultStrategy implements Strategy {

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

    class SetupStrategyCommand implements BaseCommand {

        private final Strategy newStrategy

        SetupStrategyCommand(Strategy newStrategy) {
            this.newStrategy = newStrategy
        }

        @Override
        void execute() {
            BasicIoC.this.strategy = newStrategy
        }
    }
}
