package erlikh.yaroslav.spacebattle.rotate.interfaces

import erlikh.yaroslav.spacebattle.model.Angle

interface Rotable {

    Angle getAngle()
    void setAngle(Angle newAngle)
    Angle getAngularVelocity()
}