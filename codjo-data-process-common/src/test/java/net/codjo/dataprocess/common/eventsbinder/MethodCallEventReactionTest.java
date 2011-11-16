package net.codjo.dataprocess.common.eventsbinder;
import java.awt.event.ActionEvent;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;
import org.junit.Test;

import static net.codjo.test.common.matcher.JUnitMatchers.*;
/**
 *
 */
public class MethodCallEventReactionTest {
    @Test
    public void calling() throws NoSuchMethodException {
        Logic logic = new Logic();
        Method logicMethod = Logic.class.getMethod("toCall", List.class, EventObject.class, String.class);
        Method logicErrorMethod = Logic.class.getMethod("error", Throwable.class);
        List boundObject = new ArrayList();
        MethodCallEventReaction caller = new MethodCallEventReaction(logic, logicMethod, logicErrorMethod,
                                                                     boundObject);

        ActionEvent actionEvent = new ActionEvent("source", 1, "test");
        caller.reactToAnEvent(actionEvent, "");

        assertThat(actionEvent, is(sameInstance(logic.getEvent())));
        assertThat(boundObject, is(sameInstance(logic.getBoundObject())));
        assertThat(logic.getNormallyNull(), nullValue());
    }


    static class Logic {
        private List boundObject;
        private EventObject event;
        private String normallyNull = "toto";
        private Throwable throwable;


        public void error(Throwable th) {
            this.throwable = th;
        }


        public void toCall(List newList, EventObject eventObject, String ukn) {
            this.boundObject = newList;
            this.event = eventObject;
            this.normallyNull = ukn;
        }


        public List getBoundObject() {
            return boundObject;
        }


        public EventObject getEvent() {
            return event;
        }


        public String getNormallyNull() {
            return normallyNull;
        }


        public Throwable getThrowable() {
            return throwable;
        }
    }
}
