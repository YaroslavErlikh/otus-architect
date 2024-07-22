package hw04

import erlikh.yaroslav.hw4.actions.complexcommand.CheckFuelMoveBurnFuelMacroCommand
import erlikh.yaroslav.hw4.exception.CommandException
import erlikh.yaroslav.hw4.model.Angle
import erlikh.yaroslav.hw4.model.Fuel
import erlikh.yaroslav.hw4.model.Position
import erlikh.yaroslav.hw4.model.Spaceship
import org.junit.jupiter.api.Test

import static erlikh.yaroslav.hw4.model.ModelConstants.*
import static org.assertj.core.api.Assertions.assertThat
import static org.assertj.core.api.Assertions.assertThatThrownBy

class CheckFuelMoveBurnFuelMacroCommandTest {

    @Test
    void 'move_with_consume_fuel' () {
        def spaceship = new Spaceship(
                new Position(1, 3, 5),
                new Angle(1 ,4),
                new Fuel(10),
                new Fuel(3)
        )
        def command = new CheckFuelMoveBurnFuelMacroCommand(spaceship)

        command.execute()

        assertThat((spaceship.getAttribute(POSITION) as Position).getAttribute(COORD_X)).isEqualTo(4)
        assertThat((spaceship.getAttribute(POSITION) as Position).getAttribute(COORD_Y)).isEqualTo(6)
        assertThat((spaceship.getAttribute(FUEL) as Fuel).getAttribute(FUEL_LITERS)).isEqualTo(7)
    }

    @Test
    void 'not_move_not_enough_fuel'() {
        def spaceship = new Spaceship(
                new Position(4, 2, 6),
                new Angle(8, 2),
                new Fuel(1),
                new Fuel(5)
        )
        def command = new CheckFuelMoveBurnFuelMacroCommand(spaceship)

        assertThatThrownBy { command.execute() }.isInstanceOf(CommandException.class)

        assertThat((spaceship.getAttribute(POSITION) as Position).getAttribute(COORD_X)).isEqualTo(4)
        assertThat((spaceship.getAttribute(POSITION) as Position).getAttribute(COORD_Y)).isEqualTo(2)
    }
}
