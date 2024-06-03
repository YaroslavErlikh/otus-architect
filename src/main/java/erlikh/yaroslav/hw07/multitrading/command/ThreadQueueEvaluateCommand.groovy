package erlikh.yaroslav.hw07.multitrading.command

import erlikh.yaroslav.hw4.actions.basecommand.BaseCommand

class ThreadQueueEvaluateCommand implements BaseCommand {

    @Override
    void execute() {
        new Thread(() -> {
            new QueueEvaluateCommand().execute()
        }).start()
    }
}
