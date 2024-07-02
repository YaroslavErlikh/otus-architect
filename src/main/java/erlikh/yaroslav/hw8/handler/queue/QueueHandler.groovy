package erlikh.yaroslav.hw8.handler.queue

import erlikh.yaroslav.hw8.command.CommandQueue

interface QueueHandler {

    void handle(CommandQueue queue)
}
