package net.codjo.dataprocess.common.model;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

import static net.codjo.test.common.matcher.JUnitMatchers.*;
/**
 *
 */
public class ArgListTest {
    @Test
    public void content() {
        ArgList argList = new ArgList();
        assertThat(argList.getArgs().size(), equalTo(0));

        List<ArgModel> args = new ArrayList<ArgModel>();
        args.add(new ArgModel("name", "value", 1, 1));
        args.add(new ArgModel("name2", "value2", 2, 2));

        argList.setArgs(args);
        assertThat(argList.getArgs().size(), equalTo(2));

        assertThat(argList.getArgs().get(1).getName(), equalTo("name2"));
        assertThat(argList.getArgs().get(0).getName(), equalTo("name"));

        assertThat(argList.toString(),
                   equalTo(
                         "name = name, value = value, position = 1, type = 1;name = name2, value = value2, position = 2, type = 2"));

        argList = new ArgList();
        assertThat(argList.toString(), equalTo("ArgsList(empty)"));
    }
}
