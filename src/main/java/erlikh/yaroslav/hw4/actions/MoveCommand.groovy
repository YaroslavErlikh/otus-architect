package erlikh.yaroslav.hw4.actions

import erlikh.yaroslav.hw4.actions.basecommand.BaseCommand
import erlikh.yaroslav.hw4.actions.interfaces.Movable

class MoveCommand implements BaseCommand {

    private Movable movable

    MoveCommand(Movable movable) {
        this.movable = movable
    }

    @Override
    void execute() {
        movable.setPosition(movable.getPosition() + movable.getVelocity())
    }
}
