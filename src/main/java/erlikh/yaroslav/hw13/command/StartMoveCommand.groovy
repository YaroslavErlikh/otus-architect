package erlikh.yaroslav.hw13.command

import erlikh.yaroslav.hw4.actions.basecommand.BaseCommand
import erlikh.yaroslav.hw4.model.basemodel.BaseModel

class StartMoveCommand implements BaseCommand {

    private final BaseModel object
    private final double initialVelocity

    StartMoveCommand(BaseModel object, double initialVelocity) {
        this.object = object
        this.initialVelocity = initialVelocity
    }

    @Override
    void execute() {
        object.setAttribute("initialVelocity", initialVelocity)
    }
}
