package erlikh.yaroslav.spacebattle.move.interfaces

import erlikh.yaroslav.spacebattle.model.Position

interface Movable {
    Position getPosition()
    void setPosition(Position newPosition)
    Position getVelocity()
}