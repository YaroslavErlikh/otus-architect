package hw04

import erlikh.yaroslav.hw4.actions.ChangePositionCommand
import erlikh.yaroslav.hw4.model.Angle
import erlikh.yaroslav.hw4.model.Fuel
import erlikh.yaroslav.hw4.model.Position
import erlikh.yaroslav.hw4.model.Spaceship
import org.junit.jupiter.api.Test

import static erlikh.yaroslav.hw4.model.ModelConstants.*
import static org.assertj.core.api.Assertions.assertThat

class ChangePositionCommandTest {

    @Test
    void 'move_object_to_expected_position'() {
        def spaceship = new Spaceship(
                new Position(1, 3, 5),
                new Angle(4,4),
                new Fuel(20),
                new Fuel(2)
        )
        def command = new ChangePositionCommand(spaceship)

        command.execute()

        assertThat((spaceship.getAttribute(POSITION) as Position).getAttribute(COORD_X)).isEqualTo(-4)
        assertThat((spaceship.getAttribute(POSITION) as Position).getAttribute(COORD_Y)).isEqualTo(3)
    }
}
