package erlikh.yaroslav.hw07.multitrading.handler.queue

import erlikh.yaroslav.hw07.multitrading.handler.exception.ExceptionHandler
import erlikh.yaroslav.hw07.multitrading.ioc.IoC
import erlikh.yaroslav.hw4.actions.basecommand.BaseCommand

class BaseQueueHandlerImpl implements BaseQueueHandler {

    @Override
    void handle() {
        BaseCommand command
        while ((command = IoC.resolve("BaseCommandQueue.NextCommand")) != null) {
            try {
                command.execute()
            } catch (Exception exception) {
                ((ExceptionHandler) IoC.resolve("Exception.Handler")).handle(exception, command)
            }
        }
    }
}
