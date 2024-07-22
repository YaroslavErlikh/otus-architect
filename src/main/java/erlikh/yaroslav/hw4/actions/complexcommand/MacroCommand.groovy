package erlikh.yaroslav.hw4.actions.complexcommand

import erlikh.yaroslav.hw4.actions.*
import erlikh.yaroslav.hw4.actions.adapters.FueledAdapter
import erlikh.yaroslav.hw4.actions.adapters.MovableAdapter
import erlikh.yaroslav.hw4.actions.adapters.RotableAdapter
import erlikh.yaroslav.hw4.actions.basecommand.BaseCommand
import erlikh.yaroslav.hw4.exception.CommandException
import erlikh.yaroslav.hw4.model.basemodel.BaseModel

abstract class MacroCommand implements BaseCommand {

    private List<BaseCommand> commands

    MacroCommand(List<BaseCommand> commands) {
        this.commands = commands
    }

    @Override
    void execute() {
        try {
            commands.each {
                it.execute()
            }
        } catch (Exception ex) {
            throw new CommandException()
        }
    }
}

class CheckFuelMoveBurnFuelMacroCommand<T extends BaseModel> extends MacroCommand {

    CheckFuelMoveBurnFuelMacroCommand(T model) {
        super(List.of(
                new CheckFuelCommand(new FueledAdapter(model)),
                new MoveCommand(new MovableAdapter(model)),
                new BurnFuelCommand(new FueledAdapter(model))
        ))
    }
}

class MoveRotateMacroCommand<T extends BaseModel> extends MacroCommand {

    MoveRotateMacroCommand(T model) {
        super(List.of(
                new CheckMoveCommand(new MovableAdapter(model)),
                new RotateCommand(new RotableAdapter(model)),
                new ChangePositionCommand<T>(model)
        ))
    }
}