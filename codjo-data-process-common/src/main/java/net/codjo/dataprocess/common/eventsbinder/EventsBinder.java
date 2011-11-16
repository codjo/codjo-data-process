/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.dataprocess.common.eventsbinder;
import net.codjo.dataprocess.common.eventsbinder.annotations.BindAnnotation;
import net.codjo.dataprocess.common.eventsbinder.annotations.OnError;
import net.codjo.dataprocess.common.eventsbinder.dynalistener.DynamicListener;
import net.codjo.dataprocess.common.eventsbinder.reflect.GetterHelper;
import net.codjo.dataprocess.common.eventsbinder.reflect.GetterHelperException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.EventListener;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;
/**
 * This utility class is used to bind a logic to an object
 */
public class EventsBinder {
    private static final Logger LOG = Logger.getLogger(EventsBinder.class);
    private final Map<Class<?extends Annotation>, AnnotationManager<?extends Annotation>> annotationsManagers =
        new HashMap<Class<?extends Annotation>, AnnotationManager<?extends Annotation>>();

    public void registerAnnotationManager(Class<Annotation> clazz,
        AnnotationManager<?extends Annotation> annotationManager) {
        annotationsManagers.put(clazz, annotationManager);
    }


    protected AnnotationManager getAnnotationManagerFor(Annotation annotation) {
        return annotationsManagers.get(annotation.annotationType());
    }


    /**
     * Effectiv bind a logic class with another object
     *
     * @param logic
     * @param boundObject
     *
     * @throws EventBinderException
     */
    public void bind(Object logic, Object boundObject)
            throws EventBinderException {
        Method errorMethod = getErrorMethod(logic);
        for (Method currentMethod : logic.getClass().getMethods()) {
            for (Annotation annotationBasic : currentMethod.getAnnotations()) {
                boolean isABindAnnotation =
                    annotationBasic.annotationType().getAnnotation(BindAnnotation.class) != null;
                if (isABindAnnotation) {
                    try {
                        bindOneAnnotation(annotationBasic, logic, currentMethod, errorMethod, boundObject);
                    }
                    catch (Exception ex) {
                        throw new EventBinderException(ex);
                    }
                }
            }
        }
    }


    protected Method getErrorMethod(Object logic)
            throws EventBinderException {
        Method errorMethod = null;
        for (Method currentMethod : logic.getClass().getMethods()) {
            if (currentMethod.isAnnotationPresent(OnError.class)) {
                if (errorMethod != null) {
                    throw new EventBinderException("Two method are tagged with @OnError in the logic ("
                        + logic.getClass() + ")");
                }
                if (currentMethod.getParameterTypes().length != 1
                        || currentMethod.getParameterTypes()[0] != Throwable.class) {
                    throw new EventBinderException(
                        "The method tagged with @OnError in the logic must have ONE Throwable parameter ("
                        + logic.getClass() + ")");
                }
                errorMethod = currentMethod;
            }
        }
        if (errorMethod == null) {
            throw new EventBinderException("There's no method tagged with @OnError in the logic ("
                + logic.getClass() + ")");
        }
        return errorMethod;
    }


    protected void bindOneAnnotation(Annotation currentAnnotation, Object logic, Method currentMethod,
        Method erroMethod, Object boundObject)
            throws IllegalAccessException, InstantiationException, EventBinderException, GetterHelperException {
        AnnotationManager annotationManager;
        annotationManager = annotationsManagers.get(currentAnnotation.annotationType());
        if (annotationManager == null) {
            mountAnnotationManager(currentAnnotation);
            annotationManager = annotationsManagers.get(currentAnnotation.annotationType());
        }
        MethodCallEventReaction methodCallReaction =
            new MethodCallEventReaction(logic, currentMethod, erroMethod, boundObject);
        String[] propertyNames = annotationManager.getBoundPropertiesNames(currentAnnotation);

        bindOneAnnotationToAllProperties(propertyNames, boundObject, annotationManager, currentAnnotation,
            methodCallReaction);
    }


    protected void bindOneAnnotationToAllProperties(String[] propertyNames, Object boundObject,
        AnnotationManager annotationManager, Annotation currentAnnotation,
        MethodCallEventReaction methodCallReaction)
            throws GetterHelperException, EventBinderException {
        for (String propertyName : propertyNames) {
            Object property = GetterHelper.getProperty(propertyName, boundObject);

            Class<?extends EventListener> listenerClass =
                annotationManager.getListenerClass(currentAnnotation);

            EventListener listener =
                DynamicListener.createEventListener(listenerClass, methodCallReaction,
                    annotationManager.createEventChecker(currentAnnotation));
            annotationManager.registerListener(currentAnnotation, property, listener, listenerClass);
        }
    }


    protected void mountAnnotationManager(Annotation currentAnnotation)
            throws IllegalAccessException, InstantiationException, EventBinderException {
        BindAnnotation bindAnnotation =
            currentAnnotation.annotationType().getAnnotation(BindAnnotation.class);

        if (bindAnnotation == null) {
            throw new EventBinderException(EventBinderException.printNotABindAnnotation(currentAnnotation));
        }

        Class<?extends AnnotationManager<?extends Annotation>> annotationManagerClass =
            bindAnnotation.managerClass();
        LOG.debug("Mount " + annotationManagerClass);
        annotationsManagers.put(currentAnnotation.annotationType(), annotationManagerClass.newInstance());
    }
}
