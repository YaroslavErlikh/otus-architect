package erlikh.yaroslav.hw07.multitrading.command

import erlikh.yaroslav.hw07.multitrading.ioc.IoC
import erlikh.yaroslav.hw4.actions.basecommand.BaseCommand

class QueueCommand implements BaseCommand {

    @Override
    void execute() {
        (IoC.resolve("IoC.Register","BaseCommandQueue.NextCommand", args1 -> {
            BaseCommand command = (IoC.resolve("BaseCommandQueue") as BaseCommandQueue).readFirst()

            return (command == null) ? IoC.resolve("BaseCommandQueue.EmptyCommand") : command
        }) as BaseCommand).execute()
    }
}
