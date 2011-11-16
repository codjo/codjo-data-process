package net.codjo.dataprocess.server.handler;
import net.codjo.dataprocess.common.table.annotations.HandlerCommand;
import net.codjo.dataprocess.server.handler.handlerfactory.HandlerCommandFactory;
import net.codjo.dataprocess.server.handler.handlerfactory.HandlerFactory;
import java.lang.reflect.Method;
import java.util.Map;
import org.picocontainer.MutablePicoContainer;
/**
 *
 */
public class HandlerCommandClassProcessor implements ClassProcessor {
    private MutablePicoContainer container;
    private Class clazz;
    private Map<String, HandlerFactory> map;


    public HandlerCommandClassProcessor(MutablePicoContainer container,
                                        Class clazz,
                                        Map<String, HandlerFactory> map) {
        this.container = container;
        this.clazz = clazz;
        this.map = map;
    }


    public void process() {
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(HandlerCommand.class)) {
                HandlerFactory handlerFactory = new HandlerCommandFactory(container, clazz, method, null);
                map.put(handlerFactory.getHandlerId(), handlerFactory);
            }
        }
    }
}
