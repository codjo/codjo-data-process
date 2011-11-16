package net.codjo.dataprocess.server.dao;
import net.codjo.dataprocess.common.context.DataProcessContext;
import net.codjo.dataprocess.common.model.ExecutionListModel;
import net.codjo.dataprocess.common.model.TreatmentModel;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;

import static net.codjo.dataprocess.common.DataProcessConstants.FAILED;
import static net.codjo.dataprocess.common.DataProcessConstants.FAILED_DEPENDENCY;
import static net.codjo.dataprocess.common.DataProcessConstants.TO_DO;
/**
 *
 */
public class StatusDao {

    /* WATS specific objects.*/
    private Method watsUpdateMethod = null;
    private Object watsSingleton = null;


    public StatusDao() {
        /* WATS specific objects initialization through reflection.*/
        try {
            // dynamically instantiate implementation class
            // If dependency is ok at runtime of the client application, everything will be fine.
            // Otherwise, remain silent !
            Class instantiatiatedClass = Class.forName(
                  "net.codjo.wats.watsservices.dataprocessintegration.WATSIntegrationService");
            Method watsSingletonMethod = instantiatiatedClass.getMethod("getInstance");

            if (watsSingletonMethod != null) {
                watsSingleton = watsSingletonMethod.invoke(instantiatiatedClass);
                if (watsSingleton != null) {
                    watsUpdateMethod = instantiatiatedClass.getMethod(
                          "updateTreatmentStatusWithAnalysisDateOf",
                          java.sql.Connection.class,
                          java.sql.Timestamp.class,
                          int.class,
                          int.class,
                          String.class,
                          boolean.class);
                }
            }
        }
        catch (ClassNotFoundException e) {
            // do nothing
        }
        catch (NoSuchMethodException e) {
            // do nothing
        }
        catch (InvocationTargetException e) {
            // do nothing
        }
        catch (IllegalAccessException e) {
            // do nothing
        }
    }


    public void updateExecutionListStatus(Connection con,
                                          ExecutionListModel executionListModel,
                                          DataProcessContext context,
                                          int newStatus,
                                          boolean forced)
          throws SQLException {
        String updateStatus = "update PM_EXECUTION_LIST_STATUS set STATUS = ?, EXECUTION_DATE = ?"
                              + " where EXECUTION_LIST_ID = ?";
        String periode = context.getProperty("periode");

        int status = getExecutionListStatus(con, executionListModel);

        if (((status != FAILED) && (status != FAILED_DEPENDENCY)) || forced) {
            PreparedStatement stmt = con.prepareStatement(updateStatus);
            try {
                stmt.setInt(1, newStatus);
                if (newStatus == TO_DO) {
                    stmt.setNull(2, Types.TIMESTAMP);
                }
                else {
                    stmt.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
                }
                stmt.setInt(3, executionListModel.getId());
                stmt.execute();

                // should be correctly initializd if everything is ok.
                if (watsUpdateMethod != null) {
                    try {
                        watsUpdateMethod.invoke(watsSingleton,
                                                con,
                                                new Timestamp(System.currentTimeMillis()),
                                                executionListModel.getId(),
                                                newStatus,
                                                periode,
                                                false
                        );
                    }
                    catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            }
            finally {
                stmt.close();
            }
        }
    }


    public void updateTreatmentStatus(Connection con,
                                      ExecutionListModel executionListModel,
                                      TreatmentModel treatmentModel,
                                      DataProcessContext context,
                                      int status) throws SQLException {
        updateTreatmentStatus(con, executionListModel, treatmentModel, context, status, "");
    }


    public void updateTreatmentStatus(Connection con,
                                      ExecutionListModel executionListModel,
                                      TreatmentModel treatmentModel,
                                      DataProcessContext context,
                                      int status,
                                      String errorMessage)
          throws SQLException {
        int nbRows = 0;
        PreparedStatement stmt = con.prepareStatement(
              "update PM_TREATMENT_STATUS set STATUS = ?, EXECUTION_DATE = ?, ERROR_MESSAGE = ?"
              + " where EXECUTION_LIST_ID= ? and TREATMENT_ID = ?");
        try {
            stmt.setInt(1, status);
            if (status == TO_DO) {
                stmt.setNull(2, Types.TIMESTAMP);
            }
            else {
                stmt.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            }
            stmt.setString(3, errorMessage);
            stmt.setInt(4, executionListModel.getId());
            stmt.setString(5, treatmentModel.getId());
            nbRows = stmt.executeUpdate();
        }
        finally {
            stmt.close();
        }
        if (nbRows == 0) {
            stmt = con.prepareStatement(
                  "insert into PM_TREATMENT_STATUS (EXECUTION_LIST_ID, TREATMENT_ID, EXECUTION_DATE, STATUS, ERROR_MESSAGE) "
                  + " values (?, ?, ?, ?, ?) ");
            try {
                stmt.setInt(1, executionListModel.getId());
                stmt.setString(2, treatmentModel.getId());
                if (status == TO_DO) {
                    stmt.setNull(3, Types.TIMESTAMP);
                }
                else {
                    stmt.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
                }
                stmt.setInt(4, status);
                stmt.setString(5, errorMessage);
                stmt.executeUpdate();
            }
            finally {
                stmt.close();
            }
        }

        // should be correctly initializd if everything is ok.
        // this is an ugly call to the same wats method (see above)
        if (watsUpdateMethod != null) {
            try {
                String periode = context.getProperty("periode");
                watsUpdateMethod.invoke(watsSingleton,
                                        con,
                                        new Timestamp(System.currentTimeMillis()),
                                        executionListModel.getId(),
                                        -1,
                                        periode,
                                        true);
            }
            catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }


    public int getExecutionListStatus(Connection con, ExecutionListModel executionListModel)
          throws SQLException {
        PreparedStatement stmt = con.prepareStatement(
              "select STATUS from PM_EXECUTION_LIST_STATUS where EXECUTION_LIST_ID = ?");
        try {
            stmt.setInt(1, executionListModel.getId());
            ResultSet rs = stmt.executeQuery();
            try {
                if (rs.next()) {
                    return rs.getInt("STATUS");
                }
                else {
                    throw new IllegalStateException("La liste de traitements id = "
                                                    + executionListModel.getId()
                                                    + " n'existe pas ! Son statut ne peut donc être récupéré.");
                }
            }
            finally {
                rs.close();
            }
        }
        finally {
            stmt.close();
        }
    }


    public void createDefaultExecutionListStatus(Connection con, int executionListId) throws SQLException {
        PreparedStatement pstmt = con.prepareStatement(
              "insert into PM_EXECUTION_LIST_STATUS (EXECUTION_LIST_ID, STATUS, EXECUTION_DATE)"
              + " values (?, ?, ?)");
        try {
            pstmt.setInt(1, executionListId);
            pstmt.setInt(2, TO_DO);
            pstmt.setTimestamp(3, new Timestamp(0));
            pstmt.executeUpdate();
        }
        finally {
            pstmt.close();
        }
    }
}
