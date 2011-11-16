/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.dataprocess.common.userparam;
import net.codjo.dataprocess.common.userparam.User.Repository;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
/**
 *
 */
public class UserXStreamImpl implements UserToXml {
    public String toXml(User user) {
        XStream xstream = new XStream(new DomDriver());
        alias(xstream);
        return xstream.toXML(user);
    }


    public User fromXml(String xml) {
        XStream xstream = new XStream(new DomDriver());
        alias(xstream);
        return (User)xstream.fromXML(xml);
    }


    private static void alias(XStream xstream) {
        xstream.alias("user", User.class);
        xstream.alias("repository", Repository.class);
        xstream.aliasField("repositories", User.class, "repositoryList");
    }
}
