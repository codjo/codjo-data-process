/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.dataprocess.common.codec;
import static net.codjo.test.common.matcher.JUnitMatchers.assertThat;
import static net.codjo.test.common.matcher.JUnitMatchers.equalTo;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
/**
 *
 */
public class ListCodecTest {
    private ListCodec listCodec;


    @Test
    public void encode() {
        List<String> list = new ArrayList<String>();
        list.add("toto");
        list.add("titi");
        list.add("tata");
        list.add("tutu");
        assertThat(listCodec.encode(list), equalTo("'toto','titi','tata','tutu'"));
        assertThat(listCodec.encode(list, "\"", ",,,"), equalTo("\"toto\",,,\"titi\",,,\"tata\",,,\"tutu\""));
        assertThat(listCodec.encode(list, "", ","), equalTo("toto,titi,tata,tutu"));
        assertThat(listCodec.encode(list, new ListCodec.Quote("[", "]"), ","),
                   equalTo("[toto],[titi],[tata],[tutu]"));
        assertThat(listCodec.encode(list, new ListCodec.Quote("[<", ">]"), ",,"),
                   equalTo("[<toto>],,[<titi>],,[<tata>],,[<tutu>]"));

        list = new ArrayList<String>();
        assertThat(listCodec.encode(list), equalTo(""));
    }


    @Test
    public void decode() {
        List<String> list = listCodec.decode("'to, to','titi','tata','tu, tu, va'");
        assertThat(list.toString(), equalTo("[to, to, titi, tata, tu, tu, va]"));
        assertThat(list.size(), equalTo(4));

        list = listCodec.decode("'to, to' ,    '  titi', 'tata',    '   tu, tu, va'   ");
        assertThat(list.toString(), equalTo("[to, to,   titi, tata,    tu, tu, va]"));
        assertThat(list.size(), equalTo(4));
        assertThat(list.get(3), equalTo("   tu, tu, va"));

        listCodec.setTrim(true);
        list = listCodec.decode("'to, to' ,    '  titi', 'tata',    '   tu, tu, va'   ");
        assertThat(list.toString(), equalTo("[to, to, titi, tata, tu, tu, va]"));
        assertThat(list.size(), equalTo(4));
        assertThat(list.get(3), equalTo("tu, tu, va"));
        listCodec.setTrim(false);

        list = listCodec.decode("\"toto\",,,  \"titi\"  ,,,   \"tata\",,,  \"  tutu\"",
                                "\"", ",,,");
        assertThat(list.toString(), equalTo("[toto, titi, tata,   tutu]"));
        list = listCodec.decode("");
        assertThat(0, equalTo(list.size()));
        assertThat(list.toString(), equalTo("[]"));

        list = listCodec.decode("XXtotoXX,,,  XXtitiXX  ,,,   XXtataXX,,,  XX  tutuXX",
                                "XX", ",,,");
        assertThat(list.toString(), equalTo("[toto, titi, tata,   tutu]"));
        list = listCodec.decode("");
        assertThat(0, equalTo(list.size()));
        assertThat(list.toString(), equalTo("[]"));

        list = listCodec.decode(null);
        assertThat(0, equalTo(list.size()));

        list = listCodec.decode("to, to,    titi,tata,tu, tu, va", "", ",");
        assertThat(list.toString(), equalTo("[to, to, titi, tata, tu, tu, va]"));
        assertThat(list.size(), equalTo(7));

        list = listCodec.decode("[[toto]],,,  [[titi]]  ,,,   [[tata]],,,  [[  tutu]]",
                                new ListCodec.Quote("[[", "]]"), ",,,");
        assertThat(list.toString(), equalTo("[toto, titi, tata,   tutu]"));
    }


    @Test
    public void removeQuote() {
        assertThat("toto", equalTo(listCodec.removeQuote("'toto'")));
    }


    @Before
    public void before() {
        listCodec = new ListCodec();
    }
}
