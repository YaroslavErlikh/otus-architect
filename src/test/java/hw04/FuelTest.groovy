package hw04

import erlikh.yaroslav.hw4.actions.BurnFuelCommand
import erlikh.yaroslav.hw4.actions.CheckFuelCommand
import erlikh.yaroslav.hw4.actions.interfaces.Fueled
import erlikh.yaroslav.hw4.exception.CommandException
import erlikh.yaroslav.hw4.model.Fuel
import org.junit.jupiter.api.Test
import org.mockito.Mockito

import static org.assertj.core.api.Assertions.assertThatCode
import static org.assertj.core.api.Assertions.assertThatThrownBy

class FuelTest {

    @Test
    void 'reduce_fuel_when_move'() {
        Fueled fueled = Mockito.mock(Fueled.class)
        BurnFuelCommand burnFuelCommand = new BurnFuelCommand(fueled)

        Mockito.doReturn(new Fuel(30)).when(fueled).getVolume()
        Mockito.doReturn(new Fuel(8)).when(fueled).getConsumption()

        burnFuelCommand.execute()

        Mockito.verify(fueled).setVolume(Mockito.refEq(new Fuel(22)))
    }

    @Test
    void 'check_fuel'() {
        Fueled fueled = Mockito.mock(Fueled.class)
        CheckFuelCommand checkFuelCommand = new CheckFuelCommand(fueled)

        Mockito.doReturn(new Fuel(20)).when(fueled).getVolume()
        Mockito.doReturn(new Fuel(10)).when(fueled).getConsumption()

        assertThatCode { checkFuelCommand.execute() }.doesNotThrowAnyException()
    }

    @Test
    void 'check_enough_fuel'() {
        Fueled fueled = Mockito.mock(Fueled.class)
        CheckFuelCommand checkFuelCommand = new CheckFuelCommand(fueled)

        Mockito.doReturn(new Fuel(10)).when(fueled).getVolume()
        Mockito.doReturn(new Fuel(15)).when(fueled).getConsumption()

        assertThatThrownBy { checkFuelCommand.execute() }.isInstanceOf(CommandException.class)
    }
}
