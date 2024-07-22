package erlikh.yaroslav.hw07.multitrading.command

import erlikh.yaroslav.hw07.multitrading.ioc.IoC
import erlikh.yaroslav.hw4.actions.basecommand.BaseCommand

class SoftStopCommand implements BaseCommand {

    @Override
    void execute() {
        (IoC.resolve("IoC.Register","BaseCommandQueue.NextCommand", args1 ->
                (IoC.resolve("BaseCommandQueue") as BaseCommandQueue).readFirst()) as BaseCommand).execute()
    }
}
