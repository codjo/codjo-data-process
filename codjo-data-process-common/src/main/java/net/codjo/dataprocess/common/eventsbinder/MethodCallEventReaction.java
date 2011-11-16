package net.codjo.dataprocess.common.eventsbinder;
import net.codjo.dataprocess.common.eventsbinder.dynalistener.EventReaction;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.EventObject;
/**
 *
 */
class MethodCallEventReaction implements EventReaction {
    private Object boundObject;
    private final Object logicObject;
    private final Method logicMethod;
    private final Method logicErrorMethod;
    public static final String FATAL_ERROR_MSG
          = "Fatal Error, when call OnError Handler...";


    MethodCallEventReaction(Object logicObject,
                            Method logicMethod,
                            Method logicErrorMethod,
                            Object boundObject) {
        this.logicObject = logicObject;
        this.logicMethod = logicMethod;
        this.logicErrorMethod = logicErrorMethod;
        this.boundObject = boundObject;
    }


    public void reactToAnEvent(EventObject eventObject, String methodCalled) {
        try {
            Object parameters[] = new Object[logicMethod.getParameterTypes().length];
            for (int index = 0; index < parameters.length; index++) {
                Class<?> paramClass = logicMethod.getParameterTypes()[index];
                if (EventObject.class.isAssignableFrom(paramClass)) {
                    parameters[index] = eventObject;
                }
                if (paramClass.isAssignableFrom(boundObject.getClass())) {
                    parameters[index] = boundObject;
                }
            }
            logicMethod.invoke(logicObject, parameters);
        }
        catch (Exception ex) {
            Throwable toReThrow = ex;
            try {
                if (ex instanceof InvocationTargetException) {
                    toReThrow = ex.getCause();
                }
                logicErrorMethod.invoke(logicObject, toReThrow);
            }
            catch (Throwable th) {
                throw new RuntimeException(FATAL_ERROR_MSG, th);
            }
        }
    }
}
