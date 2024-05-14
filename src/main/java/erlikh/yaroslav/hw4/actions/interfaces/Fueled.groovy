package erlikh.yaroslav.hw4.actions.interfaces

import erlikh.yaroslav.hw4.model.Fuel

interface Fueled {
    Fuel getVolume()
    void setVolume(Fuel newValue)
    Fuel getConsumption()
}