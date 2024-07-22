package hw04

import erlikh.yaroslav.hw4.actions.complexcommand.MoveRotateMacroCommand
import erlikh.yaroslav.hw4.exception.CommandException
import erlikh.yaroslav.hw4.model.Angle
import erlikh.yaroslav.hw4.model.Fuel
import erlikh.yaroslav.hw4.model.Position
import erlikh.yaroslav.hw4.model.Spaceship
import org.junit.jupiter.api.Test

import static erlikh.yaroslav.hw4.model.ModelConstants.*
import static org.assertj.core.api.Assertions.assertThat
import static org.assertj.core.api.Assertions.assertThatThrownBy

class MoveRotateMacroCommandTest {

    @Test
    void 'rotate_object'() {
        def spaceship = new Spaceship(
                new Position(2, 5, 5),
                new Angle(3, 3),
                new Fuel(10),
                new Fuel(3),
        )
        def command = new MoveRotateMacroCommand(spaceship)

        command.execute()

        assertThat((spaceship.getAttribute(POSITION) as Position).getAttribute(COORD_X) as int).isEqualTo(2)
        assertThat((spaceship.getAttribute(POSITION) as Position).getAttribute(COORD_Y) as int).isEqualTo(0)
        assertThat((spaceship.getAttribute(ANGLE) as Angle).getAttribute(DIRECTION)).isEqualTo(6)
    }

    @Test
    void 'do_not_rotate_and_change_velocity'() {
        def spaceship = new Spaceship(
                new Position(4, 8, 0),
                new Angle(3, 0),
                new Fuel(9),
                new Fuel(2)
        )
        def command = new MoveRotateMacroCommand(spaceship)

        assertThatThrownBy { command.execute() }.isInstanceOf(CommandException.class)

        assertThat((spaceship.getAttribute(POSITION) as Position).getAttribute(VELOCITY)).isEqualTo(0)
        assertThat((spaceship.getAttribute(ANGLE) as Angle).getAttribute(ANGULAR_VELOCITY)).isEqualTo(0)
    }
}
