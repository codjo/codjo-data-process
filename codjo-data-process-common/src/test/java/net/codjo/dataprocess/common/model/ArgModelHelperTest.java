package net.codjo.dataprocess.common.model;
import java.util.List;
import org.junit.Test;

import static net.codjo.test.common.matcher.JUnitMatchers.*;
/**
 *
 */
public class ArgModelHelperTest {

    @Test
    public void getParameters() {
        List<String> list = ArgModelHelper.getParameters("'to, to','titi','tata','tu, tu, va'");
        assertThat(list.toString(), equalTo("[to, to, titi, tata, tu, tu, va]"));
        assertThat(list.get(0), equalTo("to, to"));
        assertThat(list.get(1), equalTo("titi"));
        assertThat(list.get(2), equalTo("tata"));
        assertThat(list.get(3), equalTo("tu, tu, va"));
        assertThat(list.size(), equalTo(4));

        list = ArgModelHelper.getParameters("'to, to' ,    '  titi', 'tata',    '   tu, tu, va'   ");
        assertThat(list.size(), equalTo(4));
        assertThat(list.get(0), equalTo("to, to"));
        assertThat(list.get(1), equalTo("  titi"));
        assertThat(list.get(3), equalTo("   tu, tu, va"));
        assertThat(list.toString(), equalTo("[to, to,   titi, tata,    tu, tu, va]"));

        list = ArgModelHelper.getParameters("");
        assertThat(0, equalTo(list.size()));
        assertThat(list.toString(), equalTo("[]"));

        list = ArgModelHelper.getParameters(null);
        assertThat(0, equalTo(list.size()));
    }


    @Test
    public void isFunctionAndGetFunctionParams() {
        assertThat(ArgModelHelper.isFunctionValue("fct(val1, val2)"), equalTo(true));
        assertThat(ArgModelHelper.isFunctionValue("fct val1, val2)"), equalTo(false));
        assertThat(ArgModelHelper.isFunctionValue("fct(val1, val2"), equalTo(false));
    }


    @Test
    public void getLocalValueGlovalValue() throws Exception {
        assertThat(ArgModelHelper.getLocalValue("#toto#"), equalTo("toto"));
        assertThat(ArgModelHelper.getLocalValue("toto"), equalTo(null));
        assertThat(ArgModelHelper.getGlobalValue("$toto$"), equalTo("toto"));
        assertThat(ArgModelHelper.getGlobalValue("#toto#"), equalTo(null));
    }


    @Test
    public void isGlobalValueLocalValue() throws Exception {
        assertThat(ArgModelHelper.isLocalValue("#toto#"), equalTo(true));
        assertThat(ArgModelHelper.isLocalValue("toto"), equalTo(false));
        assertThat(ArgModelHelper.isGlobalValue("$toto$"), equalTo(true));
        assertThat(ArgModelHelper.isGlobalValue("#toto#"), equalTo(false));
    }


    @Test
    public void isFunctionValue() throws Exception {
        assertThat(ArgModelHelper.isFunctionValue("toto"), equalTo(false));
        assertThat(ArgModelHelper.isFunctionValue("fct(val1, val2)"), equalTo(true));
        assertThat(ArgModelHelper.isFunctionValue("fct(val1, val2"), equalTo(false));
        assertThat(ArgModelHelper.isFunctionValue("fct val1, val2"), equalTo(false));
    }
}
