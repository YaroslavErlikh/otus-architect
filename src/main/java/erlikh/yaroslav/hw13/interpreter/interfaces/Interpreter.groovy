package erlikh.yaroslav.hw13.interpreter.interfaces

import erlikh.yaroslav.hw4.actions.basecommand.BaseCommand
import erlikh.yaroslav.hw4.model.basemodel.BaseModel

interface Interpreter {

    /**
     * Интерпретация приказа в команду (+ проверка прав на объект при его наличии)
     * @param object приказ
     * @return команда
     */
    BaseCommand interpret(BaseModel object)
}
