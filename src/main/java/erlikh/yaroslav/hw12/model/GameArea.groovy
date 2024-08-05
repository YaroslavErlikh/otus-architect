package erlikh.yaroslav.hw12.model

import erlikh.yaroslav.hw12.command.MacroCommand
import erlikh.yaroslav.hw12.ioc.IoC
import erlikh.yaroslav.hw12.model.interfaces.Area
import erlikh.yaroslav.hw12.model.interfaces.AreaObject
import erlikh.yaroslav.hw12.model.interfaces.Location
import erlikh.yaroslav.hw4.actions.basecommand.BaseCommand

import java.util.concurrent.ConcurrentHashMap
import java.util.stream.Collectors

class GameArea implements Area {

    private final String id
    private final Location location
    private final Map<String, AreaObject> objects
    private volatile BaseCommand checkCollisionsCommand = () -> {}

    GameArea(String id, Location location) {
        this.id = id
        this.location = location
        this.objects = new ConcurrentHashMap<>()
    }

    @Override
    String getId() {
        return id
    }

    @Override
    void addObject(AreaObject object) {
        objects.put(object.getId(), object)
        updateCheckCollisionCommand()
    }

    @Override
    void removeObject(AreaObject object) {
        objects.remove(object.getId())
        updateCheckCollisionCommand()
    }

    @Override
    Collection<AreaObject> getObjects() {
        return Collections.unmodifiableCollection(objects.values())
    }

    @Override
    void handleCollisions() {
        checkCollisionsCommand.execute()
    }

    @Override
    boolean testLocation(Location location) {
        return this.location.testIntersection(location)
    }

    private void updateCheckCollisionCommand() {
        // для каждого объекта окрестности создает команду проверки коллизии этих двух объектов, помещает в макрокоманду
        List<BaseCommand> checkCollisionsCommands = new ArrayList<>()
        List<AreaObject> objectsList = objects.values().stream().sorted(Comparator.comparing(AreaObject::getId)).collect(Collectors.toList())
        for (int i = 0; i < objectsList.size(); i++) {
            for (int j = i + 1; j < objectsList.size(); j++) {
                BaseCommand command = IoC.resolve("CheckCollisionCommand", objectsList.get(i), objectsList.get(j))
                checkCollisionsCommands.add(command)
            }
        }
        // записывает макрокоманду проверки коллизий объектов
        this.checkCollisionsCommand = new MacroCommand(checkCollisionsCommands)
    }
}
