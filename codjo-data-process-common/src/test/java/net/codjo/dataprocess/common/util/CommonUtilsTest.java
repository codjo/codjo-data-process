/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.dataprocess.common.util;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

import static net.codjo.test.common.matcher.JUnitMatchers.*;
/**
 *
 */
public class CommonUtilsTest {

    @Test
    public void getParameters() {
        assertThat(CommonUtils.stringToList("scop,    toto,    tit i").toString(),
                   equalTo("[scop, toto, tit i]"));
        assertThat(CommonUtils.stringToList("").toString(), equalTo("[]"));
    }


    @Test
    public void listToString() {
        List<String> list = new ArrayList<String>();
        list.add("toto");
        list.add("tata");
        list.add("titi");
        assertThat(CommonUtils.listToString(list), equalTo("toto, tata, titi"));
        list.clear();
        assertThat(CommonUtils.listToString(list), equalTo(""));
    }


    @Test
    public void timeMillisToString() {
        assertThat(CommonUtils.timeMillisToString(86400000 + 5000), equalTo("1j 5s"));
        assertThat(CommonUtils.timeMillisToString(86400000 + 999), equalTo("1j"));
        assertThat(CommonUtils.timeMillisToString(86400000 + 1000), equalTo("1j 1s"));
        assertThat(CommonUtils.timeMillisToString(86400000 + 900000), equalTo("1j 15mn"));
        assertThat(CommonUtils.timeMillisToString(86400000 + 900000 + 15000), equalTo("1j 15mn 15s"));
        assertThat(CommonUtils.timeMillisToString(86400000 + 18000000), equalTo("1j 5h"));
        assertThat(CommonUtils.timeMillisToString(666), equalTo("666ms"));
    }


    @Test
    public void doubleQuote() {
        String result;
        result = CommonUtils.doubleQuote("ta'ta");
        assertThat(result, equalTo("ta''ta"));

        result = CommonUtils.doubleQuote("ta''ta");
        assertThat(result, equalTo("ta''''ta"));

        result = CommonUtils.doubleQuote("ta'''ta");
        assertThat(result, equalTo("ta''''''ta"));

        result = CommonUtils.doubleQuote("tata");
        assertThat(result, equalTo("tata"));

        result = CommonUtils.doubleQuote("");
        assertThat(result, equalTo(""));
    }


    @Test
    public void localify() {
        assertThat(CommonUtils.localify(1, "list1", "val"), equalTo("1.list1.val"));
    }
}
