/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.dataprocess.server.kernel;
import net.codjo.dataprocess.common.exception.TreatmentException;
import java.sql.Connection;
import java.util.List;
/**
 *
 */
public interface ArgumentModifier {
    String proceed(Connection con, List<String> parameters) throws TreatmentException;
}
