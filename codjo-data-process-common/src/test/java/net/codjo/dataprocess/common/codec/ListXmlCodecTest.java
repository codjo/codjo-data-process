package net.codjo.dataprocess.common.codec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;

import static net.codjo.test.common.matcher.JUnitMatchers.*;
/**
 *
 */
public class ListXmlCodecTest {
    @Test
    public void testEncode() {
        List<String> list = new ArrayList<String>();
        list.add("A1");
        list.add("A2");
        list.add("<>");
        list.add("abcdefghij");
        String str = ListXmlCodec.encode(list);
        assertThat(str, equalTo("<list>\n"
                                + "  <data value=\"A1\"/>\n"
                                + "  <data value=\"A2\"/>\n"
                                + "  <data value=\"&lt;&gt;\"/>\n"
                                + "  <data value=\"abcdefghij\"/>\n"
                                + "</list>"));

        str = ListXmlCodec.encode(Arrays.asList("A1", "A2", "<>", "abcdefghij"));
        assertThat(str, equalTo("<list>\n"
                                + "  <data value=\"A1\"/>\n"
                                + "  <data value=\"A2\"/>\n"
                                + "  <data value=\"&lt;&gt;\"/>\n"
                                + "  <data value=\"abcdefghij\"/>\n"
                                + "</list>"));
    }


    @Test
    public void testDecode() {
        List<String> stringList = ListXmlCodec.decode("<list>\n"
                                                      + "  <data value=\"A1\"/>\n"
                                                      + "  <data value=\"A2\"/>\n"
                                                      + "  <data value=\"&lt;&gt;\"/>\n"
                                                      + "  <data value=\"abcdefghij\"/>\n"
                                                      + "</list>");
        assertThat(stringList.size(), equalTo(4));
        assertThat(stringList.get(0), equalTo("A1"));
        assertThat(stringList.get(1), equalTo("A2"));
        assertThat(stringList.get(2), equalTo("<>"));
        assertThat(stringList.get(3), equalTo("abcdefghij"));
    }
}
