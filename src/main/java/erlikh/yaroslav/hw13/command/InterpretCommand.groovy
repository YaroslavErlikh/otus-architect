package erlikh.yaroslav.hw13.command

import erlikh.yaroslav.hw13.ioc.IoC
import erlikh.yaroslav.hw4.actions.basecommand.BaseCommand
import erlikh.yaroslav.hw4.model.basemodel.BaseModel

class InterpretCommand implements BaseCommand {

    private final String gameId
    private final BaseModel order

    InterpretCommand(String gameId, BaseModel order) {
        this.gameId = gameId
        this.order = order
    }

    /**
     * Интерпретация приказа
     * Выполнить команду
     */
    @Override
    void execute() {
        BaseCommand command = IoC.resolve("Interpreter.Command.Execute", gameId, order)
        command.execute()
    }
}
