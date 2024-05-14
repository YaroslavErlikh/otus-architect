package erlikh.yaroslav.hw4.actions.adapters

import erlikh.yaroslav.hw4.actions.interfaces.Rotable
import erlikh.yaroslav.hw4.model.Angle
import erlikh.yaroslav.hw4.model.basemodel.BaseModel

import static erlikh.yaroslav.hw4.model.ModelConstants.ANGLE
import static erlikh.yaroslav.hw4.model.ModelConstants.ANGULAR_VELOCITY

class RotableAdapter implements Rotable {

    private BaseModel model

    RotableAdapter(BaseModel model) {
        this.model = model
    }

    @Override
    Angle getAngle() {
        return model.getAttribute(ANGLE) as Angle
    }

    @Override
    void setAngle(Angle newAngle) {
        model.setAttribute(ANGLE, newAngle)
    }

    @Override
    int getAngularVelocity() {
        return (model.getAttribute(ANGLE) as Angle).getAttribute(ANGULAR_VELOCITY) as int
    }
}
