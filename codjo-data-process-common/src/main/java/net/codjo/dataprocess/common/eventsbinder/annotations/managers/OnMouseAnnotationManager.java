/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.dataprocess.common.eventsbinder.annotations.managers;
import net.codjo.dataprocess.common.eventsbinder.AnnotationManager;
import net.codjo.dataprocess.common.eventsbinder.annotations.events.OnMouse;
import net.codjo.dataprocess.common.eventsbinder.dynalistener.EventChecker;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.EventListener;
import java.util.EventObject;
/**
 * 
 */
public class OnMouseAnnotationManager extends AnnotationManager<OnMouse> {
    @Override
    public Class<?extends EventListener> getListenerClass(OnMouse currentAnnotation) {
        return MouseListener.class;
    }


    @Override
    public String[] getBoundPropertiesNames(OnMouse currentAnnotation) {
        return currentAnnotation.value();
    }


    @Override
    public EventChecker createEventChecker(OnMouse currentAnnotation) {
        return new OnMouseAnnotationManager.MouseEventChecker(currentAnnotation);
    }

    private static class MouseEventChecker implements EventChecker {
        private final OnMouse onMouse;

        MouseEventChecker(OnMouse onMouse) {
            this.onMouse = onMouse;
        }

        public boolean checkEvent(EventObject eventObject, String methodCalled) {
            boolean methodOk = false;
            boolean popupOk = false;
            boolean buttonOk = false;
            boolean clickCountOk = false;
            boolean modifiersOk = false;
            MouseEvent event = (MouseEvent)eventObject;
            if (onMouse.popupTriggered() == OnMouse.PopupType.ALL) {
                popupOk = true;
            }
            if (onMouse.popupTriggered() == OnMouse.PopupType.TRUE && event.isPopupTrigger()) {
                popupOk = true;
            }
            if (onMouse.popupTriggered() == OnMouse.PopupType.FALSE && !event.isPopupTrigger()) {
                popupOk = true;
            }

            if (onMouse.button() == -1 || onMouse.button() == event.getButton()) {
                buttonOk = true;
            }
            if (onMouse.clickCount() == -1 || onMouse.clickCount() == event.getClickCount()) {
                clickCountOk = true;
            }
            if (onMouse.modifiers() == -1 || onMouse.modifiers() == event.getModifiers()) {
                modifiersOk = true;
            }
            methodOk = checkMethodCalled(methodOk, methodCalled);

            return popupOk && buttonOk && clickCountOk && modifiersOk && methodOk;
        }


        private boolean checkMethodCalled(boolean methodOk, String methodCalled) {
            switch (onMouse.eventType()) {
                case ALL:
                    methodOk = true;
                    break;
                case PRESSED:
                    methodOk = "mousePressed".equals(methodCalled);
                    break;
                case RELEASED:
                    methodOk = "mouseReleased".equals(methodCalled);
                    break;
                case CLICKED:
                    methodOk = "mouseClicked".equals(methodCalled);
                    break;
                case ENTERED:
                    methodOk = "mouseEntered".equals(methodCalled);
                    break;
                case EXITED:
                    methodOk = "mouseExited".equals(methodCalled);
            }
            return methodOk;
        }
    }
}
