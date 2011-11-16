package net.codjo.dataprocess.common.userparam;
/**
 *
 */
public class DefaultUserCodec extends UserCodec {
    public DefaultUserCodec() {
        super(new UserXStreamImpl());
    }
}
