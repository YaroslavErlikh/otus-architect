package erlikh.yaroslav.hw06.generator.model

class Position {

    private final int[] coords

    Position(int... coords) {
        this.coords = coords
    }

    int getCoord(int dim) {
        if (dim < 0 || dim >= coords.length) {
            throw new IllegalArgumentException()
        }
        return coords[dim]
    }

    int getDimensions() {
        return coords.length
    }

    static Position plus(Position position1, Position position2){
        if (position1.getDimensions() != position2.getDimensions()) {
            throw new IllegalStateException()
        }
        int[] vector = new int[position1.getDimensions()]
        for (int i = 0; i < vector.length; i++) {
            vector[i] = position1.getCoord(i) + position2.getCoord(i)
        }
        return new Position(vector)
    }
}
