package erlikh.yaroslav.hw4.actions

import erlikh.yaroslav.hw4.actions.basecommand.BaseCommand
import erlikh.yaroslav.hw4.actions.interfaces.Movable
import erlikh.yaroslav.hw4.exception.CommandException
import erlikh.yaroslav.hw4.model.ModelConstants

import static erlikh.yaroslav.hw4.model.ModelConstants.*

class CheckMoveCommand<T extends Movable> implements BaseCommand {

    private T movable

    CheckMoveCommand(T movable) {
        this.movable = movable
    }

    @Override
    void execute() {
        if ((movable.getVelocity().getAttribute(VELOCITY) as int) == 0) {
            throw new CommandException()
        }
    }
}
