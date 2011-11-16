/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.dataprocess.common.context;
import java.util.HashMap;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;

import static net.codjo.test.common.matcher.JUnitMatchers.*;
/**
 *
 */
public class DataProcessContextTest {
    private DataProcessContext context;


    @Before
    public void before() {
        context = new DataProcessContext();
    }


    @Test
    public void setPropertyLocalArgument() {
        context.setPropertyLocalArgument("tableResult", "T_RESULTAT", 1, "maList d'exécution");
        assertThat("T_RESULTAT", equalTo(context.getProperty("1.maList d'exécution.tableResult")));
        assertThat("{1.maList d'exécution.tableResult=T_RESULTAT}", equalTo(context.toString()));
    }


    @Test
    public void setProperty() {
        context.setProperty("k1", "v1");
        context.setProperty("k2", "v2");
        context.setProperty("k3", "v3");
        context.setProperty("k4", " ");
        context.setProperty("k5", "");
        context.setProperty("k6", null);
        assertThat(context.size(), equalTo(3));
        assertThat(context.getProperty("k4"), equalTo(null));
        assertThat(context.getProperty("k5"), equalTo(null));
        assertThat(context.getProperty("k6"), equalTo(null));

        assertThat(context.getProperty("k3"), equalTo("v3"));
        assertThat(context.getProperty("k1"), equalTo("v1"));
        assertThat(context.getProperty("k2"), equalTo("v2"));

        context.setProperty("k1", "");
        context.setProperty("k2", "   ");
        context.setProperty("k3", null);
        assertThat(context.size(), equalTo(0));
        assertThat("{}", equalTo(context.toString()));
    }


    @Test
    public void putAll() {
        context.setProperty("k1", "v1");
        context.setProperty("k2", "v2");
        context.setProperty("k3", "v2");

        Map<String, String> map = new HashMap<String, String>();
        map.put("k1", "");
        map.put("k2", "   ");
        map.put("k3", "v3");
        map.put("k4", "v4");
        context.putAll(map);

        assertThat(context.size(), equalTo(2));
        assertThat(context.getProperty("k4"), equalTo("v4"));
        assertThat(context.getProperty("k3"), equalTo("v3"));
    }


    @Test
    public void clone1() throws CloneNotSupportedException {
        context.setProperty("k1", "v1");
        context.setProperty("k2", "v2");
        context.setProperty("k3", "v3");

        DataProcessContext dataProcessContext = context.clone();
        assertThat(dataProcessContext.size(), equalTo(3));
        assertThat(dataProcessContext.getProperty("k1"), equalTo("v1"));
        assertThat(dataProcessContext.getProperty("k2"), equalTo("v2"));
        assertThat(dataProcessContext.getProperty("k3"), equalTo("v3"));

        context.setProperty("k4", "v4");
        assertThat(dataProcessContext.getProperty("k4"), equalTo(null));
    }


    @Test
    public void addContext() {
        context.setProperty("k1", "v1");
        context.setProperty("k2", "v2");
        context.setProperty("k3", "v2");

        DataProcessContext context1 = new DataProcessContext();
        context1.setProperty("k1", "v1");
        context1.setProperty("k4", "v4");
        context1.setProperty("k5", "v5");

        context.addContext(context1);

        assertThat(context.size(), equalTo(5));
        assertThat(context.getProperty("k1"), equalTo("v1"));
        assertThat(context.getProperty("k2"), equalTo("v2"));
        assertThat(context.getProperty("k3"), equalTo("v2"));
        assertThat(context.getProperty("k4"), equalTo("v4"));
        assertThat(context.getProperty("k5"), equalTo("v5"));
    }


    @Test
    public void constructor() {
        DataProcessContext dataProcessContext =
              DataProcessContextCodec.decode("<DataProcessContext>\n"
                                             + "  <data key=\"youyou\" value=\"200501\\,hip=P_01\"/>\n"
                                             + "  <data key=\"yaya\" value=\"c'est bien\"/>\n"
                                             + "</DataProcessContext>");

        assertThat("200501\\,hip=P_01", equalTo(dataProcessContext.getProperty("youyou")));
        assertThat("c'est bien", equalTo(dataProcessContext.getProperty("yaya")));

        dataProcessContext = new DataProcessContext();
        assertThat(0, equalTo(dataProcessContext.size()));
    }


    @Test
    public void encode() {
        context.setProperty("k1", "v1");
        context.setProperty("k2", "v2");
        context.setProperty("k3", "v2");

        assertThat("<DataProcessContext>\n"
                   + "  <data key=\"k3\" value=\"v2\"/>\n"
                   + "  <data key=\"k1\" value=\"v1\"/>\n"
                   + "  <data key=\"k2\" value=\"v2\"/>\n"
                   + "</DataProcessContext>", equalTo(context.encode()));
    }
}
