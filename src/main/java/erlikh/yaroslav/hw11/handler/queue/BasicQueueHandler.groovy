package erlikh.yaroslav.hw11.handler.queue

import erlikh.yaroslav.hw11.command.interfaces.CommandQueue
import erlikh.yaroslav.hw11.handler.queue.interfaces.QueueHandler
import erlikh.yaroslav.hw11.state.interfaces.State

class BasicQueueHandler implements QueueHandler {

    private volatile State state
    private final CommandQueue commandQueue

    BasicQueueHandler(State initialState, CommandQueue commandQueue) {
        this.state = initialState
        this.commandQueue = commandQueue
    }

    @Override
    void handle() {
        State currentState
        while ((currentState = state) != null) {
            state = currentState.handle(commandQueue)
        }
    }
}
