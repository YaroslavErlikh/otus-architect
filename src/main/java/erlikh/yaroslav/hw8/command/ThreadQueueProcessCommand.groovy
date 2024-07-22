package erlikh.yaroslav.hw8.command

import erlikh.yaroslav.hw4.actions.basecommand.BaseCommand

class ThreadQueueProcessCommand implements BaseCommand {

    private final CommandQueue queue

    ThreadQueueProcessCommand(CommandQueue queue) {
        this.queue = queue
    }

    @Override
    void execute() {
        new Thread(() -> {
            new QueueProcessCommand(queue).execute()
        }).start()
    }
}