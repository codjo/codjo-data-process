package net.codjo.dataprocess.server.handler;
import net.codjo.agent.UserId;
import net.codjo.mad.server.handler.Handler;
import net.codjo.mad.server.handler.HandlerMap;
import net.codjo.mad.server.handler.HandlerMapBuilder;
import net.codjo.plugin.common.ApplicationCore;
import net.codjo.reflect.collect.ClassCollector;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.picocontainer.MutablePicoContainer;
/**
 *
 */
public class DataProcessHandlerMapBuilder implements HandlerMapBuilder {
    private HandlerMapBuilder defaultBuilder;
    private ApplicationCore applicationCore;
    private List<ClassCollector> classCollectors;


    public DataProcessHandlerMapBuilder(ApplicationCore applicationCore,
                                        HandlerMapBuilder defaultBuilder,
                                        List<ClassCollector> classCollectors) {
        this.defaultBuilder = defaultBuilder;
        this.applicationCore = applicationCore;
        this.classCollectors = classCollectors;
    }


    public void collectHandlerFrom(Class fromClass, String inPackage) throws BuildException {
        defaultBuilder.collectHandlerFrom(fromClass, inPackage);
    }


    public void addGlobalComponent(Class aClass) {
        defaultBuilder.addGlobalComponent(aClass);
    }


    public void addGlobalComponent(Object object) {
        defaultBuilder.addGlobalComponent(object);
    }


    public void addSessionComponent(Class aClass) {
        defaultBuilder.addSessionComponent(aClass);
    }


    public void removeSessionComponent(Class aClass) {
        defaultBuilder.removeSessionComponent(aClass);
    }


    public void addUserHandler(Class<? extends Handler> handlerCommandClass) {
        defaultBuilder.addUserHandler(handlerCommandClass);
    }


    public HandlerMap createHandlerMap(UserId userId, Object[] contextualInstances) {
        MutablePicoContainer pico = applicationCore.createChildPicoContainer();
        try {
            Class[] classes = collectHandler();
            return new DataProcessHandlerMap(pico,
                                             defaultBuilder.createHandlerMap(userId, contextualInstances),
                                             classes);
        }
        catch (Exception ex) {
            throw new RuntimeException("Echec de la récupération de handlers : " + ex.getLocalizedMessage());
        }
    }


    private Class[] collectHandler() throws Exception {
        Set<Class> classes = new HashSet<Class>();
        for (ClassCollector classCollector : classCollectors) {
            Collections.addAll(classes, classCollector.collect());
        }
        return classes.toArray(new Class[classes.size()]);
    }
}
