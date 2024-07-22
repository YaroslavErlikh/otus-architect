package erlikh.yaroslav.hw8.command

import erlikh.yaroslav.hw4.actions.basecommand.BaseCommand
import erlikh.yaroslav.hw4.model.basemodel.BaseModel
import erlikh.yaroslav.hw8.ioc.IoC

class InterpretCommand implements BaseCommand {

    private final String gameId
    private final String objectId
    private final String operationId
    private final Object[] args

    InterpretCommand(String gameId, String objectId, String operationId, Object[] args) {
        this.gameId = gameId
        this.objectId = objectId
        this.operationId = operationId
        this.args = args
    }

    /**
     * Получает объект
     * Проверяет допустимость операции
     * Получает команду
     * Кладёт команду в очередь игры
     */
    @Override
    void execute() {
        BaseModel object = IoC.resolve(String.format("Games.%s.Objects.Get", gameId), objectId)
        if (object == null) {
            throw new IllegalArgumentException("Object not found")
        }
        boolean isAllowed = IoC.resolve(String.format("Games.%s.AllowedOperations.Get", gameId), operationId)
        if (!isAllowed) {
            throw new IllegalStateException("Operation not allowed")
        }
        BaseCommand command = IoC.resolve(operationId, object, args)
        CommandQueue commandQueue = IoC.resolve(String.format("Games.%s.CommandQueue", gameId))
        commandQueue.addLast(command)
    }
}
