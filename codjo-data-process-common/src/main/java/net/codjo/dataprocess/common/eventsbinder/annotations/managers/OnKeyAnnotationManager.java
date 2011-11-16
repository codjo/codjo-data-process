/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.dataprocess.common.eventsbinder.annotations.managers;
import net.codjo.dataprocess.common.eventsbinder.AnnotationManager;
import net.codjo.dataprocess.common.eventsbinder.annotations.events.OnKey;
import net.codjo.dataprocess.common.eventsbinder.dynalistener.EventChecker;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.EventListener;
import java.util.EventObject;
/**
 * 
 */
public class OnKeyAnnotationManager extends AnnotationManager<OnKey> {
    @Override
    public Class<?extends EventListener> getListenerClass(OnKey currentAnnotation) {
        return KeyListener.class;
    }


    @Override
    public String[] getBoundPropertiesNames(OnKey currentAnnotation) {
        return currentAnnotation.propertiesBound();
    }


    @Override
    public EventChecker createEventChecker(OnKey currentAnnotation) {
        return new KeyEventChecker(currentAnnotation);
    }

    private static class KeyEventChecker implements EventChecker {
        private final OnKey onKey;

        KeyEventChecker(OnKey onKey) {
            this.onKey = onKey;
        }

        public boolean checkEvent(EventObject eventObject, String methodCalled) {
            boolean keyCharOk = false;
            boolean keyCodeOk = false;
            boolean modifiersOk = false;
            boolean methodOk = false;
            KeyEvent keyEvent = (KeyEvent)eventObject;
            if (onKey.keyChar() == 0 || keyEvent.getKeyChar() == onKey.keyChar()) {
                keyCharOk = true;
            }
            if (onKey.keyCode() == -1 || keyEvent.getKeyCode() == onKey.keyCode()) {
                keyCodeOk = true;
            }
            if (onKey.modifiers() == -1 || keyEvent.getModifiers() == onKey.modifiers()) {
                modifiersOk = true;
            }
            switch (onKey.eventType()) {
                case ALL:
                    methodOk = true;
                    break;
                case PRESSED:
                    methodOk = "keyPressed".equals(methodCalled);
                    break;
                case RELEASED:
                    methodOk = "keyReleased".equals(methodCalled);
                    break;
                case TYPED:
                    methodOk = "keyTyped".equals(methodCalled);
            }
            return keyCharOk && keyCodeOk && modifiersOk && methodOk;
        }
    }
}
