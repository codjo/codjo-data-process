package net.codjo.dataprocess.server.control;
import net.codjo.control.common.Dictionary;
import org.junit.Test;

import static net.codjo.test.common.matcher.JUnitMatchers.*;
/**
 *
 */
public class DataProcessControlTest {
    @Test
    public void getValueFromDico() {
        DataProcessControl dataProcessControl = new DataProcessControl("ORBIS", "treatment1");
        Dictionary dico = new Dictionary();
        assertThat(dataProcessControl.getValueFromDico(dico, "key1"), equalTo(null));

        dico.addVariable("key1", "value");
        assertThat(dataProcessControl.getValueFromDico(dico, "key1"), equalTo("value"));

        dico = new Dictionary();
        Dictionary parent = new Dictionary();
        dico.setParent(parent);
        assertThat(dataProcessControl.getValueFromDico(dico, "key1"), equalTo(null));
        assertThat(dataProcessControl.getValueFromDico(parent, "key1"), equalTo(null));

        parent.addVariable("key1", "value");
        assertThat(dataProcessControl.getValueFromDico(dico, "key1"), equalTo("value"));
    }
}
