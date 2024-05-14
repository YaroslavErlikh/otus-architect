package erlikh.yaroslav.hw4.actions

import erlikh.yaroslav.hw4.actions.basecommand.BaseCommand
import erlikh.yaroslav.hw4.actions.interfaces.Fueled

class BurnFuelCommand implements BaseCommand {

    private Fueled fueled

    BurnFuelCommand(Fueled fueled) {
        this.fueled = fueled
    }

    @Override
    void execute() {
        fueled.setVolume(fueled.getVolume() - fueled.getConsumption())
    }
}
