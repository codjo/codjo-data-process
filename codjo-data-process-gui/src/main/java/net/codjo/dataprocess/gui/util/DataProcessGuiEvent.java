/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.dataprocess.gui.util;
import net.codjo.mad.gui.framework.GuiEvent;
/**
 *
 */
public class DataProcessGuiEvent extends GuiEvent {
    public static final String PRE_CHANGE_REPOSITORY_EVENT = "PRE_CHANGE_REPOSITORY_EVENT";
    public static final String POST_CHANGE_REPOSITORY_EVENT = "POST_CHANGE_REPOSITORY_EVENT";
    public static final String UPDATE_USER_EVENT = "UPDATE_USER_EVENT";
    private Object value;


    public DataProcessGuiEvent(String name) {
        super(name);
    }


    public DataProcessGuiEvent(String name, Object value) {
        super(name);
        this.value = value;
    }


    public Object getValue() {
        return value;
    }
}
