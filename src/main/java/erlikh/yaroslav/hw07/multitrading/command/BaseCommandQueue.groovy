package erlikh.yaroslav.hw07.multitrading.command

import erlikh.yaroslav.hw4.actions.basecommand.BaseCommand

interface BaseCommandQueue {

        /**
         * Добавить в хвост
         * @param baseCommand
         */
        void addLast(BaseCommand baseCommand)

        /**
         * Взять с головы
         * @return BaseCommand or null, if absent
         */
        BaseCommand readFirst()

        /**
         * Количество элементов в очереди
         * @return int
         */
        int size()
}