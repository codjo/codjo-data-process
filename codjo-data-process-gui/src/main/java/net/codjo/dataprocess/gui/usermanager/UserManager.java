/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.dataprocess.gui.usermanager;
import net.codjo.dataprocess.common.userparam.User;
import net.codjo.dataprocess.common.userparam.UserToXml;
/**
 *
 */
public class UserManager {
    private UserToXml userToXml;


    public UserManager(UserToXml userToXml) {
        this.userToXml = userToXml;
    }


    public String toXml(User user) {
        return userToXml.toXml(user);
    }


    public User fromXml(String xml) {
        return userToXml.fromXml(xml);
    }
}
