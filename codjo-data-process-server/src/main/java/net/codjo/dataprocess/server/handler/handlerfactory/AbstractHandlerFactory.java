package net.codjo.dataprocess.server.handler.handlerfactory;
import net.codjo.database.api.Database;
import net.codjo.dataprocess.common.Log;
import net.codjo.dataprocess.common.table.annotations.Arg;
import net.codjo.dataprocess.common.table.annotations.Attributes;
import net.codjo.dataprocess.common.table.model.TableModel;
import net.codjo.mad.server.handler.Handler;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.picocontainer.MutablePicoContainer;
/**
 *
 */
public abstract class AbstractHandlerFactory implements HandlerFactory {
    private MutablePicoContainer container;
    private TableModel tableModel;
    private Handler handler;


    protected AbstractHandlerFactory(MutablePicoContainer container, TableModel tableModel) {
        this.container = container;
        this.tableModel = tableModel;
    }


    public Handler getHandler() {
        if (handler == null) {
            handler = createHandler();
            Log.info(getClass(), "HANDLER : Création de = " + getHandlerId());
        }
        return handler;
    }


    protected Database getDatabase() {
        return (Database)container.getComponentInstance(Database.class);
    }


    public TableModel getTableModel() {
        return tableModel;
    }


    public MutablePicoContainer getContainer() {
        return container;
    }


    protected List<Arg> getArgAnnotations(Method method) {
        List<Arg> args = new ArrayList<Arg>();
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        for (Annotation[] annotations : parameterAnnotations) {
            for (Annotation annotation : annotations) {
                if (annotation instanceof Arg) {
                    args.add((Arg)annotation);
                }
            }
        }
        return args;
    }


    protected String[] getAttributes(Method method) {
        if (method.isAnnotationPresent(Attributes.class)) {
            Attributes attributes = method.getAnnotation(Attributes.class);
            return attributes.value();
        }
        else {
            return new String[]{};
        }
    }
}
