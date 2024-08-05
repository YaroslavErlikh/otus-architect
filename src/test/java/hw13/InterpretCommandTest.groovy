package hw13

import erlikh.yaroslav.hw13.command.InterpretCommand
import erlikh.yaroslav.hw13.interpreter.interfaces.Interpreter
import erlikh.yaroslav.hw13.ioc.IoC
import erlikh.yaroslav.hw13.ioc.ScopeBasedStrategy
import erlikh.yaroslav.hw4.actions.basecommand.BaseCommand
import erlikh.yaroslav.hw4.model.basemodel.BaseModel
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import static org.mockito.Mockito.*

class InterpretCommandTest {

    private String gameId
    private Interpreter interpreter

    @BeforeEach
    void setUp() {
        ScopeBasedStrategy scopeBasedStrategy = new ScopeBasedStrategy()
        (new ScopeBasedStrategy.InitScopeBasedIoCCommand(scopeBasedStrategy)).execute()

        gameId = "game"
        IoC.resolve("Games.Create", gameId)

        interpreter = mock(Interpreter.class)
        ((BaseCommand) IoC.resolve("IoC.Register", "Interpreter.Command.Execute", args -> {
            if (gameId.equals(args[0])) {
                return interpreter.interpret((BaseModel) args[1])
            } else {
                return null
            }
        })).execute()
    }

    @Test
    void shouldCallInterpreterAndAddCommandToQueue() {
        BaseModel order = mock(BaseModel.class)
        BaseCommand command = mock(BaseCommand.class)
        when(interpreter.interpret(refEq(order))).thenReturn(command)
        new InterpretCommand(gameId, order).execute()
        verify(command, times(1)).execute()
    }

}
