package net.codjo.dataprocess.common.util;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import java.util.List;
/**
 *
 */
public class UserLoginTracker {
    private String userName;
    private String repository;
    private String ipaddr;
    private String hostname;
    private String date;


    public UserLoginTracker(String userName,
                            String repository,
                            String ipaddr,
                            String hostname,
                            String date) {
        this.userName = userName;
        this.repository = repository;
        this.ipaddr = ipaddr;
        this.hostname = hostname;
        this.date = date;
    }


    public static String encode(UserLoginTracker userLoginTracker) {
        XStream xstream = new XStream(new DomDriver());
        alias(xstream);
        return xstream.toXML(userLoginTracker);
    }


    public static UserLoginTracker decode(String xml) {
        XStream xstream = new XStream(new DomDriver());
        alias(xstream);
        return (UserLoginTracker)xstream.fromXML(xml);
    }


    public static String encodeList(List<UserLoginTracker> list) {
        XStream xstream = new XStream(new DomDriver());
        UserLoginTrackerList userLoginTrackerList = new UserLoginTrackerList();
        userLoginTrackerList.setList(list);
        aliasList(xstream);
        return xstream.toXML(userLoginTrackerList);
    }


    public static List<UserLoginTracker> decodeList(String xml) {
        XStream xstream = new XStream(new DomDriver());
        aliasList(xstream);
        UserLoginTrackerList userLoginTrackerList = (UserLoginTrackerList)xstream.fromXML(xml);
        return userLoginTrackerList.getList();
    }


    private static void alias(XStream xstream) {
        xstream.alias("UserLoginTracker", UserLoginTracker.class);
    }


    private static void aliasList(XStream xstream) {
        xstream.alias("UserLoginTrackerList", UserLoginTrackerList.class);
        xstream.alias("UserLoginTracker", UserLoginTracker.class);
        xstream.addImplicitCollection(UserLoginTrackerList.class, "list");
    }


    public String getUserName() {
        return userName;
    }


    public String getRepository() {
        return repository;
    }


    public String getIpaddr() {
        return ipaddr;
    }


    public String getHostname() {
        return hostname;
    }


    public String getDate() {
        return date;
    }


    public void setDate(String date) {
        this.date = date;
    }


    private static class UserLoginTrackerList {
        private List<UserLoginTracker> list;


        public void setList(List<UserLoginTracker> userLoginTrackerList) {
            list = userLoginTrackerList;
        }


        public List<UserLoginTracker> getList() {
            return list;
        }
    }
}
