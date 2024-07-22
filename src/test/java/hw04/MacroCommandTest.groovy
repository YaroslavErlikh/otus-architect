package hw04

import erlikh.yaroslav.hw4.actions.basecommand.BaseCommand
import erlikh.yaroslav.hw4.actions.complexcommand.MacroCommand
import erlikh.yaroslav.hw4.exception.CommandException
import org.assertj.core.api.ThrowableAssert
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito

import static org.assertj.core.api.Assertions.assertThatThrownBy

class MacroCommandTest {

    private BaseCommand command1 = Mockito.mock(BaseCommand.class)
    private BaseCommand command2 = Mockito.mock(BaseCommand.class)
    private BaseCommand command3 = Mockito.mock(BaseCommand.class)

    private List<BaseCommand> commandList
    private MacroCommand macroCommand

    @BeforeEach
    void setup() {
        commandList = List.of(command1, command2, command3)
        macroCommand = new MacroCommand(commandList) {}
    }

    @Test
    void 'run_inner_commands'() {
        macroCommand.execute()

        commandList.forEach {
            Mockito.verify(it).execute()
        }
    }

    @Test
    void 'stop_if_one_inner_fail'() {
        Mockito.when(command2.execute()).thenThrow(new RuntimeException())

        assertThatThrownBy(() -> macroCommand.execute()).isInstanceOf(CommandException.class)
    }
}