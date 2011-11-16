/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.dataprocess.common.eventsbinder;
import net.codjo.dataprocess.common.eventsbinder.annotations.events.OnAction;
import net.codjo.dataprocess.common.eventsbinder.annotations.managers.OnActionAnnotationManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Method;
import java.util.EventObject;
import junit.framework.TestCase;
/**
 * Classe de test de {@link net.codjo.dataprocess.common.eventsbinder.EventsBinder}.
 */
public class EventsBinderTest extends TestCase {
    private EventsBinder eventsBinder;
    private LogicMock logicMock;
    private BoundObject boundObject;

    @Override
    protected void setUp() throws Exception {
        eventsBinder = new EventsBinder();
        logicMock = new LogicMock();
        boundObject = new BoundObject();
    }


    public void test_mountAnnotationManager() throws Exception {
        OnEventFakeMock fakeAnnotation =
            logicMock.getClass().getMethod("badMock1").getAnnotation(OnEventFakeMock.class);
        assertNull(eventsBinder.getAnnotationManagerFor(fakeAnnotation));
        try {
            eventsBinder.mountAnnotationManager(fakeAnnotation);
        }
        catch (EventBinderException ex) {
            assertEquals(EventBinderException.printNotABindAnnotation(fakeAnnotation), ex.getMessage());
        }
        assertNull(eventsBinder.getAnnotationManagerFor(fakeAnnotation));

        OnAction annotation = logicMock.getClass().getMethod("mock1",EventObject.class).getAnnotation(OnAction.class);
        assertNull(eventsBinder.getAnnotationManagerFor(annotation));
        eventsBinder.mountAnnotationManager(annotation);
        assertNotNull(eventsBinder.getAnnotationManagerFor(annotation));
        assertTrue(eventsBinder.getAnnotationManagerFor(annotation) instanceof OnActionAnnotationManager);
    }


    public void test_bindOneAnnotationToAllProperties()
            throws Exception {
        String methodName = "mock1";
        Method currentMethod = logicMock.getClass().getMethod(methodName, EventObject.class);
        OnAction annotation = currentMethod.getAnnotation(OnAction.class);
        eventsBinder.mountAnnotationManager(annotation);
        MethodCallEventReaction methodCallReaction =
            new MethodCallEventReaction(logicMock, currentMethod, null, boundObject);
        String[] propertiesNames = annotation.propertiesBound();
        AnnotationManager annotationManager = eventsBinder.getAnnotationManagerFor(annotation);
        eventsBinder.bindOneAnnotationToAllProperties(propertiesNames, boundObject, annotationManager,
            annotation, methodCallReaction);

        ActionListener[] actionListeners = boundObject.getButton1().getListeners(ActionListener.class);

        ActionEvent event = new ActionEvent(new Object(), 1, "");
        actionListeners[0].actionPerformed(event);

        assertSame(event, logicMock.getEventObject());
        assertEquals(methodName, logicMock.getCalled());
    }


    public void test_bindOneAnnotation() throws Exception {
        String methodName = "mock1";
        Method currentMethod = logicMock.getClass().getMethod(methodName, EventObject.class);
        OnAction annotation = currentMethod.getAnnotation(OnAction.class);
        eventsBinder.mountAnnotationManager(annotation);

        eventsBinder.bindOneAnnotation(annotation, logicMock, currentMethod, null, boundObject);

        ActionListener[] actionListeners = boundObject.getButton1().getListeners(ActionListener.class);

        ActionEvent event = new ActionEvent(new Object(), 1, "");
        actionListeners[0].actionPerformed(event);

        assertSame(event, logicMock.getEventObject());
        assertEquals(methodName, logicMock.getCalled());

        actionListeners = boundObject.getButton2().getListeners(ActionListener.class);
        ActionEvent event2 = new ActionEvent(new Object(), 1, "");
        actionListeners[0].actionPerformed(event2);

        assertSame(event2, logicMock.getEventObject());
        assertEquals(methodName, logicMock.getCalled());
    }



    public void test_bind() throws Exception {
        String methodName = "mock1";

        eventsBinder.bind(logicMock, boundObject);

        ActionListener[] actionListeners = boundObject.getButton1().getListeners(ActionListener.class);

        ActionEvent event = new ActionEvent(new Object(), 1, "");
        actionListeners[0].actionPerformed(event);

        assertSame(event, logicMock.getEventObject());
        assertEquals(methodName, logicMock.getCalled());

        actionListeners = boundObject.getButton2().getListeners(ActionListener.class);
        ActionEvent event2 = new ActionEvent(new Object(), 1, "");
        actionListeners[0].actionPerformed(event2);

        assertSame(event2, logicMock.getEventObject());
        assertEquals(methodName, logicMock.getCalled());
    }
}
