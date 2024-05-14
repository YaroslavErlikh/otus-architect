package erlikh.yaroslav.hw4.actions.adapters

import erlikh.yaroslav.hw4.actions.interfaces.Movable
import erlikh.yaroslav.hw4.model.Angle
import erlikh.yaroslav.hw4.model.Position
import erlikh.yaroslav.hw4.model.basemodel.BaseModel

import static erlikh.yaroslav.hw4.model.ModelConstants.*
import static java.lang.Math.*

class MovableAdapter implements Movable {

    private BaseModel model

    MovableAdapter(BaseModel model) {
        this.model = model
    }

    @Override
    Position getPosition() {
        model.getAttribute(POSITION) as Position
    }

    void setPosition(Position newValue) {
        model.setAttribute(POSITION, newValue)
    }

    @Override
    Position getVelocity() {
        def angle = model.getAttribute(ANGLE) as Angle
        def d = angle.getAttribute(DIRECTION) as int
        def n = angle.getAttribute(DIRECTION_NUMBER) as int
        def pos = model.getAttribute(POSITION) as Position
        def v = pos.getAttribute(VELOCITY) as int
        def dRad = 2 * PI * d / n
        return new Position((v * cos(dRad)).intValue(), (v * sin(dRad)).intValue(), v)
    }
}
