package move

import erlikh.yaroslav.spacebattle.exception.ChangePositionException
import erlikh.yaroslav.spacebattle.model.Position
import erlikh.yaroslav.spacebattle.move.Move
import erlikh.yaroslav.spacebattle.move.interfaces.Movable
import org.junit.jupiter.api.Assertions

import org.junit.jupiter.api.Test
import org.mockito.Mockito

import static org.mockito.ArgumentMatchers.refEq

class MoveTest {

    //Для объекта, находящегося в точке (12, 5) и движущегося со скоростью (-7, 3) движение меняет положение объекта на (5, 8)
    @Test
    void 'object_from(12,5)_to(5,8)_with_speed(-7,3)'() {
        Movable movable = Mockito.mock(Movable.class)
        Mockito.when(movable.getPosition()).thenReturn(new Position(12d, 5d))
        Mockito.when(movable.getVelocity()).thenReturn(new Position(-7d, 3d))
        Mockito.doNothing().when(movable).setPosition(Mockito.any())

        new Move(movable).moving()

        Mockito.verify(movable).setPosition(refEq(new Position(5d, 8d)))
    }

    @Test
    void 'move_object_when_not_read_position'() {
        Movable movable = Mockito.mock(Movable.class)
        Mockito.when(movable.getPosition()).thenThrow(ChangePositionException.class)
        Mockito.when(movable.getVelocity()).thenReturn(new Position(-7, 3))
        Mockito.doNothing().when(movable).setPosition(Mockito.any())

        Assertions.assertThrows(ChangePositionException.class, () -> {
            new Move(movable).moving();
        })
    }

    @Test
    void 'move_object_when_not_read_velocity'() {
        Movable movable = Mockito.mock(Movable.class)
        Mockito.when(movable.getPosition()).thenReturn(new Position(12, 5))
        Mockito.when(movable.getVelocity()).thenThrow(ChangePositionException.class)
        Mockito.doNothing().when(movable).setPosition(Mockito.any())

        Assertions.assertThrows(ChangePositionException.class, () -> {
            new Move(movable).moving()
        })
    }

    @Test
    void 'move_object_when_not_change_position'() {
        Movable movable = Mockito.mock(Movable.class)
        Mockito.when(movable.getPosition()).thenReturn(new Position(12, 5))
        Mockito.when(movable.getVelocity()).thenReturn(new Position(-7, 3))
        Mockito.doThrow(ChangePositionException.class).when(movable).setPosition(Mockito.any())

        Assertions.assertThrows(ChangePositionException.class, () -> {
            new Move(movable).moving();
        })
    }
}
