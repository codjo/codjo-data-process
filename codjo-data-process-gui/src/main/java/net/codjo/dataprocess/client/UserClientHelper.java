package net.codjo.dataprocess.client;
import net.codjo.dataprocess.common.DataProcessConstants;
import net.codjo.dataprocess.common.exception.UserManagerException;
import net.codjo.dataprocess.common.userparam.DefaultUserCodec;
import net.codjo.dataprocess.common.userparam.User;
import net.codjo.dataprocess.common.userparam.UserCodec;
import net.codjo.mad.client.request.RequestException;
import net.codjo.mad.client.request.Row;
import net.codjo.mad.gui.framework.MutableGuiContext;
import java.util.HashMap;
import java.util.Map;
/**
 *
 */
public class UserClientHelper {
    private UserCodec userCodec;


    public UserClientHelper() {
        this(new DefaultUserCodec());
    }


    public UserClientHelper(UserCodec userCodec) {
        this.userCodec = userCodec;
    }


    public void save(MutableGuiContext ctxt, User user) throws UserManagerException, RequestException {
        Map<String, String> args = new HashMap<String, String>();
        args.put("command", DataProcessConstants.USER_COMMAND_SAVE);
        args.put("userName", user.getUserName());
        args.put("userParam", userCodec.toXml(user));

        HandlerCommandSender sender = new HandlerCommandSender();
        Row row = sender.send(ctxt, args, "userManager");
        String result = row.getFieldValue("result");
        if (result.startsWith("ERROR")) {
            throw new UserManagerException(result);
        }
    }


    public void create(MutableGuiContext ctxt, User user) throws UserManagerException, RequestException {
        Map<String, String> args = new HashMap<String, String>();
        args.put("command", DataProcessConstants.USER_COMMAND_CREATE);
        args.put("userName", user.getUserName());
        args.put("userParam", userCodec.toXml(user));

        HandlerCommandSender sender = new HandlerCommandSender();
        Row row = sender.send(ctxt, args, "userManager");
        String result = row.getFieldValue("result");
        if (result.startsWith("ERROR")) {
            throw new UserManagerException(result);
        }
    }


    public User load(MutableGuiContext ctxt, String userName) throws UserManagerException, RequestException {
        Map<String, String> args = new HashMap<String, String>();
        args.put("command", DataProcessConstants.USER_COMMAND_LOAD);
        args.put("userName", userName);
        args.put("userParam", "");

        HandlerCommandSender sender = new HandlerCommandSender();
        Row row = sender.send(ctxt, args, "userManager");
        String result = row.getFieldValue("result");
        if (result.startsWith("ERROR")) {
            throw new UserManagerException(result);
        }
        User user = userCodec.fromXml(result);
        if (user.getUserName() == null) {
            user.setUserName(userName);
            save(ctxt, user);
        }
        return user;
    }


    public boolean isExist(MutableGuiContext ctxt, String userName)
          throws UserManagerException, RequestException {
        Map<String, String> args = new HashMap<String, String>();
        args.put("command", DataProcessConstants.USER_COMMAND_IS_EXIST);
        args.put("userName", userName);
        args.put("userParam", "");

        HandlerCommandSender sender = new HandlerCommandSender();
        Row row = sender.send(ctxt, args, "userManager");
        String result = row.getFieldValue("result");
        if (result.startsWith("ERROR")) {
            throw new UserManagerException(result);
        }
        return "TRUE".equalsIgnoreCase(result);
    }
}
