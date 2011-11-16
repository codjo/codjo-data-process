package net.codjo.dataprocess.server.handler;
import net.codjo.dataprocess.common.table.annotations.HandlerCommand;
import net.codjo.dataprocess.common.table.annotations.QueryFactory;
import net.codjo.dataprocess.common.table.model.TableModel;
import net.codjo.dataprocess.server.handler.handlerfactory.DeleteHandlerFactory;
import net.codjo.dataprocess.server.handler.handlerfactory.HandlerCommandFactory;
import net.codjo.dataprocess.server.handler.handlerfactory.HandlerFactory;
import net.codjo.dataprocess.server.handler.handlerfactory.InsertHandlerFactory;
import net.codjo.dataprocess.server.handler.handlerfactory.QueryHandlerFactory;
import net.codjo.dataprocess.server.handler.handlerfactory.SelectAllHandlerFactory;
import net.codjo.dataprocess.server.handler.handlerfactory.SelectByPkHandlerFactory;
import net.codjo.dataprocess.server.handler.handlerfactory.SelectRequetorHandlerFactory;
import net.codjo.dataprocess.server.handler.handlerfactory.UpdateHandlerFactory;
import java.lang.reflect.Method;
import java.util.Map;
import org.picocontainer.MutablePicoContainer;
/**
 *
 */
public class TableClassProcessor implements ClassProcessor {
    private MutablePicoContainer container;
    private Class clazz;
    private Map<String, HandlerFactory> map;


    public TableClassProcessor(MutablePicoContainer container, Class clazz, Map<String, HandlerFactory> map) {
        this.container = container;
        this.clazz = clazz;
        this.map = map;
    }


    public void process() {
        TableModel table = new TableModel(clazz);
        addHandlerFactory(new SelectAllHandlerFactory(container, table));
        addHandlerFactory(new SelectByPkHandlerFactory(container, table));
        addHandlerFactory(new InsertHandlerFactory(container, table));
        addHandlerFactory(new UpdateHandlerFactory(container, table));
        addHandlerFactory(new DeleteHandlerFactory(container, table));
        addHandlerFactory(new SelectRequetorHandlerFactory(container, table));

        for (Method method : clazz.getDeclaredMethods()) {
            HandlerFactory handlerFactory = null;
            if (method.isAnnotationPresent(HandlerCommand.class)) {
                handlerFactory = new HandlerCommandFactory(container, clazz, method, table);
            }
            else if (method.isAnnotationPresent(QueryFactory.class)) {
                handlerFactory = new QueryHandlerFactory(container, clazz, method, table);
            }
            if (handlerFactory != null) {
                addHandlerFactory(handlerFactory);
            }
        }
    }


    private void addHandlerFactory(HandlerFactory handlerFactory) {
        map.put(handlerFactory.getHandlerId(), handlerFactory);
    }
}
