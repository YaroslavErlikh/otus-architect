package erlikh.yaroslav.hw11.state

import erlikh.yaroslav.hw11.command.HardStopCommand
import erlikh.yaroslav.hw11.command.MoveToCommand
import erlikh.yaroslav.hw11.command.interfaces.CommandQueue
import erlikh.yaroslav.hw11.handler.exceptions.interfaces.ExceptionHandler
import erlikh.yaroslav.hw11.ioc.IoC
import erlikh.yaroslav.hw11.state.interfaces.State
import erlikh.yaroslav.hw4.actions.basecommand.BaseCommand

class DefaultState implements State {

    @Override
    State handle(CommandQueue queue) {
        BaseCommand command = queue.readFirst()
        State nextState = this
        if (command != null) {
            try {
                // все команды (в т.ч., HardStop и MoveTo) нужно сначала выполнить
                command.execute()
                if (command instanceof HardStopCommand) {
                    nextState = null
                } else if (command instanceof MoveToCommand) {
                    nextState = new MoveToState(IoC.resolve("CommandQueue.MoveTo"))
                }
            } catch (Exception exception) {
                ((ExceptionHandler) IoC.resolve("Exception.Handler")).handle(exception, command)
            }
        }
        return nextState
    }
}
