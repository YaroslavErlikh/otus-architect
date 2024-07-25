package erlikh.yaroslav.hw12.model.interfaces

interface AreaObject {

    /**
     * Получить id
     * @return уникальный id объекта
     */
    String getId()

    /**
     * Текущее положение
     * @return текущее расположение в пространстве
     */
    Location getLocation()
}