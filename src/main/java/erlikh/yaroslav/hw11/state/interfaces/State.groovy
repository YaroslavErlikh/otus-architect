package erlikh.yaroslav.hw11.state.interfaces

import erlikh.yaroslav.hw11.command.interfaces.CommandQueue

interface State {

    /**
     * Обработать команду из очереди и вернуть ссылку на следующее состояние
     * @param queue очередь команд
     * @return ссылка на следующее состояние
     */
    State handle(CommandQueue queue);
}