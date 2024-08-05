package erlikh.yaroslav.hw11.action.interfaces

import erlikh.yaroslav.hw11.model.Position

interface Movable {

    Position getPosition()

    Position getVelocity()

    void setPosition(Position newValue)
}