package erlikh.yaroslav.hw11.command

import erlikh.yaroslav.hw11.command.interfaces.CommandQueue
import erlikh.yaroslav.hw11.ioc.IoC
import erlikh.yaroslav.hw4.actions.basecommand.BaseCommand

class MoveToCommand implements BaseCommand {

    private final CommandQueue otherQueue

    MoveToCommand(CommandQueue otherQueue) {
        this.otherQueue = otherQueue
    }

    @Override
    void execute() {
        ((BaseCommand) IoC.resolve("IoC.Register","CommandQueue.MoveTo", args1 -> otherQueue)).execute()
    }
}
