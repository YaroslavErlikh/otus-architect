package erlikh.yaroslav.hw13.command

import erlikh.yaroslav.hw4.actions.basecommand.BaseCommand
import erlikh.yaroslav.hw4.model.basemodel.BaseModel

class ShootCommand implements BaseCommand {

    private final BaseModel object
    private final int fireDirection

    ShootCommand(BaseModel object, int fireDirection) {
        this.object = object
        this.fireDirection = fireDirection
    }

    @Override
    void execute() {
        object.setAttribute("fireDirection", fireDirection)
    }
}
