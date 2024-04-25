package erlikh.yaroslav.spacebattle.model

class Position {

    double x
    double y

    Position(double x, double y) {
        this.x = x
        this.y = y
    }

    Position plus(Position newPos) {
        return new Position(this.x + newPos.x, this.y + newPos.y)
    }
}
