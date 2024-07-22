package erlikh.yaroslav.hw4.actions

import erlikh.yaroslav.hw4.actions.basecommand.BaseCommand
import erlikh.yaroslav.hw4.actions.interfaces.Rotable

class RotateCommand implements BaseCommand {

    Rotable rotable

    RotateCommand(Rotable rotable) {
        this.rotable = rotable
    }

    @Override
    void execute() {
        rotable.setAngle(rotable.getAngle() + rotable.getAngularVelocity())
    }
}
