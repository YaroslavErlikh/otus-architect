package erlikh.yaroslav.hw4.actions.interfaces

import erlikh.yaroslav.hw4.model.Angle

interface Rotable {
    Angle getAngle()
    void setAngle(Angle newAngle)
    int getAngularVelocity()
}