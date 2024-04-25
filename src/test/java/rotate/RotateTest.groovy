package rotate

import erlikh.yaroslav.spacebattle.exception.ChangePositionException
import erlikh.yaroslav.spacebattle.model.Angle
import erlikh.yaroslav.spacebattle.rotate.Rotate
import erlikh.yaroslav.spacebattle.rotate.interfaces.Rotable
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito

import static org.mockito.ArgumentMatchers.refEq

class RotateTest {

    @Test
    void 'object_with_angle=5_velocity=6_change_angle_on_+3(right)'() {
        Rotable rotable = Mockito.mock(Rotable.class)
        Mockito.when(rotable.getAngle()).thenReturn(new Angle(5))
        Mockito.when(rotable.getAngularVelocity()).thenReturn(new Angle(6))
        Mockito.doNothing().when(rotable).setAngle(Mockito.any())

        new Rotate(rotable).rotatingRight()

        Mockito.verify(rotable).setAngle(refEq(new Angle(3)))
    }

    @Test
    void 'object_with_angle=5_velocity=6_change_angle_on_-3(left)'() {
        Rotable rotable = Mockito.mock(Rotable.class)
        Mockito.when(rotable.getAngle()).thenReturn(new Angle(5))
        Mockito.when(rotable.getAngularVelocity()).thenReturn(new Angle(6))
        Mockito.doNothing().when(rotable).setAngle(Mockito.any())

        new Rotate(rotable).rotatingLeft()

        Mockito.verify(rotable).setAngle(refEq(new Angle(-3), 'direction'))
    }

    @Test
    void 'object_with_angle=5_velocity=-6_change_angle_on_-1(left)'() {
        Rotable rotable = Mockito.mock(Rotable.class)
        Mockito.when(rotable.getAngle()).thenReturn(new Angle(5))
        Mockito.when(rotable.getAngularVelocity()).thenReturn(new Angle(-6))
        Mockito.doNothing().when(rotable).setAngle(Mockito.any())

        new Rotate(rotable).rotatingLeft()

        Mockito.verify(rotable).setAngle(refEq(new Angle(-1), 'direction'))
    }

    @Test
    void 'rotate_object_when_not_read_angle'() {
        Rotable rotable = Mockito.mock(Rotable.class)
        Mockito.when(rotable.getAngle()).thenThrow(ChangePositionException.class)
        Mockito.when(rotable.getAngularVelocity()).thenReturn(new Angle(6))
        Mockito.doNothing().when(rotable).setAngle(Mockito.any())

        Assertions.assertThrows(ChangePositionException.class, () -> {
            new Rotate(rotable).rotatingRight()
        })
    }

    @Test
    void 'rotate_object_when_not_read_angular_velocity'() {
        Rotable rotable = Mockito.mock(Rotable.class)
        Mockito.when(rotable.getAngle()).thenReturn(new Angle(5))
        Mockito.when(rotable.getAngularVelocity()).thenThrow(ChangePositionException.class)
        Mockito.doNothing().when(rotable).setAngle(Mockito.any())

        Assertions.assertThrows(ChangePositionException.class, () -> {
            new Rotate(rotable).rotatingRight()
        })
    }

    @Test
    void 'rotate_object_when_not_change_angle'() {
        Rotable rotable = Mockito.mock(Rotable.class)
        Mockito.when(rotable.getAngle()).thenReturn(new Angle(5))
        Mockito.when(rotable.getAngularVelocity()).thenReturn(new Angle(6))
        Mockito.doThrow(ChangePositionException.class).when(rotable).setAngle(Mockito.any())

        Assertions.assertThrows(ChangePositionException.class, () -> {
            new Rotate(rotable).rotatingRight()
        })
    }
}
