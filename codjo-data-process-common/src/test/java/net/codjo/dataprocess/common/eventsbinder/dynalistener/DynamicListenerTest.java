package net.codjo.dataprocess.common.eventsbinder.dynalistener;
import static net.codjo.test.common.matcher.JUnitMatchers.assertThat;
import static net.codjo.test.common.matcher.JUnitMatchers.equalTo;
import static net.codjo.test.common.matcher.JUnitMatchers.is;
import static net.codjo.test.common.matcher.JUnitMatchers.nullValue;
import static net.codjo.test.common.matcher.JUnitMatchers.sameInstance;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.EventObject;
import javax.swing.JButton;
import org.junit.Before;
import org.junit.Test;
/**
 *
 */
public class DynamicListenerTest {

    private EventCheckerMock eventCheckerMock;
    private EventReactionMock eventReactionMock;


    @Before
    public void before() {
        eventCheckerMock = new EventCheckerMock();
        eventReactionMock = new EventReactionMock();
    }


    @Test
    public void actionListener() {
        assertThat(eventReactionMock.getEventObject(), nullValue());
        assertThat(eventReactionMock.getMethodCalled(), nullValue());

        ActionEvent actionEvent = new ActionEvent("t", 1, "t");
        ActionListener actionListener = (ActionListener)DynamicListener
              .createEventListener(ActionListener.class, eventReactionMock, eventCheckerMock);

        actionListener.actionPerformed(actionEvent);

        assertThat("actionPerformed", equalTo(eventReactionMock.getMethodCalled()));
        assertThat(actionEvent, is(sameInstance(eventReactionMock.getEventObject())));

        eventReactionMock.clear();
        assertThat(eventReactionMock.getEventObject(), nullValue());
        assertThat(eventReactionMock.getMethodCalled(), nullValue());

        eventCheckerMock.setMockCkeckEvent(false);
        actionListener.actionPerformed(actionEvent);
        assertThat(eventReactionMock.getEventObject(), nullValue());
        assertThat(eventReactionMock.getMethodCalled(), nullValue());
    }


    @Test
    public void mouseListener() {
        assertThat(eventReactionMock.getEventObject(), nullValue());
        assertThat(eventReactionMock.getMethodCalled(), nullValue());

        MouseEvent mouseEvent = new MouseEvent(new JButton(), 1, 1, 1, 1, 1, 1, false);
        MouseListener mouseListener = (MouseListener)DynamicListener
              .createEventListener(MouseListener.class, eventReactionMock, eventCheckerMock);

        mouseListener.mouseClicked(mouseEvent);
        assertThat("mouseClicked", equalTo(eventReactionMock.getMethodCalled()));
        assertThat(mouseEvent, is(sameInstance(eventReactionMock.getEventObject())));
        eventReactionMock.clear();
        assertThat(eventReactionMock.getEventObject(), nullValue());
        assertThat(eventReactionMock.getMethodCalled(), nullValue());

        mouseListener.mouseEntered(mouseEvent);
        assertThat("mouseEntered", equalTo(eventReactionMock.getMethodCalled()));
        assertThat(mouseEvent, is(sameInstance(eventReactionMock.getEventObject())));
        eventReactionMock.clear();
        assertThat(eventReactionMock.getEventObject(), nullValue());
        assertThat(eventReactionMock.getMethodCalled(), nullValue());

        mouseListener.mouseExited(mouseEvent);
        assertThat("mouseExited", equalTo(eventReactionMock.getMethodCalled()));
        assertThat(mouseEvent, is(sameInstance(eventReactionMock.getEventObject())));
        eventReactionMock.clear();
        assertThat(eventReactionMock.getEventObject(), nullValue());
        assertThat(eventReactionMock.getMethodCalled(), nullValue());

        eventCheckerMock.setMockCkeckEvent(false);
        mouseListener.mouseExited(mouseEvent);
        assertThat(eventReactionMock.getEventObject(), nullValue());
        assertThat(eventReactionMock.getMethodCalled(), nullValue());
    }


    private static class EventCheckerMock implements EventChecker {
        private boolean mockCkeckEvent = true;


        public void setMockCkeckEvent(boolean mockCkeckEvent) {
            this.mockCkeckEvent = mockCkeckEvent;
        }


        public boolean checkEvent(EventObject eventObject, String methodCalled) {
            return mockCkeckEvent;
        }
    }

    public static class EventReactionMock implements EventReaction {
        private EventObject eventObject;
        private String methodCalled;


        public EventObject getEventObject() {
            return eventObject;
        }


        public String getMethodCalled() {
            return methodCalled;
        }


        public void clear() {
            eventObject = null;
            methodCalled = null;
        }


        public void reactToAnEvent(EventObject receivedEventObject, String receivedMethodCalled) {
            this.eventObject = receivedEventObject;
            this.methodCalled = receivedMethodCalled;
        }
    }
}
