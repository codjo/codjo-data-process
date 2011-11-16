/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.dataprocess.common;
import org.apache.log4j.Logger;
/**
 *
 */
public final class Log {
    private static final Logger LOG = Logger.getLogger(Log.class);


    private Log() {
    }


    private static String getLoggerName(Class logger) {
        return logger.getName() + " : ";
    }


    public static void debug(Class logger, Object msg) {
        LOG.debug(getLoggerName(logger) + msg);
    }


    public static void debug(Class logger, Object msg, Throwable error) {
        LOG.debug(getLoggerName(logger) + msg, error);
    }


    public static void error(Class logger, Object msg) {
        LOG.error(getLoggerName(logger) + msg);
    }


    public static void error(Class logger, Object msg, Throwable error) {
        LOG.error(getLoggerName(logger) + msg, error);
    }


    public static void fatal(Class logger, Object msg) {
        LOG.fatal(getLoggerName(logger) + msg);
    }


    public static void fatal(Class logger, Object msg, Throwable error) {
        LOG.fatal(getLoggerName(logger) + msg, error);
    }


    public static void info(Class logger, Object msg) {
        LOG.info(getLoggerName(logger) + msg);
    }


    public static void info(Class logger, Object msg, Throwable error) {
        LOG.info(getLoggerName(logger) + msg, error);
    }


    public static void warn(Class logger, Object msg) {
        LOG.warn(getLoggerName(logger) + msg);
    }


    public static void warn(Class logger, Object msg, Throwable error) {
        LOG.warn(getLoggerName(logger) + msg, error);
    }


    public static boolean isDebugEnabled() {
        return LOG.isDebugEnabled();
    }


    public static boolean isInfoEnabled() {
        return LOG.isInfoEnabled();
    }
}
