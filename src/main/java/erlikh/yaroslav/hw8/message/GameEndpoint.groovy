package erlikh.yaroslav.hw8.message

import erlikh.yaroslav.hw4.actions.basecommand.BaseCommand
import erlikh.yaroslav.hw8.command.GameCommand
import erlikh.yaroslav.hw8.ioc.IoC

class GameEndpoint implements Endpoint {

    void receive(Message message) {
        GameCommand game = IoC.resolve("Games.GetById", message.getGameId())
        if (!game) {
            throw new IllegalArgumentException("Game not found")
        }
        new GameCommand.AddToGameQueueCommand(game, (BaseCommand) IoC.resolve("InterpretCommand", message.getGameId(), message.getObjectId(), message.getOperationId(), message.getArgs())).execute()
    }
}
