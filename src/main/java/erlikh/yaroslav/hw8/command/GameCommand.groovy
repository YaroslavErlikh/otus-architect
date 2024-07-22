package erlikh.yaroslav.hw8.command

import erlikh.yaroslav.hw4.actions.basecommand.BaseCommand
import erlikh.yaroslav.hw8.ioc.IoC

class GameCommand implements BaseCommand {

    private final String id

    GameCommand(String id) {
        this.id = id
    }

    @Override
    void execute() {
        new QueueProcessCommand(queue()).execute()
    }

    private CommandQueue queue() {
        return IoC.resolve(String.format("Games.%s.CommandQueue", id))
    }

    /**
     * Команда по добавлению команды в очередь игры
     */
    class AddToGameQueueCommand implements BaseCommand {

        private final BaseCommand command

        AddToGameQueueCommand(BaseCommand command) {
            this.command = command
        }

        @Override
        void execute() {
            GameCommand.this.queue().addLast(command)
        }
    }
}
