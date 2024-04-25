package erlikh.yaroslav.spacebattle.move

import erlikh.yaroslav.spacebattle.move.interfaces.Movable

class Move {

    private Movable movable

    Move(Movable movable) {
        this.movable = movable
    }

    void moving() {
        movable.setPosition(movable.getPosition() + movable.getVelocity())
    }
}
