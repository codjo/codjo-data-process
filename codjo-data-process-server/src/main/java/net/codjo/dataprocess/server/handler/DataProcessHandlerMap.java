package net.codjo.dataprocess.server.handler;
import net.codjo.dataprocess.common.table.annotations.Table;
import net.codjo.dataprocess.server.handler.handlerfactory.HandlerFactory;
import net.codjo.mad.server.handler.Handler;
import net.codjo.mad.server.handler.HandlerMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.picocontainer.MutablePicoContainer;
/**
 *
 */
class DataProcessHandlerMap implements HandlerMap {
    private HandlerMap handlerMap;
    private Map<String, HandlerFactory> handlerFactoryMap = new HashMap<String, HandlerFactory>();


    DataProcessHandlerMap(MutablePicoContainer container, HandlerMap handlerMap, Class[] classes) {
        this.handlerMap = handlerMap;
        processClass(container, classes);
    }


    private void processClass(MutablePicoContainer picoContainer, Class[] classes) {
        for (Class aClass : classes) {
            ClassProcessor classProcessor = null;
            if (aClass.isAnnotationPresent(net.codjo.dataprocess.common.table.annotations.Handler.class)) {
                classProcessor = new HandlerCommandClassProcessor(picoContainer, aClass, handlerFactoryMap);
            }
            if (aClass.isAnnotationPresent(Table.class)) {
                classProcessor = new TableClassProcessor(picoContainer, aClass, handlerFactoryMap);
            }
            if (classProcessor != null) {
                classProcessor.process();
            }
        }
    }


    public Handler getHandler(String handlerId) {
        if (handlerFactoryMap.containsKey(handlerId)) {
            HandlerFactory hf = handlerFactoryMap.get(handlerId);
            return hf.getHandler();
        }
        return handlerMap.getHandler(handlerId);
    }


    public Set<String> getHandlerIdSet() {
        Set<String> handlerIdSet = new HashSet<String>(handlerMap.getHandlerIdSet());
        handlerIdSet.addAll(handlerFactoryMap.keySet());
        return handlerIdSet;
    }
}
