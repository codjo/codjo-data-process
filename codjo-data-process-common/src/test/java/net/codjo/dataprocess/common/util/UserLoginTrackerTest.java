package net.codjo.dataprocess.common.util;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

import static net.codjo.test.common.matcher.JUnitMatchers.*;
/**
 *
 */
public class UserLoginTrackerTest {
    @Test
    public void testEncode() throws Exception {
        UserLoginTracker userLoginTracker = new UserLoginTracker("michel",
                                                                 "HB2",
                                                                 "192.168.1.1",
                                                                 "localhost",
                                                                 "2012");
        assertThat(UserLoginTracker.encode(userLoginTracker),
                   equalTo(
                         "<UserLoginTracker>\n"
                         + "  <userName>michel</userName>\n"
                         + "  <repository>HB2</repository>\n"
                         + "  <ipaddr>192.168.1.1</ipaddr>\n"
                         + "  <hostname>localhost</hostname>\n"
                         + "  <date>2012</date>\n"
                         + "</UserLoginTracker>"));
    }


    @Test
    public void testEncodeList() throws Exception {
        List<UserLoginTracker> list = new ArrayList<UserLoginTracker>();
        list.add(new UserLoginTracker("michel", "HB2", "192.168.1.1", "localhost", "2012"));
        list.add(new UserLoginTracker("mimi", "HB3", "192.168.1.2", "localhost2", "2013"));
        assertThat(UserLoginTracker.encodeList(list),
                   equalTo(
                         "<UserLoginTrackerList>\n"
                         + "  <UserLoginTracker>\n"
                         + "    <userName>michel</userName>\n"
                         + "    <repository>HB2</repository>\n"
                         + "    <ipaddr>192.168.1.1</ipaddr>\n"
                         + "    <hostname>localhost</hostname>\n"
                         + "    <date>2012</date>\n"
                         + "  </UserLoginTracker>\n"
                         + "  <UserLoginTracker>\n"
                         + "    <userName>mimi</userName>\n"
                         + "    <repository>HB3</repository>\n"
                         + "    <ipaddr>192.168.1.2</ipaddr>\n"
                         + "    <hostname>localhost2</hostname>\n"
                         + "    <date>2013</date>\n"
                         + "  </UserLoginTracker>\n"
                         + "</UserLoginTrackerList>"));
    }


    @Test
    public void testDecode() throws Exception {
        UserLoginTracker userLoginTracker = new UserLoginTracker("michel",
                                                                 "HB2",
                                                                 "192.168.1.1",
                                                                 "localhost",
                                                                 "2012");
        String xml = "<UserLoginTracker>\n"
                     + "  <userName>michel</userName>\n"
                     + "  <repository>HB2</repository>\n"
                     + "  <ipaddr>192.168.1.1</ipaddr>\n"
                     + "  <hostname>localhost</hostname>\n"
                     + "  <date>2012</date>\n"
                     + "</UserLoginTracker>";
        UserLoginTracker actual = UserLoginTracker.decode(xml);
        assertThat(actual.getDate(), equalTo(userLoginTracker.getDate()));
        assertThat(actual.getHostname(), equalTo(userLoginTracker.getHostname()));
        assertThat(actual.getIpaddr(), equalTo(userLoginTracker.getIpaddr()));
        assertThat(actual.getRepository(), equalTo(userLoginTracker.getRepository()));
        assertThat(actual.getUserName(), equalTo(userLoginTracker.getUserName()));
    }


    @Test
    public void testDecodeList() throws Exception {
        String xml = "<UserLoginTrackerList>\n"
                     + "  <UserLoginTracker>\n"
                     + "    <userName>michel</userName>\n"
                     + "    <repository>HB2</repository>\n"
                     + "    <ipaddr>192.168.1.1</ipaddr>\n"
                     + "    <hostname>localhost</hostname>\n"
                     + "    <date>2012</date>\n"
                     + "  </UserLoginTracker>\n"
                     + "  <UserLoginTracker>\n"
                     + "    <userName>mimi</userName>\n"
                     + "    <repository>HB3</repository>\n"
                     + "    <ipaddr>192.168.1.2</ipaddr>\n"
                     + "    <hostname>localhost2</hostname>\n"
                     + "    <date>2013</date>\n"
                     + "  </UserLoginTracker>\n"
                     + "</UserLoginTrackerList>";
        UserLoginTracker userLoginTracker1 = new UserLoginTracker("michel",
                                                                  "HB2",
                                                                  "192.168.1.1",
                                                                  "localhost",
                                                                  "2012");
        UserLoginTracker userLoginTracker2 = new UserLoginTracker("mimi",
                                                                  "HB3",
                                                                  "192.168.1.2",
                                                                  "localhost2",
                                                                  "2013");

        List<UserLoginTracker> list = UserLoginTracker.decodeList(xml);
        UserLoginTracker actual1 = list.get(0);
        UserLoginTracker actual2 = list.get(1);

        assertThat(actual1.getDate(), equalTo(userLoginTracker1.getDate()));
        assertThat(actual1.getHostname(), equalTo(userLoginTracker1.getHostname()));
        assertThat(actual1.getIpaddr(), equalTo(userLoginTracker1.getIpaddr()));
        assertThat(actual1.getRepository(), equalTo(userLoginTracker1.getRepository()));
        assertThat(actual1.getUserName(), equalTo(userLoginTracker1.getUserName()));

        assertThat(actual2.getDate(), equalTo(userLoginTracker2.getDate()));
        assertThat(actual2.getHostname(), equalTo(userLoginTracker2.getHostname()));
        assertThat(actual2.getIpaddr(), equalTo(userLoginTracker2.getIpaddr()));
        assertThat(actual2.getRepository(), equalTo(userLoginTracker2.getRepository()));
        assertThat(actual2.getUserName(), equalTo(userLoginTracker2.getUserName()));
    }
}
