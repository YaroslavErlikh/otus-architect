package erlikh.yaroslav.hw4.model

import erlikh.yaroslav.hw4.model.basemodel.BaseModel

import static erlikh.yaroslav.hw4.model.ModelConstants.*

class Angle implements BaseModel {

    Angle(int direction, int angularVelocity) {
        setAttribute(DIRECTION, direction)
        setAttribute(DIRECTION_NUMBER, 8)
        setAttribute(ANGULAR_VELOCITY, angularVelocity)
    }

    Angle plus(int angularVelocity) {
        return new Angle(((this.getAttribute(DIRECTION) as int) + angularVelocity) % (this.getAttribute(DIRECTION_NUMBER) as int),
                this.getAttribute(ANGULAR_VELOCITY) as int)
    }
}
