/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.dataprocess.common.userparam;
import net.codjo.dataprocess.common.userparam.User.Repository;
import org.junit.Test;

import static net.codjo.test.common.matcher.JUnitMatchers.*;
/**
 *
 */
public class UserXStreamImplTest {
    private String xmlOld =
          "<user>\n"
          + "  <userName>CREO_USER</userName>\n"
          + "  <currentRepository>HB2</currentRepository>\n"
          + "  <repositoryList>\n"
          + "    <string>c60</string>\n"
          + "    <string>c68</string>\n"
          + "  </repositoryList>\n"
          + "</user>";

    private String xml =
          "<user>\n"
          + "  <userName>CREO_USER</userName>\n"
          + "  <currentRepository>HB2</currentRepository>\n"
          + "  <repositories>\n"
          + "    <repository>\n"
          + "      <name>c60</name>\n"
          + "      <expirydate>10-10-2001</expirydate>\n"
          + "      <expiryday>45</expiryday>\n"
          + "    </repository>\n"
          + "    <repository>\n"
          + "      <name>c68</name>\n"
          + "      <expirydate>18/09/2005</expirydate>\n"
          + "      <expiryday>90</expiryday>\n"
          + "    </repository>\n"
          + "  </repositories>\n"
          + "</user>";


    @Test
    public void toXml() {
        User user = new User("CREO_USER");
        user.setCurrentRepository("HB2");
        user.addRepository(new Repository("c60", "10-10-2001", "45"));
        user.addRepository(new Repository("c68", "18/09/2005", "90"));

        String result = new UserXStreamImpl().toXml(user);
        assertThat(result, equalTo(xml));
    }


    @Test
    public void fromXml() {
        User user = new UserXStreamImpl().fromXml(xml);
        assertThat(user.toString(),
                   equalTo(
                         "userName = CREO_USER,"
                         + " currentRepository = HB2,"
                         + " repositoryList = [[name = c60, expirydate = 10-10-2001, expiryday = 45], [name = c68, expirydate = 18/09/2005, expiryday = 90]]"));
    }
}
