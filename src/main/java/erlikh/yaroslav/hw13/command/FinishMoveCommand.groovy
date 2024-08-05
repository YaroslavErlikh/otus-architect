package erlikh.yaroslav.hw13.command

import erlikh.yaroslav.hw4.actions.basecommand.BaseCommand
import erlikh.yaroslav.hw4.model.basemodel.BaseModel

class FinishMoveCommand implements BaseCommand {

    private final BaseModel object

    FinishMoveCommand(BaseModel object) {
        this.object = object
    }

    @Override
    void execute() {
        object.setAttribute("velocity", 0)
    }
}
