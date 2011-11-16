package net.codjo.dataprocess.gui.util.std;
import java.util.HashMap;
import java.util.Map;
import java.util.Observer;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
/**
 *
 */
public abstract class AbstractRequestTableLoader implements Observer {
    protected AbstractListWindow abstractListWindow;
    private Map<Object, Object> properties = new HashMap<Object, Object>();


    protected void init(AbstractListWindow listWindow) {
        this.abstractListWindow = listWindow;
        initObserver();
    }


    protected void initObserver() {
        if (abstractListWindow == null) {
            throw new NullPointerException(getClass().getName()
                                           + " n'a pas été initialisé correctement. Veuilliez appeler init() auparavent.");
        }
        abstractListWindow.getCtxt().addObserver(this);
        abstractListWindow.addInternalFrameListener(new CleanUpListener(this));
    }


    abstract protected void refreshTable();


    public void putProperty(Object key, Object value) {
        properties.put(key, value);
    }


    public boolean hasProperty(Object key) {
        return properties.containsKey(key);
    }


    public Object getProperty(Object key) {
        return properties.get(key);
    }


    private class CleanUpListener extends InternalFrameAdapter {
        private Observer observer;


        CleanUpListener(Observer observer) {
            this.observer = observer;
        }


        @Override
        public void internalFrameClosed(InternalFrameEvent event) {
            if (observer != null) {
                abstractListWindow.getCtxt().removeObserver(observer);
            }
        }


        @Override
        public void internalFrameClosing(InternalFrameEvent event) {
        }
    }
}
