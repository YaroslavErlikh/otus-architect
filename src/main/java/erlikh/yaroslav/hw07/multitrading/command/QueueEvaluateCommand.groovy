package erlikh.yaroslav.hw07.multitrading.command

import erlikh.yaroslav.hw07.multitrading.handler.queue.BaseQueueHandler
import erlikh.yaroslav.hw07.multitrading.ioc.IoC
import erlikh.yaroslav.hw4.actions.basecommand.BaseCommand

class QueueEvaluateCommand implements BaseCommand {

    @Override
    void execute() {
        ((BaseQueueHandler) IoC.resolve("Queue.Handler")).handle()
    }
}
