package net.codjo.dataprocess.server.util;
import static net.codjo.test.common.matcher.JUnitMatchers.assertThat;
import static net.codjo.test.common.matcher.JUnitMatchers.equalTo;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
/**
 *
 */
public class VarsCompilerTest {

    @Test
    public void patternOk() {
        String source = "hello $[name], we are $[us]";
        Map<String, String> variables = new HashMap<String, String>();
        variables.put("name", "michel");
        variables.put("us", "ready");
        assertThat("hello michel, we are ready", equalTo(VarsCompiler.compile(source, variables)));
    }


    @Test
    public void patternOkForm2() {
        String source = "hello $[name], we are $[us]";
        assertThat("hello michel, we are ready",
                   equalTo(VarsCompiler.compile(source, new String[]{"name", "us"},
                                                new String[]{"michel", "ready"})));
    }


    @Test
    public void patternOkForm3() {
        String source = "hello $[name], we are $[us]";
        String name = "michel";
        String us = "ready";
        assertThat("hello michel, we are ready",
                   equalTo(VarsCompiler.compile(source, "name", name, "us", us)));
    }


    @Test
    public void error() {
        String source = "hello $[name], we are $[us]";
        Map<String, String> variables = new HashMap<String, String>();
        variables.put("name", "michel");
        try {
            VarsCompiler.compile(source, variables);
        }
        catch (IllegalArgumentException e) {
            assertThat(VarsCompiler.generateUnknowVarMessage("us"), equalTo(e.getMessage()));
        }
    }
}
