package erlikh.yaroslav.hw12.model

import erlikh.yaroslav.hw12.model.interfaces.Area
import erlikh.yaroslav.hw12.model.interfaces.GameMap
import erlikh.yaroslav.hw12.model.interfaces.Location

class GameMapImpl implements GameMap {

    private final List<Area> areas
    private final GameMap next

    GameMapImpl(List<Area> areas, GameMap next) {
        this.areas = areas
        this.next = next
    }

    @Override
    Map<String, Area> getAreas(Location location) {
        // поиск соответствующей области по координатам
        Map<String, Area> areaMap = new HashMap<>()
        areas.forEach(area -> {
            if (area.testLocation(location)) {
                areaMap.put(area.getId(), area)
            }
        })
        if (next != null) {
            areaMap.putAll(next.getAreas(location))
        }
        return areaMap
    }

    @Override
    void handleCollisions() {
        areas.forEach(Area::handleCollisions)
        if (next != null) {
            next.handleCollisions()
        }
    }
}
