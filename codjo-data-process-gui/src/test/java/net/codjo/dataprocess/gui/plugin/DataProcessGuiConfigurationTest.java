package net.codjo.dataprocess.gui.plugin;
import net.codjo.dataprocess.common.context.DataProcessContext;
import net.codjo.dataprocess.common.userparam.User;
import net.codjo.mad.gui.framework.DefaultGuiContext;
import static net.codjo.test.common.matcher.JUnitMatchers.*;
import java.util.Arrays;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
/**
 *
 */
public class DataProcessGuiConfigurationTest {
    private DataProcessGuiConfiguration dataProcessGuiConfiguration;


    @Before
    public void before() {
        dataProcessGuiConfiguration = new DataProcessGuiConfiguration(new DataProcessGuiPlugin());
    }


    @Test
    public void initialisation() {
        assertThat(dataProcessGuiConfiguration.getToolBarRepoConfig("xxx"), nullValue());
        assertThat(dataProcessGuiConfiguration.getPackageArgumentModifier(), nullValue());
        assertThat(dataProcessGuiConfiguration.getUser(), nullValue());
        assertThat(dataProcessGuiConfiguration.getDataProcessContext(), notNullValue());
        assertThat(dataProcessGuiConfiguration.getRepositoryNameMap(), notNullValue());
        assertThat(dataProcessGuiConfiguration.getGlobalParameters(), notNullValue());
    }


    @Test
    public void globalParameter() {
        DefaultGuiContext context = new DefaultGuiContext();

        List<String> globalParameters = Arrays.asList("$periode$", "$dateprec$", "$datecourante$");
        dataProcessGuiConfiguration.setGlobalParameter(globalParameters, context);
        List<String> list = dataProcessGuiConfiguration.getGlobalParameters();

        assertThat(list, is(sameInstance(globalParameters)));
        assertThat(list.size(), equalTo(3));

        assertThat(list, is(sameInstance(globalParameters)));
        assertThat(list, is(sameInstance(list)));
    }


    @Test
    public void packageArgumentModifier() {
        String packageArgumentModifier = "net.codjo.creo.argumentmodifier";

        dataProcessGuiConfiguration.setPackageArgumentModifier(packageArgumentModifier);
        assertThat(dataProcessGuiConfiguration.getPackageArgumentModifier(),
                   equalTo(packageArgumentModifier));

        DataProcessContext dataProcessContext = dataProcessGuiConfiguration.getDataProcessContext();
        assertThat(dataProcessContext.getProperty(DataProcessContext.PACKAGE_ARGUMENT_MODIFIER),
                   equalTo(packageArgumentModifier));

        dataProcessGuiConfiguration.setPackageArgumentModifier(null);
        assertThat(dataProcessContext.getProperty(DataProcessContext.PACKAGE_ARGUMENT_MODIFIER),
                   equalTo(packageArgumentModifier));
    }


    @Test
    public void user() {
        User user = new User();
        dataProcessGuiConfiguration.setUser(user);
        assertThat(dataProcessGuiConfiguration.getUser(), is(sameInstance(user)));
    }
}
