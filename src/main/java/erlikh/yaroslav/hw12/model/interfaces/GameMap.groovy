package erlikh.yaroslav.hw12.model.interfaces

interface GameMap {

    /**
     * Получить область на поле игры, который принадлежит объект
     * @param location расположение объекта
     * @return области (несколько из-за наличия смещений)
     */
    Map<String, Area> getAreas(Location location)

    /**
     * Проверить коллизии
     */
    void handleCollisions()
}