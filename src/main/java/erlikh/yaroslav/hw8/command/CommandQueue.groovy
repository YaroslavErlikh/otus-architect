package erlikh.yaroslav.hw8.command

import erlikh.yaroslav.hw4.actions.basecommand.BaseCommand

interface CommandQueue {

    void addLast(BaseCommand command)

    BaseCommand readFirst()
}