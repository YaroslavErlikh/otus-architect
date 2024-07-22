package erlikh.yaroslav.hw06.generator.adapter

import erlikh.yaroslav.hw4.model.basemodel.BaseModel

interface AdapterGenerator {

    /**
     * Если адаптер отсутствует в кэше, он будет сгенерирован
     * @param interfaceType тип интерфейса
     * @param baseModel объект
     * @return объект реализующий интерфейс
     */
    <T> T resolve(Class<T> interfaceType, BaseModel baseModel);
}
