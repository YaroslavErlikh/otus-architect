package erlikh.yaroslav.hw07.multitrading.command

import erlikh.yaroslav.hw07.multitrading.ioc.IoC
import erlikh.yaroslav.hw4.actions.basecommand.BaseCommand

class HardStopCommand implements BaseCommand {

    @Override
    void execute() {
        (IoC.resolve("IoC.Register","BaseCommandQueue.NextCommand", args1 -> (BaseCommand) null) as BaseCommand).execute()
    }
}
