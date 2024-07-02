package erlikh.yaroslav.hw8.command

import erlikh.yaroslav.hw4.actions.basecommand.BaseCommand
import erlikh.yaroslav.hw8.handler.queue.QueueHandler
import erlikh.yaroslav.hw8.ioc.IoC

class QueueProcessCommand implements BaseCommand {

    private final CommandQueue queue

    QueueProcessCommand(CommandQueue queue) {
        this.queue = queue
    }

    @Override
    void execute() {
        ((QueueHandler) IoC.resolve("Queue.Handler")).handle(queue)
    }
}
