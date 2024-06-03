package erlikh.yaroslav.hw07.multitrading.handler.exception

import erlikh.yaroslav.hw4.actions.basecommand.BaseCommand

interface ExceptionHandler {

    void handle(Exception exception, BaseCommand command)
}
