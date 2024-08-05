package erlikh.yaroslav.hw11.command.interfaces

import erlikh.yaroslav.hw4.actions.basecommand.BaseCommand

interface CommandQueue {

    void addLast(BaseCommand command)

    BaseCommand readFirst()
}