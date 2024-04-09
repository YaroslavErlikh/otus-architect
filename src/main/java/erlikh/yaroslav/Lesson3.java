package erlikh.yaroslav;

public class Lesson3 {

    public double[] solveSquareRoot(double a, double b, double c, double e) {
        if (Double.isNaN(a) || Math.abs(a) <= e) {
            throw new IllegalArgumentException("a is NaN or 0");
        }
        double D = b * b - 4 * a * c;

        if (D > 0) {
            double x1 = (-b + Math.sqrt(D)) / (2 * a);
            double x2 = (-b - Math.sqrt(D)) / (2 * a);
            return new double[] {x1, x2};
        } else if (Math.abs(D) <= e) {
            double x = -b / (2 * a);
            return new double[]{x};
        } else {
            return new double[]{};
        }
    }
}
