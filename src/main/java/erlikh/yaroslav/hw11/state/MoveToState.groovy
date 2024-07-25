package erlikh.yaroslav.hw11.state

import erlikh.yaroslav.hw11.command.HardStopCommand
import erlikh.yaroslav.hw11.command.RunCommand
import erlikh.yaroslav.hw11.command.interfaces.CommandQueue
import erlikh.yaroslav.hw11.state.interfaces.State
import erlikh.yaroslav.hw4.actions.basecommand.BaseCommand

class MoveToState implements State {

    private final CommandQueue otherQueue

    MoveToState(CommandQueue otherQueue) {
        this.otherQueue = otherQueue
    }

    @Override
    State handle(CommandQueue queue) {
        BaseCommand command = queue.readFirst()
        State nextState = this
        if (command != null) {
            // команды HardStop и Run не нужно добавлять в другую очередь
            if (command instanceof HardStopCommand) {
                nextState = null
            } else if (command instanceof RunCommand) {
                nextState = new DefaultState()
            } else {
                otherQueue.addLast(command)
            }
        }
        return nextState
    }
}
