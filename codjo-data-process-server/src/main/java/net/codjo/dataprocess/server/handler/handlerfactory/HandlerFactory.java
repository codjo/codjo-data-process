package net.codjo.dataprocess.server.handler.handlerfactory;
import net.codjo.mad.server.handler.Handler;
/**
 *
 */
public interface HandlerFactory {
    Handler getHandler();


    String getHandlerId();


    Handler createHandler();
}
