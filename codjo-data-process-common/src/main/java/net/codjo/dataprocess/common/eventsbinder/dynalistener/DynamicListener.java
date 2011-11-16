package net.codjo.dataprocess.common.eventsbinder.dynalistener;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.EventListener;
import java.util.EventObject;
/**
 *
 */
public class DynamicListener implements InvocationHandler {
    private final EventReaction eventReaction;
    private final EventChecker eventChecker;


    private DynamicListener(EventReaction eventReaction,
                            EventChecker eventChecker) {

        this.eventReaction = eventReaction;
        this.eventChecker = eventChecker;
    }


    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        EventObject eventObject = (EventObject)args[0];
        if (eventChecker.checkEvent(eventObject, method.getName())) {
            eventReaction.reactToAnEvent(eventObject, method.getName());
        }
        return null;
    }


    public static EventListener createEventListener(Class<? extends EventListener> listenerTypeClass,
                                                    EventReaction eventReaction,
                                                    EventChecker eventChecker) {

        return (EventListener)Proxy.newProxyInstance(DynamicListener.class.getClassLoader(),
                                                     new Class[]{
                                                           listenerTypeClass},
                                                     new DynamicListener(eventReaction, eventChecker));
    }
}
