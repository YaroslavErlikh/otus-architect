package erlikh.yaroslav.hw11.handler.exceptions.interfaces

import erlikh.yaroslav.hw4.actions.basecommand.BaseCommand

interface ExceptionHandler {

    void handle(Exception exception, BaseCommand command)
}