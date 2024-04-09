import erlikh.yaroslav.Lesson3
import org.junit.Test

class Lesson3Test {

    //Квадратное уравнение — это уравнение вида ax2 + bx + c = 0, где a — первый или старший коэффициент, не равный нулю, b — второй коэффициент, c — свободный член

    @Test
    void 'return_empty_array-no_square'() {
        //x^2+1=0
        Lesson3 lesson3 = new Lesson3()
        def squareRoots = lesson3.solveSquareRoot(1, 0, 1.0, 0e-5)
        assert squareRoots.length == 0
    }

    @Test
    void 'return_2_array-2_square'() {
        //x^2-1=0
        Lesson3 lesson3 = new Lesson3()
        def squareRoots = lesson3.solveSquareRoot(1, 0, -1, 0e-5)
        assert squareRoots.length == 2
    }

    @Test
    void 'return_1_array-1_square'() {
        //x^2+2x+1=0
        Lesson3 lesson3 = new Lesson3()
        def squareRoots = lesson3.solveSquareRoot(1, 2, 1, 0e-5)
        assert squareRoots.length == 1
    }

    @Test(expected = IllegalArgumentException.class)
    void 'return_exception-a_not_be_0'() {
        Lesson3 lesson3 = new Lesson3()
        def squareRoots = lesson3.solveSquareRoot(0.0, 1.1, 2.0, 0e-5)
    }

    @Test
    void 'return_0_array-0_square(0)'() {
        //x^2+2x+1=0
        Lesson3 lesson3 = new Lesson3()
        def squareRoots = lesson3.solveSquareRoot(1.3, 2, 1.1, 0e-5)
        assert squareRoots.length == 0
    }

    @Test
    void 'return_0_array-0_square(1)'() {
        //x^2+2x+1=0
        Lesson3 lesson3 = new Lesson3()
        def squareRoots = lesson3.solveSquareRoot(2.3, 3.5, 4.1, 0e-5)
        assert squareRoots.length == 0
    }

    @Test
    void 'return_exception-string_not_double'() {
        //x^2+2x+1=0
        Lesson3 lesson3 = new Lesson3()
        def squareRoots = lesson3.solveSquareRoot("str", 3.5, 4.1, 0e-5)
        assert squareRoots.length == 0
    }
}
