package erlikh.yaroslav.hw4.actions.adapters

import erlikh.yaroslav.hw4.actions.interfaces.Fueled
import erlikh.yaroslav.hw4.model.Fuel
import erlikh.yaroslav.hw4.model.basemodel.BaseModel

import static erlikh.yaroslav.hw4.model.ModelConstants.FUEL
import static erlikh.yaroslav.hw4.model.ModelConstants.FUEL_CONSUMPTION

class FueledAdapter implements Fueled {

    private BaseModel model

    FueledAdapter(BaseModel model) {
        this.model = model
    }

    @Override
    Fuel getVolume() {
        return model.getAttribute(FUEL) as Fuel
    }

    @Override
    void setVolume(Fuel newValue) {
        model.setAttribute(FUEL, newValue)
    }

    @Override
    Fuel getConsumption() {
        return model.getAttribute(FUEL_CONSUMPTION) as Fuel
    }
}
