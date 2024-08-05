package hw12.command

import erlikh.yaroslav.hw12.command.CheckAllCollisionsCommand
import erlikh.yaroslav.hw12.ioc.IoC
import erlikh.yaroslav.hw12.ioc.ScopeBasedStrategy
import erlikh.yaroslav.hw12.model.GameArea
import erlikh.yaroslav.hw12.model.GameMapImpl
import erlikh.yaroslav.hw12.model.interfaces.GameMap
import erlikh.yaroslav.hw4.actions.basecommand.BaseCommand
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import static org.mockito.Mockito.*

class CheckAllCollisionsCommandTest {

    @BeforeEach
    void setUp() {
        // инициализация IoC
        ScopeBasedStrategy scopeBasedStrategy = new ScopeBasedStrategy()
        (new ScopeBasedStrategy.InitScopeBasedIoCCommand(scopeBasedStrategy)).execute()
    }

    @Test
    void 'start_check_collision'() {
        GameArea area1 = mock(GameArea.class)
        GameArea area2 = mock(GameArea.class)
        GameArea area3 = mock(GameArea.class)
        GameArea area4 = mock(GameArea.class)
        GameArea area5 = mock(GameArea.class)
        GameArea area6 = mock(GameArea.class)
        GameMap gameMap3 = spy(new GameMapImpl(List.of(area1, area2), null))
        GameMap gameMap2 = spy(new GameMapImpl(List.of(area3), gameMap3))
        GameMap gameMap1 = spy(new GameMapImpl(List.of(area4, area5, area6), gameMap2))
        ((BaseCommand) IoC.resolve("IoC.Register","GameMap", args -> gameMap1)).execute()

        CheckAllCollisionsCommand command = new CheckAllCollisionsCommand()
        command.execute()
        verify(gameMap1, times(1)).handleCollisions()
        verify(gameMap2, times(1)).handleCollisions()
        verify(gameMap3, times(1)).handleCollisions()
        verify(area1, times(1)).handleCollisions()
        verify(area2, times(1)).handleCollisions()
        verify(area3, times(1)).handleCollisions()
        verify(area4, times(1)).handleCollisions()
        verify(area5, times(1)).handleCollisions()
        verify(area6, times(1)).handleCollisions()
    }
}
