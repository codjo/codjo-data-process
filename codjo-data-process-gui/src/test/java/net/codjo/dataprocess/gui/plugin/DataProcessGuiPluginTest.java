package net.codjo.dataprocess.gui.plugin;
import static net.codjo.test.common.matcher.JUnitMatchers.assertThat;
import static net.codjo.test.common.matcher.JUnitMatchers.notNullValue;
import org.junit.Test;
/**
 *
 */
public class DataProcessGuiPluginTest {
    private DataProcessGuiPlugin dataProcessGuiPlugin = new DataProcessGuiPlugin();


    @Test
    public void getConfiguration() {
        assertThat(dataProcessGuiPlugin.getConfiguration(), notNullValue());
    }
}
