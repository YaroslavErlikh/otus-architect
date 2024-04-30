package erlikh.yaroslav.hw4.model

import erlikh.yaroslav.hw4.model.basemodel.BaseModel

import static erlikh.yaroslav.hw4.model.ModelConstants.*

class Spaceship implements BaseModel {

    Spaceship(Position position, Angle angle, Fuel fuel, Fuel fuelConsumption) {
        setAttribute(POSITION, position)
        setAttribute(ANGLE, angle)
        setAttribute(FUEL, fuel)
        setAttribute(FUEL_CONSUMPTION, fuelConsumption)
    }
}
