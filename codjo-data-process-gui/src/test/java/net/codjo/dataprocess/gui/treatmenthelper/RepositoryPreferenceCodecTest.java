package net.codjo.dataprocess.gui.treatmenthelper;
import static net.codjo.test.common.matcher.JUnitMatchers.*;
import org.junit.Test;
/**
 *
 */
public class RepositoryPreferenceCodecTest {
    @Test
    public void toXml() {
        RepositoryPreference preference = new RepositoryPreference();

        preference.addRepository("HB2");
        preference.addRepositoryPath("HB2", "c:\\dev\\projects\\scop\\repo1.xml");
        preference.addRepositoryPath("HB2", "c:\\dev\\projects\\scop\\repo2.xml");
        preference.addRepositoryPath("HB2", "c:\\dev\\projects\\scop\\repo3.xml");

        preference.addRepository("Export");
        preference.addRepositoryPath("Export", "c:\\dev\\projects\\creo\\repo1.xml");
        preference.addRepositoryPath("Export", "c:\\dev\\projects\\creo\\repo2.xml");
        preference.addRepositoryPath("Export", "c:\\dev\\projects\\creo\\repo3.xml");

        String result = RepositoryPreferenceCodec.toXml(preference);
        assertThat("<preferences>\n"
                   + "  <repositoryMap>\n"
                   + "    <entry>\n"
                   + "      <string>HB2</string>\n"
                   + "      <list>\n"
                   + "        <string>c:\\dev\\projects\\scop\\repo1.xml</string>\n"
                   + "        <string>c:\\dev\\projects\\scop\\repo2.xml</string>\n"
                   + "        <string>c:\\dev\\projects\\scop\\repo3.xml</string>\n"
                   + "      </list>\n"
                   + "    </entry>\n"
                   + "    <entry>\n"
                   + "      <string>Export</string>\n"
                   + "      <list>\n"
                   + "        <string>c:\\dev\\projects\\creo\\repo1.xml</string>\n"
                   + "        <string>c:\\dev\\projects\\creo\\repo2.xml</string>\n"
                   + "        <string>c:\\dev\\projects\\creo\\repo3.xml</string>\n"
                   + "      </list>\n"
                   + "    </entry>\n"
                   + "  </repositoryMap>\n"
                   + "</preferences>", equalTo(result));
    }


    @Test
    public void fromXml() {
        String input = "<preferences>\n"
                       + "  <repositoryMap>\n"
                       + "    <entry>\n"
                       + "      <string>HB2</string>\n"
                       + "      <list>\n"
                       + "        <string>c:\\dev\\projects\\scop\\repo1.xml</string>\n"
                       + "        <string>c:\\dev\\projects\\scop\\repo2.xml</string>\n"
                       + "      </list>\n"
                       + "    </entry>\n"
                       + "    <entry>\n"
                       + "      <string>Export</string>\n"
                       + "      <list>\n"
                       + "        <string>c:\\dev\\projects\\creo\\repo1.xml</string>\n"
                       + "        <string>c:\\dev\\projects\\creo\\repo2.xml</string>\n"
                       + "        <string>c:\\dev\\projects\\creo\\repo3.xml</string>\n"
                       + "      </list>\n"
                       + "    </entry>\n"
                       + "  </repositoryMap>\n"
                       + "</preferences>";
        RepositoryPreference preference = RepositoryPreferenceCodec.fromXml(input);
        assertThat(preference.getRepositoryPath("HB2").size(), equalTo(2));
        assertThat(preference.getRepositoryPath("Export").contains("c:\\dev\\projects\\creo\\repo3.xml"),
                   equalTo(true));
    }
}
