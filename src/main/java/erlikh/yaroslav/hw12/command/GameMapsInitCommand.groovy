package erlikh.yaroslav.hw12.command

import erlikh.yaroslav.hw12.ioc.IoC
import erlikh.yaroslav.hw12.model.interfaces.GameMap
import erlikh.yaroslav.hw12.model.interfaces.Location
import erlikh.yaroslav.hw4.actions.basecommand.BaseCommand

import java.util.concurrent.atomic.AtomicReference

class GameMapsInitCommand implements BaseCommand {

    private final int[] shifts
    private final int areaSize
    private final Location location

    GameMapsInitCommand(int[] shifts, int areaSize, Location location) {
        this.shifts = shifts
        this.areaSize = areaSize
        this.location = location
    }

    @Override
    void execute() {
        // Каждое игровое поле имеет ссылку на следующее. Начинаем с конца, т.к. последнее ссылки не имеет
        AtomicReference<GameMap> gameMapNext = new AtomicReference<>()
        Arrays.stream(shifts).boxed().sorted(Collections.reverseOrder()).forEach(shift -> {
            gameMapNext.set(IoC.resolve("GameMap.Obtain", shift, areaSize, location, gameMapNext.get()))
        })
        // Регистрируем первое игровое поле (будет содержать ссылку на следующее и т.п.)
        ((BaseCommand) IoC.resolve("IoC.Register", "GameMap", args -> gameMapNext.get())).execute()
    }
}
