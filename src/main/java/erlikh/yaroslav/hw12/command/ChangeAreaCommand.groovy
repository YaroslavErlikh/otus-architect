package erlikh.yaroslav.hw12.command

import erlikh.yaroslav.hw12.ioc.IoC
import erlikh.yaroslav.hw12.model.interfaces.Area
import erlikh.yaroslav.hw12.model.interfaces.AreaObject
import erlikh.yaroslav.hw12.model.interfaces.GameMap
import erlikh.yaroslav.hw12.model.interfaces.Location
import erlikh.yaroslav.hw4.actions.basecommand.BaseCommand

class ChangeAreaCommand implements BaseCommand {

    private final AreaObject object
    private final Location previousLocation

    ChangeAreaCommand(AreaObject object, Location previousLocation) {
        this.object = object
        this.previousLocation = previousLocation
    }

    @Override
    void execute() {
        GameMap gameMap = IoC.resolve("GameMap")
        // определяем предыдущие окрестности (несколько, т.к. каждая соответствует своему игровому полю)
        Map<String, Area> previousAreas
        if (previousLocation != null) {
            previousAreas = gameMap.getAreas(previousLocation)
        } else {
            previousAreas = null
        }
        // определяем окрестности, в которых сейчас присутствует объект
        Map<String, Area> currentAreas = gameMap.getAreas(object.getLocation())

        // если попал в новую окрестность

        // удаляем из списка объектов старой окрестности (+ обновляются макрокоманды проверки коллизий)
        if (previousAreas != null) {
            previousAreas.forEach((key, value) -> {
                if (!currentAreas.containsKey(key)) {
                    value.removeObject(object)
                }
            })
        }
        // добавляем в список объектов новой окрестности (+ обновляются макрокоманды проверки коллизий)
        currentAreas.forEach((key, value) -> {
            if (previousAreas == null || !previousAreas.containsKey(key)) {
                value.addObject(object)
            }
        })
    }
}
