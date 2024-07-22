package erlikh.yaroslav.hw4.actions

import erlikh.yaroslav.hw4.actions.adapters.MovableAdapter
import erlikh.yaroslav.hw4.actions.basecommand.BaseCommand
import erlikh.yaroslav.hw4.model.basemodel.BaseModel

class ChangePositionCommand<T extends BaseModel> implements BaseCommand {

    private T obj

    ChangePositionCommand(T obj) {
        this.obj = obj
    }

    @Override
    void execute() {
        def movableAdapter = new MovableAdapter(obj)
        movableAdapter.setPosition(movableAdapter.getPosition() + movableAdapter.getVelocity())
    }
}
