package erlikh.yaroslav.hw12.command

import erlikh.yaroslav.hw12.ioc.IoC
import erlikh.yaroslav.hw12.model.interfaces.GameMap
import erlikh.yaroslav.hw4.actions.basecommand.BaseCommand

class CheckAllCollisionsCommand implements BaseCommand {

    CheckAllCollisionsCommand() {
    }

    @Override
    void execute() {
        // Получаем первое игровое поле
        GameMap gameMap = IoC.resolve("GameMap")
        // Проверяем коллизии (будут проверяться по цепочке по областям и других полям со смещениями)
        gameMap.handleCollisions()
    }
}
