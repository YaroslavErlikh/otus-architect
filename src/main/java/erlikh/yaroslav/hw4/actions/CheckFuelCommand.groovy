package erlikh.yaroslav.hw4.actions

import erlikh.yaroslav.hw4.actions.basecommand.BaseCommand
import erlikh.yaroslav.hw4.actions.interfaces.Fueled
import erlikh.yaroslav.hw4.exception.CommandException

class CheckFuelCommand implements BaseCommand {

    private Fueled fueled

    CheckFuelCommand(Fueled fueled) {
        this.fueled = fueled
    }

    @Override
    void execute() {
        if ((fueled.getVolume() - fueled.getConsumption()).isEmpty()) {
            throw new CommandException()
        }
    }
}
