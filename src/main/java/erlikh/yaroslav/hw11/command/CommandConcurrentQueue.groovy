package erlikh.yaroslav.hw11.command

import erlikh.yaroslav.hw11.command.interfaces.CommandQueue
import erlikh.yaroslav.hw4.actions.basecommand.BaseCommand

import java.util.concurrent.ConcurrentLinkedDeque

class CommandConcurrentQueue implements CommandQueue {

    private final Deque<BaseCommand> deque

    CommandConcurrentQueue() {
        this.deque = new ConcurrentLinkedDeque<>()
    }

    @Override
    void addLast(BaseCommand object) {
        deque.addLast(object)
    }

    @Override
    BaseCommand readFirst() {
        return deque.pollFirst()
    }
}
