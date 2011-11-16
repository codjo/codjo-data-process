/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.dataprocess.common.codec;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;

import static net.codjo.test.common.matcher.JUnitMatchers.*;
/**
 *
 */
public class MapXmlCodecTest {
    @Test
    public void encodeDecode() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("1", "un");
        map.put("2", "deux");
        map.put("trois", "<3>");
        map.put("quatre 4", "1,2,3");
        String xml = MapXmlCodec.encode(map);

        map = MapXmlCodec.decode(xml);
        assertThat(4, equalTo(map.size()));
        assertThat("deux", equalTo(map.get("2")));
        assertThat("<3>", equalTo(map.get("trois")));
        assertThat("1,2,3", equalTo(map.get("quatre 4")));
        assertThat("un", equalTo(map.get("1")));

        map = MapXmlCodec.decode("<map/>");
        assertThat(0, equalTo(map.size()));

        map.clear();
        assertThat("<map/>", equalTo(MapXmlCodec.encode(map)));
    }
}