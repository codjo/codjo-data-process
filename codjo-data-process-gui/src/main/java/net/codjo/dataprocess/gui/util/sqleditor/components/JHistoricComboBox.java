package net.codjo.dataprocess.gui.util.sqleditor.components;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
/**
 *
 */
public class JHistoricComboBox extends JComboBox {

    public JHistoricComboBox(int maxSize) {
        setModel(new HistoryComboBoxModel(maxSize));
    }


    public void historize(String toHistorize) {
        ((HistoryComboBoxModel)getModel()).historize(toHistorize);
    }


    private static class HistoryComboBoxModel implements ComboBoxModel {

        private Stack<String> stack = new Stack<String>();
        private Integer currentSelection = -1;
        private final int maxSize;
        private List<ListDataListener> listeners = new ArrayList<ListDataListener>();


        HistoryComboBoxModel(int maxSize) {
            this.maxSize = maxSize;
        }


        public void setSelectedItem(Object anItem) {
            currentSelection = stack.indexOf(anItem.toString());
        }


        public Object getSelectedItem() {
            if (currentSelection < 0) {
                return null;
            }
            return stack.get(currentSelection);
        }


        public int getSize() {
            return stack.size();
        }


        public Object getElementAt(int index) {
            return stack.get(index);
        }


        public void addListDataListener(ListDataListener listener) {
            listeners.add(listener);
        }


        public void removeListDataListener(ListDataListener listener) {
            listeners.remove(listener);
        }


        synchronized void historize(String toHistorize) {
            if (stack.contains(toHistorize)) {
                currentSelection = stack.indexOf(toHistorize);
            }
            else {
                stack.add(0, toHistorize);
                while (stack.size() > maxSize) {
                    stack.remove(stack.size() - 1);
                }
                currentSelection = 0;
            }
            for (ListDataListener listDataListener : listeners) {
                listDataListener
                      .contentsChanged(new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, 0, 0));
            }
        }
    }
}
