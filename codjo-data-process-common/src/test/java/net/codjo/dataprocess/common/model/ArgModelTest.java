package net.codjo.dataprocess.common.model;
import java.util.List;
import org.junit.Test;

import static net.codjo.test.common.matcher.JUnitMatchers.*;
/**
 *
 */
public class ArgModelTest {
    @Test
    public void content() {
        ArgModel argModel = new ArgModel("name", "value");
        assertThat(argModel.getName(), equalTo("name"));
        assertThat(argModel.getValue(), equalTo("value"));
        assertThat(argModel.getPosition(), equalTo(0));
        assertThat(argModel.getType(), equalTo(0));

        argModel = new ArgModel("name", "value", 1, 2);
        assertThat(argModel.getName(), equalTo("name"));
        assertThat(argModel.getValue(), equalTo("value"));
        assertThat(argModel.getPosition(), equalTo(1));
        assertThat(argModel.getType(), equalTo(2));

        argModel.setName("name1");
        assertThat(argModel.getName(), equalTo("name1"));

        argModel.setValue("value1");
        assertThat(argModel.getValue(), equalTo("value1"));

        argModel.setPosition(3);
        assertThat(argModel.getPosition(), equalTo(3));

        argModel.setType(4);
        assertThat(argModel.getType(), equalTo(4));

        assertThat(argModel.toString(), equalTo("name = name1, value = value1, position = 3, type = 4"));
    }


    @Test
    public void isGlobalValue() {
        ArgModel argModel = new ArgModel("name", "$toto$");
        assertThat(argModel.isGlobalValue(), equalTo(true));

        argModel = new ArgModel("name", "toto");
        assertThat(argModel.isGlobalValue(), equalTo(false));
    }


    @Test
    public void isLocalValue() {
        ArgModel argModel = new ArgModel("name", "#toto#");
        assertThat(argModel.isLocalValue(), equalTo(true));

        argModel = new ArgModel("name", "toto");
        assertThat(argModel.isLocalValue(), equalTo(false));
    }


    @Test
    public void getGlobalValue() {
        ArgModel argModel = new ArgModel("name", "$toto$");
        assertThat(argModel.getGlobalValue(), equalTo("toto"));

        argModel = new ArgModel("name", "toto");
        assertThat(argModel.getGlobalValue(), equalTo(null));
    }


    @Test
    public void getLocalValue() {
        ArgModel argModel = new ArgModel("name", "#toto#");
        assertThat(argModel.getLocalValue(), equalTo("toto"));

        argModel = new ArgModel("name", "toto");
        assertThat(argModel.getLocalValue(), equalTo(null));
    }


    @Test
    public void isFunctionAndGetFunctionParams() {
        ArgModel argModel = new ArgModel("name", "dateCloture('$toto$ ', '  45')");
        assertThat(argModel.isFunctionValue(), equalTo(true));
        assertThat(argModel.getFunctionParams().toString(), equalTo("[$toto$, 45]"));

        argModel = new ArgModel("name", "dateCloture('$toto$', '45'");
        assertThat(argModel.getFunctionParams(), equalTo(null));
        assertThat(argModel.isFunctionValue(), equalTo(false));
    }


    @Test
    public void getFunctionParams() {
        ArgModel argModel = new ArgModel("name", "dateCloture('$periode$ ', '  45')");
        List<String> functionParams = argModel.getFunctionParams();
        assertThat(functionParams.get(0), equalTo("$periode$"));
        assertThat(functionParams.get(1), equalTo("45"));
    }
}
