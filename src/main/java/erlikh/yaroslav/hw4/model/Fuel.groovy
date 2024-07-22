package erlikh.yaroslav.hw4.model

import erlikh.yaroslav.hw4.model.basemodel.BaseModel

import static erlikh.yaroslav.hw4.model.ModelConstants.*

class Fuel implements BaseModel {

    Fuel(int fuelVolume) {
        setAttribute(FUEL_LITERS, fuelVolume)
    }

    Fuel minus(Fuel consumption) {
        return new Fuel((this.getAttribute(FUEL_LITERS) as int) - (consumption.getAttribute(FUEL_LITERS) as int))
    }

    boolean isEmpty() {
        this.getAttribute(FUEL_LITERS) <= 0
    }
}
