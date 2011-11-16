/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.dataprocess.common.eventsbinder;
import net.codjo.dataprocess.common.eventsbinder.annotations.events.OnAction;
import net.codjo.dataprocess.common.eventsbinder.annotations.OnError;
import java.util.EventObject;
/**
 * 
 */
public class LogicMock {
    private String called;
    private EventObject eventObject;

    public EventObject getEventObject() {
        return eventObject;
    }


    public String getCalled() {
        return called;
    }


    @OnEventFakeMock(properties = "prop1")
    public void badMock1() {}


    @OnAction(propertiesBound = {"button1","button2"})
    public void mock1(EventObject evtObj) {
        this.eventObject = evtObj;
        fixMethodCalled();
    }

    @OnError
    public void manageError(Throwable th) {

    }

    private void fixMethodCalled() {
        called = new Exception().getStackTrace()[1].getMethodName();
    }
}
