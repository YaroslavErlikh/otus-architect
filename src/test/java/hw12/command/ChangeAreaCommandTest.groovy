package hw12.command

import erlikh.yaroslav.hw12.command.ChangeAreaCommand
import erlikh.yaroslav.hw12.ioc.IoC
import erlikh.yaroslav.hw12.ioc.ScopeBasedStrategy
import erlikh.yaroslav.hw12.model.GameArea
import erlikh.yaroslav.hw12.model.interfaces.Area
import erlikh.yaroslav.hw12.model.interfaces.AreaObject
import erlikh.yaroslav.hw12.model.interfaces.GameMap
import erlikh.yaroslav.hw12.model.interfaces.Location
import erlikh.yaroslav.hw4.actions.basecommand.BaseCommand
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import static org.mockito.ArgumentMatchers.any
import static org.mockito.ArgumentMatchers.eq
import static org.mockito.Mockito.*

class ChangeAreaCommandTest {

    @BeforeEach
    void setUp() {
        ScopeBasedStrategy scopeBasedStrategy = new ScopeBasedStrategy();
        (new ScopeBasedStrategy.InitScopeBasedIoCCommand(scopeBasedStrategy)).execute()
    }

    @Test
    void 'change_area_and_update_macrocommand'() {
        Location previousLocation = mock(Location.class)
        Location currentLocation = mock(Location.class)

        AreaObject obj = mock(AreaObject.class)
        when(obj.getId()).thenReturn("100")
        when(obj.getLocation()).thenReturn(currentLocation)

        GameArea area1 = mock(GameArea.class)
        GameArea area2 = mock(GameArea.class)
        GameArea area3 = mock(GameArea.class)
        when(area1.getId()).thenReturn("1")
        when(area2.getId()).thenReturn("2")
        when(area3.getId()).thenReturn("3")

        Map<String, Area> previousAreas = Map.of(area1.getId(), area1, area2.getId(), area2)
        Map<String, Area> currentAreas = Map.of(area2.getId(), area2, area3.getId(), area3)

        GameMap gameMap = mock(GameMap.class)
        when(gameMap.getAreas(eq(previousLocation))).thenReturn(previousAreas)
        when(gameMap.getAreas(eq(currentLocation))).thenReturn(currentAreas)
        ((BaseCommand) IoC.resolve("IoC.Register","GameMap", args -> gameMap)).execute()

        ChangeAreaCommand command = new ChangeAreaCommand(obj, previousLocation)
        command.execute()
        verify(area1, times(1)).removeObject(eq(obj))
        verify(area3, times(1)).addObject(eq(obj))
        verify(area1, times(0)).addObject(any())
        verify(area3, times(0)).removeObject(any())
        verify(area2, times(0)).addObject(any())
        verify(area2, times(0)).removeObject(any())
    }

    @Test
    void 'init_game_place_add_to_area'() {
        Location currentLocation = mock(Location.class)

        AreaObject obj = mock(AreaObject.class)
        when(obj.getId()).thenReturn("100")
        when(obj.getLocation()).thenReturn(currentLocation)

        GameArea area1 = mock(GameArea.class)
        when(area1.getId()).thenReturn("1")

        Map<String, Area> currentAreas = Map.of(area1.getId(), area1)

        GameMap gameMap = mock(GameMap.class)
        when(gameMap.getAreas(eq(currentLocation))).thenReturn(currentAreas)
        ((BaseCommand) IoC.resolve("IoC.Register","GameMap", args -> gameMap)).execute()

        ChangeAreaCommand command = new ChangeAreaCommand(obj, null)
        command.execute()
        verify(area1, times(1)).addObject(eq(obj))
        verify(area1, times(0)).removeObject(any())
    }
}
