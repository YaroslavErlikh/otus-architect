package erlikh.yaroslav.hw4.actions.interfaces

import erlikh.yaroslav.hw4.model.Position

interface Movable {
    Position getPosition()
    void setPosition(Position newPosition)
    Position getVelocity()
}