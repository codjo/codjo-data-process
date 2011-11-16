package net.codjo.dataprocess.server.handler.handlerfactory;
import net.codjo.dataprocess.common.Log;
import net.codjo.dataprocess.common.table.annotations.Arg;
import net.codjo.dataprocess.common.table.model.TableModel;
import net.codjo.mad.server.handler.Handler;
import net.codjo.mad.server.handler.HandlerContext;
import net.codjo.mad.server.handler.HandlerException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.defaults.DuplicateComponentKeyRegistrationException;

import static net.codjo.mad.server.handler.XMLUtils.convertFromStringValue;
/**
 *
 */
public class HandlerCommandFactory extends AbstractHandlerFactory {
    private Class<?> clazz;
    private Method method;


    public HandlerCommandFactory(MutablePicoContainer container,
                                 Class<?> clazz,
                                 Method method,
                                 TableModel tableModel) {
        super(container, tableModel);
        this.clazz = clazz;
        this.method = method;
    }


    public String getHandlerId() {
        net.codjo.dataprocess.common.table.annotations.HandlerCommand command
              = method.getAnnotation(net.codjo.dataprocess.common.table.annotations.HandlerCommand.class);
        net.codjo.dataprocess.common.table.annotations.Handler handlerAnno
              = clazz.getAnnotation(net.codjo.dataprocess.common.table.annotations.Handler.class);
        String part1 = handlerAnno.value();
        String part2 = command.value().length() == 0 ? method.getName() : command.value();
        return part1 + (part1.length() != 0 ? "." : "") + part2;
    }


    public Handler createHandler() {
        try {
            getContainer().registerComponentImplementation(getHandlerId(), clazz);
        }
        catch (DuplicateComponentKeyRegistrationException e) {
            Log.debug(getClass(), "HANDLER : Classe " + clazz.getName() + " déjà enregistrée dans pico.");
        }
        Object componentInstance = getContainer().getComponentInstance(getHandlerId());
        return new HandlerCommand(componentInstance, method);
    }


    private static class HandlerCommand extends net.codjo.mad.server.handler.HandlerCommand {
        private Object object;
        private Method method;


        HandlerCommand(Object object, Method method) {
            this.object = object;
            this.method = method;
        }


        @Override
        public CommandResult executeQuery(CommandQuery query) throws HandlerException, SQLException {
            try {
                return new CommandResult("result", method.invoke(object, getArgument(query)));
            }
            catch (InvocationTargetException ex) {
                if (ex.getTargetException() instanceof SQLException) {
                    throw (SQLException)ex.getTargetException();
                }
                else {
                    throw new HandlerException(ex.getLocalizedMessage(), (Exception)ex.getTargetException());
                }
            }
            catch (Exception ex) {
                throw new HandlerException(ex.getLocalizedMessage(), ex);
            }
        }


        private Object[] getArgument(CommandQuery query) {
            List<Object> arguments = new ArrayList<Object>();
            Annotation[][] parameterAnnotations = method.getParameterAnnotations();
            Class<?>[] parameterTypes = method.getParameterTypes();
            int ii = 0;
            for (Annotation[] annotations : parameterAnnotations) {
                Class<?> parameterType = parameterTypes[ii++];
                if (annotations.length > 0) {
                    for (Annotation annotation : annotations) {
                        if (annotation instanceof Arg) {
                            Arg argument = (Arg)annotation;
                            arguments.add(convertFromStringValue(parameterType,
                                                                 query.getArgumentString(argument.value())));
                        }
                    }
                }
                else {
                    if (parameterType.isAssignableFrom(HandlerContext.class)) {
                        arguments.add(getContext());
                    }
                    else {
                        arguments.add(null);
                    }
                }
            }
            return arguments.toArray();
        }
    }
}
