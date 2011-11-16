/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.dataprocess.gui.usermanager;
import net.codjo.dataprocess.common.userparam.UserXStreamImpl;
/**
 *
 */
public class DefaultUserManager extends UserManager {
    public DefaultUserManager() {
        super(new UserXStreamImpl());
    }
}
