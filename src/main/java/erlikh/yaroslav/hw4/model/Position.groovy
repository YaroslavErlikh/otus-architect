package erlikh.yaroslav.hw4.model;

import erlikh.yaroslav.hw4.model.basemodel.BaseModel

import static erlikh.yaroslav.hw4.model.ModelConstants.*;

class Position implements BaseModel {

    Position(int x, int y, int velocity) {
        setAttribute(COORD_X, x)
        setAttribute(COORD_Y, y)
        setAttribute(VELOCITY, velocity)
    }

    Position plus(Position newPos) {
        return new Position(
                (this.getAttribute('x') as int) + (newPos.getAttribute('x') as int),
                (this.getAttribute('y') as int) + (newPos.getAttribute('y') as int),
                newPos.getAttribute(VELOCITY) as int
        )
    }
}
