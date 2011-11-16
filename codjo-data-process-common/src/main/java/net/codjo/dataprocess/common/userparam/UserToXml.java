/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.dataprocess.common.userparam;
/**
 *
 */
public interface UserToXml {
    String toXml(User user);


    User fromXml(String xml);
}
