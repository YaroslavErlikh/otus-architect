package hw12.model

import erlikh.yaroslav.hw12.command.CheckCollisionCommand
import erlikh.yaroslav.hw12.ioc.IoC
import erlikh.yaroslav.hw12.ioc.ScopeBasedStrategy
import erlikh.yaroslav.hw12.model.GameArea
import erlikh.yaroslav.hw12.model.interfaces.AreaObject
import erlikh.yaroslav.hw12.model.interfaces.Location
import erlikh.yaroslav.hw4.actions.basecommand.BaseCommand
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import static org.assertj.core.api.Assertions.assertThat
import static org.mockito.Mockito.*

class GameAreaTest {

    @BeforeEach
    void setUp() {
        ScopeBasedStrategy scopeBasedStrategy = new ScopeBasedStrategy()
        (new ScopeBasedStrategy.InitScopeBasedIoCCommand(scopeBasedStrategy)).execute()
    }

    @Test
    void 'add_delete_object_and_update_command_check_collision'() {
        List<CheckCollisionInfo> checkCollisionCommands = new ArrayList<>()

        ((BaseCommand) IoC.resolve("IoC.Register", "CheckCollisionCommand", args -> {
            AreaObject object1 = (AreaObject) args[0]
            AreaObject object2 = (AreaObject) args[1]
            BaseCommand command = spy(new CheckCollisionCommand(object1, object2))
            checkCollisionCommands.add(new CheckCollisionInfo(command, object1, object2))
            return command
        })).execute()

        String id = "1"
        Location location = mock(Location.class)
        GameArea gameArea = new GameArea(id, location)

        AreaObject object1 = mock(AreaObject.class)
        when(object1.getId()).thenReturn("100")
        AreaObject object2 = mock(AreaObject.class)
        when(object2.getId()).thenReturn("200")
        AreaObject object3 = mock(AreaObject.class)
        when(object3.getId()).thenReturn("300")

        gameArea.addObject(object1)
        assertThat(checkCollisionCommands).isEmpty()

        gameArea.addObject(object2)
        assertThat(checkCollisionCommands.size()).isEqualTo(1)
        assertThat(checkCollisionCommands.get(0).obj1).isEqualTo(object1)
        assertThat(checkCollisionCommands.get(0).obj2).isEqualTo(object2)
        gameArea.handleCollisions()
        verify(checkCollisionCommands.get(0).command, times(1)).execute()

        gameArea.addObject(object3)
        assertThat(checkCollisionCommands.size()).isEqualTo(4)
        assertThat(checkCollisionCommands.get(1).obj1).isEqualTo(object1)
        assertThat(checkCollisionCommands.get(1).obj2).isEqualTo(object2)
        assertThat(checkCollisionCommands.get(2).obj1).isEqualTo(object1)
        assertThat(checkCollisionCommands.get(2).obj2).isEqualTo(object3)
        assertThat(checkCollisionCommands.get(3).obj1).isEqualTo(object2)
        assertThat(checkCollisionCommands.get(3).obj2).isEqualTo(object3)
        gameArea.handleCollisions()
        verify(checkCollisionCommands.get(0).command, times(1)).execute()
        verify(checkCollisionCommands.get(1).command, times(1)).execute()
        verify(checkCollisionCommands.get(2).command, times(1)).execute()
        verify(checkCollisionCommands.get(3).command, times(1)).execute()

        gameArea.removeObject(object2)
        assertThat(checkCollisionCommands.size()).isEqualTo(5)
        assertThat(checkCollisionCommands.get(4).obj1).isEqualTo(object1)
        assertThat(checkCollisionCommands.get(4).obj2).isEqualTo(object3)
        gameArea.handleCollisions()
        verify(checkCollisionCommands.get(0).command, times(1)).execute()
        verify(checkCollisionCommands.get(1).command, times(1)).execute()
        verify(checkCollisionCommands.get(2).command, times(1)).execute()
        verify(checkCollisionCommands.get(3).command, times(1)).execute()
        verify(checkCollisionCommands.get(4).command, times(1)).execute()
    }

    private static class CheckCollisionInfo {
        BaseCommand command
        AreaObject obj1
        AreaObject obj2

        CheckCollisionInfo(BaseCommand command, AreaObject obj1, AreaObject obj2) {
            this.command = command
            this.obj1 = obj1
            this.obj2 = obj2
        }
    }
}
