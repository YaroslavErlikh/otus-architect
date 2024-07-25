package erlikh.yaroslav.hw12.model.interfaces

interface Location {

    /**
     * Проверить, пересекаются ли 2 расположения в пространстве
     * @param location другое расположение
     * @return результат проверки
     */
    boolean testIntersection(Location location)
}