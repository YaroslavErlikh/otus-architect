package erlikh.yaroslav.hw12.handler.exceptions

import erlikh.yaroslav.hw4.actions.basecommand.BaseCommand

interface ExceptionHandler {

    void handle(Exception exception, BaseCommand command)
}