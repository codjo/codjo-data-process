package net.codjo.dataprocess.gui.util.action;
import net.codjo.mad.gui.base.GuiConfiguration;
import net.codjo.mad.gui.base.GuiPlugin;
import javax.swing.Action;
/**
 *
 */
public class RegisterAction implements RegisterActionProvider {
    private GuiConfiguration guiConfiguration;
    private GuiPlugin guiPlugin;


    public RegisterAction(GuiConfiguration guiConfiguration, GuiPlugin guiPlugin) {
        this.guiConfiguration = guiConfiguration;
        this.guiPlugin = guiPlugin;
    }


    public void register(String actionId, Class<? extends Action> clazz) {
        guiConfiguration.registerAction(guiPlugin, actionId, clazz);
    }


    public void register(Class<? extends Action> clazz) {
        guiConfiguration.registerAction(guiPlugin, clazz.getSimpleName(), clazz);
    }
}
