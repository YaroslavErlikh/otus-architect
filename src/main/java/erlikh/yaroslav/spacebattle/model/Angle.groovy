package erlikh.yaroslav.spacebattle.model

class Angle {

    double direction
    double n = 8

    Angle(double direction) {
        this.direction = direction
    }

    Angle plus(Angle newPos) {
        return new Angle((this.direction + newPos.direction) % n)
    }

    Angle minus(Angle newPos) {
        return new Angle((this.direction - newPos.direction) % n)
    }
}
