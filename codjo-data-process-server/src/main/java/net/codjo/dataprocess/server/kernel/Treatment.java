/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.dataprocess.server.kernel;
import net.codjo.dataprocess.common.context.DataProcessContext;
import net.codjo.dataprocess.common.exception.TreatmentException;
import java.sql.SQLException;
/**
 *
 */
interface Treatment {
    public Object proceedTreatment(DataProcessContext context, Object... param)
          throws TreatmentException, SQLException;
}
