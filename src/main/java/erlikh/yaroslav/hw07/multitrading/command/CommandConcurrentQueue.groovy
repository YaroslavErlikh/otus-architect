package erlikh.yaroslav.hw07.multitrading.command

import erlikh.yaroslav.hw4.actions.basecommand.BaseCommand

import java.util.concurrent.ConcurrentLinkedDeque

class CommandConcurrentQueue implements BaseCommandQueue {

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

    @Override
    int size() {
        return deque.size()
    }
}