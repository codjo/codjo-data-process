package net.codjo.dataprocess.common.userparam;
import net.codjo.dataprocess.common.userparam.User.Repository;
import org.junit.Test;

import static net.codjo.test.common.matcher.JUnitMatchers.*;
/**
 *
 */
public class DefaultUserCodecTest {
    private UserCodec userCodec = new DefaultUserCodec();


    @Test
    public void toXml() throws Exception {
        User user = new User("CREO_USER");
        user.setCurrentRepository("HB2");
        user.addRepository(new Repository("HB3", "23112009", "45"));
        user.addRepository(new Repository("HB4", "05122008", "90"));

        String result =
              "<user>\n"
              + "  <userName>CREO_USER</userName>\n"
              + "  <currentRepository>HB2</currentRepository>\n"
              + "  <repositories>\n"
              + "    <repository>\n"
              + "      <name>HB3</name>\n"
              + "      <expirydate>23112009</expirydate>\n"
              + "      <expiryday>45</expiryday>\n"
              + "    </repository>\n"
              + "    <repository>\n"
              + "      <name>HB4</name>\n"
              + "      <expirydate>05122008</expirydate>\n"
              + "      <expiryday>90</expiryday>\n"
              + "    </repository>\n"
              + "  </repositories>\n"
              + "</user>";
        assertThat(result, equalTo(userCodec.toXml(user)));
    }


    @Test
    public void fromXml() throws Exception {
        String xml =
              "<user>\n"
              + "  <userName>CREO_USER</userName>\n"
              + "  <currentRepository>HB2</currentRepository>\n"
              + "  <repositories>\n"
              + "    <repository>\n"
              + "      <name>HB3</name>\n"
              + "      <expirydate>23112009</expirydate>\n"
              + "      <expiryday>45</expiryday>\n"
              + "    </repository>\n"
              + "    <repository>\n"
              + "      <name>HB4</name>\n"
              + "      <expirydate>05122008</expirydate>\n"
              + "      <expiryday>90</expiryday>\n"
              + "    </repository>\n"
              + "  </repositories>\n"
              + "</user>";
        User user = userCodec.fromXml(xml);
        assertThat(user.getCurrentRepository(), equalTo("HB2"));
        assertThat(user.getUserName(), equalTo("CREO_USER"));
        assertThat(user.getRepositoryList().size(), equalTo(2));
        assertThat(user.getRepositoryList().get(0).getName(), equalTo("HB3"));
        assertThat(user.getRepositoryList().get(1).getName(), equalTo("HB4"));
        assertThat(user.getRepositoryList().get(0).getExpiryday(), equalTo("45"));
        assertThat(user.getRepositoryList().get(1).getExpiryday(), equalTo("90"));
    }
}
