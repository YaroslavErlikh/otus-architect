package hw12.command

import erlikh.yaroslav.hw12.command.GameMapsInitCommand
import erlikh.yaroslav.hw12.ioc.IoC
import erlikh.yaroslav.hw12.ioc.ScopeBasedStrategy
import erlikh.yaroslav.hw12.model.GameArea
import erlikh.yaroslav.hw12.model.GameMapImpl
import erlikh.yaroslav.hw12.model.interfaces.GameMap
import erlikh.yaroslav.hw12.model.interfaces.Location
import erlikh.yaroslav.hw4.actions.basecommand.BaseCommand
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import static org.assertj.core.api.Assertions.assertThat
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.spy

class GameMapsInitCommandTest {

    @BeforeEach
    void setUp() {
        ScopeBasedStrategy scopeBasedStrategy = new ScopeBasedStrategy()
        (new ScopeBasedStrategy.InitScopeBasedIoCCommand(scopeBasedStrategy)).execute()
    }

    @Test
    void 'create_register_gamemap'() {
        int[] shifts = new int[] {2, 0, 1}
        int areaSize = 10
        Location location = mock(Location.class)
        List<GameMap> expected = new ArrayList<>()
        GameMap gameMap1 = spy(new GameMapImpl(List.of(new GameArea("1", mock(Location.class)), new GameArea("2", mock(Location.class))), null))
        GameMap gameMap2 = spy(new GameMapImpl(List.of(new GameArea("3", mock(Location.class))), gameMap1))
        GameMap gameMap3 = spy(new GameMapImpl(List.of(new GameArea("4", mock(Location.class)), new GameArea("5", mock(Location.class)), new GameArea("6", mock(Location.class))), gameMap2))
        expected.add(gameMap2)
        expected.add(gameMap3)
        expected.add(gameMap1)
        ((BaseCommand) IoC.resolve("IoC.Register", "GameMap.Obtain", args -> {
            int shiftArg = (Integer) args[0]
            int areaSizeArg = (Integer) args[1]
            Location locationArg = (Location) args[2]
            if (areaSizeArg == areaSize && locationArg == location) {
                if (shiftArg == shifts[0]) {
                    return expected.get(0)
                } else if (shiftArg == shifts[1]) {
                    return expected.get(1)
                } else if (shiftArg == shifts[2]) {
                    return expected.get(2)
                }
            }
            return null
        })).execute()

        GameMapsInitCommand command = new GameMapsInitCommand(shifts, areaSize, location)
        command.execute()
        GameMap actual = IoC.resolve("GameMap")
        assertThat(actual).isEqualTo(gameMap3)
    }
}
