package net.codjo.dataprocess.server.handler.handlerfactory;
import net.codjo.database.api.Database;
import net.codjo.database.api.query.PreparedQuery;
import net.codjo.dataprocess.common.table.annotations.Arg;
import net.codjo.dataprocess.common.table.annotations.QueryFactory;
import net.codjo.dataprocess.common.table.annotations.Type;
import net.codjo.dataprocess.common.table.model.TableModel;
import net.codjo.mad.server.handler.Handler;
import net.codjo.mad.server.handler.HandlerException;
import net.codjo.mad.server.handler.sql.Getter;
import net.codjo.mad.server.handler.sql.QueryBuilder;
import net.codjo.mad.server.handler.sql.SqlHandler;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.picocontainer.MutablePicoContainer;
/**
 *
 */
public class QueryHandlerFactory extends AbstractHandlerFactory {
    private Class<?> clazz;
    private Method method;


    public QueryHandlerFactory(MutablePicoContainer container,
                               Class<?> clazz,
                               Method method,
                               TableModel tableModel) {
        super(container, tableModel);
        this.clazz = clazz;
        this.method = method;
    }


    public String getHandlerId() {
        QueryFactory queryFactory = method.getAnnotation(QueryFactory.class);
        net.codjo.dataprocess.common.table.annotations.Handler handlerAnno
              = clazz.getAnnotation(net.codjo.dataprocess.common.table.annotations.Handler.class);
        String part1 = handlerAnno.value();
        String part2 = queryFactory.value().length() == 0 ? method.getName() : queryFactory.value();
        return part1 + (part1.length() != 0 ? "." : "") + part2;
    }


    public Handler createHandler() {
        getContainer().registerComponentImplementation(getHandlerId(), clazz);
        Object componentInstance = getContainer().getComponentInstance(getHandlerId());
        return new MySqlHandler(componentInstance, method, getTableModel(), getDatabase());
    }


    private class MySqlHandler extends SqlHandler {
        private Method method;
        private Object object;


        MySqlHandler(Object object, Method method, TableModel tableModel, Database database) {
            super(tableModel.getPkAsStrArray(), "", database);
            this.object = object;
            this.method = method;

            int idx = 1;
            for (String attribute : getAttributes(method)) {
                addGetter(attribute, new Getter(idx++));
            }
        }


        @Override
        protected void fillQuery(PreparedQuery query, Map<String, String> args) throws SQLException {
            int idx = 1;
            for (Arg argAnno : getArgAnnotations(method)) {
                if (argAnno.type() != Type.UNDEFINED) {
                    query.setObject(idx++, args.get(argAnno.value()), argAnno.type().getSqlType());
                }
            }
        }


        @Override
        protected String buildQuery(Map<String, String> arguments) throws HandlerException {
            return new MyQueryBuilder().buildQuery(arguments, this);
        }


        private class MyQueryBuilder implements QueryBuilder {
            public String buildQuery(Map<String, String> args, SqlHandler sqlHandler)
                  throws HandlerException {
                try {
                    return (String)method.invoke(object, getMethodArguments(method, args, sqlHandler));
                }
                catch (Exception ex) {
                    throw new HandlerException(ex);
                }
            }


            private Object[] getMethodArguments(Method method,
                                                Map<String, String> arguments,
                                                SqlHandler sqlHandler) {
                List<Object> values = new ArrayList<Object>();
                Annotation[][] parameterAnnotations = method.getParameterAnnotations();
                Class<?>[] parameterTypes = method.getParameterTypes();
                int ii = 0;
                for (Annotation[] annotations : parameterAnnotations) {
                    Class<?> parameterType = parameterTypes[ii++];
                    if (annotations.length > 0) {
                        for (Annotation annotation : annotations) {
                            if (annotation instanceof Arg) {
                                Arg argAnno = (Arg)annotation;
                                values.add(arguments.get(argAnno.value()));
                            }
                        }
                    }
                    else {
                        if (parameterType.isAssignableFrom(SqlHandler.class)) {
                            values.add(sqlHandler);
                        }
                        else {
                            values.add(null);
                        }
                    }
                }
                return values.toArray();
            }
        }
    }
}