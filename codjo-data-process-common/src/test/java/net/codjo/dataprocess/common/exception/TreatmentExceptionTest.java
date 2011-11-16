package net.codjo.dataprocess.common.exception;
import static net.codjo.test.common.matcher.JUnitMatchers.assertThat;
import static net.codjo.test.common.matcher.JUnitMatchers.equalTo;
import java.sql.SQLException;
import org.junit.Test;
/**
 *
 */
public class TreatmentExceptionTest {
    @Test
    public void testTreatmentException() throws Exception {
        TreatmentException exception = new TreatmentException("mon message d'erreur");
        try {
            throw exception;
        }
        catch (TreatmentException ex) {
            assertThat(exception.getLocalizedMessage(), equalTo("mon message d'erreur"));
        }

        exception = new TreatmentException(new SQLException("mon message d'erreur SQL"));
        try {
            throw exception;
        }
        catch (TreatmentException ex) {
            assertThat(exception.getLocalizedMessage(), equalTo(
                  "java.sql.SQLException: mon message d'erreur SQL, causée par: mon message d'erreur SQL"));
        }

        exception = new TreatmentException("message d'erreur", new SQLException("mon message d'erreur SQL"));
        try {
            throw exception;
        }
        catch (TreatmentException ex) {
            assertThat(exception.getLocalizedMessage(),
                       equalTo("message d'erreur, causée par: mon message d'erreur SQL"));
        }
    }
}
