package net.codjo.dataprocess.common.userparam;
/**
 *
 */
public class UserCodec {
    private UserToXml userToXml;


    public UserCodec(UserToXml userToXml) {
        this.userToXml = userToXml;
    }


    public String toXml(User user) {
        return userToXml.toXml(user);
    }


    public User fromXml(String xml) {
        return userToXml.fromXml(xml);
    }
}
