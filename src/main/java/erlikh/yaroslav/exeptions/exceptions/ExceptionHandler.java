package erlikh.yaroslav.exeptions.exceptions;

import erlikh.yaroslav.exeptions.command.LogExceptionCommand;
import erlikh.yaroslav.exeptions.command.interfaces.MyCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public class ExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(ExceptionHandler.class);

    private static final Map<Class<? extends MyCommand>, Map<Class<? extends Exception>, BiFunction<MyCommand, Exception, MyCommand>>> commandExceptionMap = new HashMap<>();

    public static MyCommand handle(MyCommand c, Exception ex) {
        Map<Class<? extends Exception>, BiFunction<MyCommand, Exception, MyCommand>> exceptionHandlers = commandExceptionMap.get(c.getClass());
        if (exceptionHandlers != null && exceptionHandlers.containsKey(ex.getClass())) {
            return exceptionHandlers.get(ex.getClass()).apply(c, ex);
        } else {
            return new LogExceptionCommand(c, ex, log);
        }
    }

    public static void register(Class<? extends MyCommand> c, Class<? extends Exception> ex, BiFunction<MyCommand, Exception, MyCommand> handler) {
        commandExceptionMap.computeIfAbsent(c, k -> new HashMap<>()).put(ex, handler);
    }
}
