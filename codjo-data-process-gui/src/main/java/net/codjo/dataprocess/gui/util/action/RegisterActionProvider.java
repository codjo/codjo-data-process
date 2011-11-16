package net.codjo.dataprocess.gui.util.action;
import javax.swing.Action;
/**
 *
 */
public interface RegisterActionProvider {
    void register(String actionId, Class<? extends Action> clazz);


    void register(Class<? extends Action> clazz);
}
