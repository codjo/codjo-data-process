/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.dataprocess.common.context;
import org.junit.Test;

import static net.codjo.test.common.matcher.JUnitMatchers.*;
/**
 *
 */
public class DataProcessContextCodecTest {
    @Test
    public void decode() {
        DataProcessContext ctxt
              = DataProcessContextCodec.decode("<DataProcessContext>\n"
                                               + "  <data key=\"youyou\" value=\"200501\\,hip=P_01\"/>\n"
                                               + "  <data key=\"yaya\" value=\"c'est bien\"/>\n"
                                               + "</DataProcessContext>");
        assertThat(ctxt.getProperty("youyou"), equalTo("200501\\,hip=P_01"));
        assertThat(ctxt.getProperty("yaya"), equalTo("c'est bien"));
    }


    @Test
    public void encode() {
        DataProcessContext ctxt = new DataProcessContext();
        ctxt.setProperty("youyou", "200501\\,hip=P_01");
        ctxt.setProperty("portefeuille", "P_01");

        assertThat("<DataProcessContext>\n"
                   + "  <data key=\"portefeuille\" value=\"P_01\"/>\n"
                   + "  <data key=\"youyou\" value=\"200501\\,hip=P_01\"/>\n"
                   + "</DataProcessContext>",
                   equalTo(DataProcessContextCodec.encode(ctxt)));
    }
}
