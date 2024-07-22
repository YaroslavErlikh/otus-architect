package erlikh.yaroslav.hw8.model

class Coords {

    private final int[] coords

    Coords(int... coords) {
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

    static Coords plus(Coords position1, Coords position2){
        if (position1.getDimensions() != position2.getDimensions()) {
            throw new IllegalStateException()
        }
        int[] vector = new int[position1.getDimensions()]
        for (int i = 0; i < vector.length; i++) {
            vector[i] = position1.getCoord(i) + position2.getCoord(i)
        }
        return new Coords(vector)
    }
}
