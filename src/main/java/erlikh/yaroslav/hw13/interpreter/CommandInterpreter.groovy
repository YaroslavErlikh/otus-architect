package erlikh.yaroslav.hw13.interpreter

import erlikh.yaroslav.hw13.exception.InterpretException
import erlikh.yaroslav.hw13.interpreter.interfaces.Interpreter
import erlikh.yaroslav.hw13.ioc.IoC
import erlikh.yaroslav.hw4.actions.basecommand.BaseCommand
import erlikh.yaroslav.hw4.model.basemodel.BaseModel

import java.lang.reflect.Constructor
import java.lang.reflect.Field
import java.lang.reflect.Parameter

class CommandInterpreter implements Interpreter {

    private final String gameId

    CommandInterpreter(String gameId) {
        this.gameId = gameId
    }

    @Override
    BaseCommand interpret(BaseModel order) {
        Object actionObj = order.getAttribute("action")
        Class<BaseCommand> action = null
        if (actionObj != null) {
            action = IoC.resolve(String.format("Games.%s.Actions.Types.Get", gameId), actionObj.toString())
        }
        if (action == null) {
            throw new InterpretException("Action not found")
        }

        //проверка наличия параметра id объекта (nullable)
        //если есть, проверяем в скоупе игрока существование объекта (приказывать чужим объектам нельзя)
        Object idObj = order.getAttribute("id")
        BaseModel object
        if (idObj != null) {
            object = IoC.resolve(String.format("Games.%s.Objects.Get", gameId), idObj.toString())
            if (object == null) {
                throw new InterpretException("Object not found")
            }
        } else {
            object = null
        }

        //отбираем параметры необходимые для действия
        //если игровой объект не присутствует среди них, то будут обрабатываться любые приказы
        Constructor<?>[] constructors = action.getConstructors()
        Field[] fields = action.getDeclaredFields()
        List<Object> actionParameters = new ArrayList<>()
        if (constructors.length > 0) {
            for (Parameter parameter : constructors[0].getParameters()) {
                Optional<Field> field = Arrays.stream(fields).filter(f -> f.getType() == parameter.getType()).findFirst()
                field.ifPresent(f -> {
                    if (f.getName().equalsIgnoreCase("object")) {
                        actionParameters.add(object)
                    } else {
                        actionParameters.add(order.getAttribute(f.getName()))
                    }
                });
            }
        }

        BaseCommand command = IoC.resolve(String.format("Games.%s.Actions.Commands.Get", gameId), actionObj.toString(), actionParameters.toArray())
        if (command == null) {
            throw new InterpretException("Command not found")
        }

        return command
    }
}
