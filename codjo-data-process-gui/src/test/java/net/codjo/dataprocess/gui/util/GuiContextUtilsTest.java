package net.codjo.dataprocess.gui.util;
import net.codjo.dataprocess.common.context.DataProcessContext;
import net.codjo.mad.gui.framework.DefaultGuiContext;
import net.codjo.mad.gui.framework.MutableGuiContext;
import static net.codjo.test.common.matcher.JUnitMatchers.*;
import org.junit.Test;
/**
 *
 */
public class GuiContextUtilsTest {
    @Test
    public void testDataProcessContext() {
        MutableGuiContext context = new DefaultGuiContext();

        DataProcessContext dataProcessContext = new DataProcessContext();
        dataProcessContext.setProperty("periode", "200512");

        GuiContextUtils.putDataProcessContext(context, dataProcessContext);
        DataProcessContext dataProcessContext2 = GuiContextUtils.getDataProcessContext(context);

        assertThat(dataProcessContext2, is(sameInstance(dataProcessContext)));
        assertThat(dataProcessContext2.getProperty("periode"), equalTo("200512"));
    }
}
