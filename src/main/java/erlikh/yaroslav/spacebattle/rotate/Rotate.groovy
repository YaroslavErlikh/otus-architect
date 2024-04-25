package erlikh.yaroslav.spacebattle.rotate

import erlikh.yaroslav.spacebattle.rotate.interfaces.Rotable

class Rotate {

    Rotable rotable

    Rotate(Rotable rotable) {
        this.rotable = rotable
    }

    void rotatingRight() {
        rotable.setAngle(rotable.getAngle() + rotable.getAngularVelocity())
    }

    void rotatingLeft() {
        rotable.setAngle(rotable.getAngle() - rotable.getAngularVelocity())
    }
}
