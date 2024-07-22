package erlikh.yaroslav.hw06.generator.interfaces

import erlikh.yaroslav.hw06.generator.model.Position

interface IMovable {

    Position getPosition()

    Position getVelocity()

    void setPosition(Position newValue)

}