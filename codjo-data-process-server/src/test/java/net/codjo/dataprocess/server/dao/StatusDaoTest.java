package net.codjo.dataprocess.server.dao;
import net.codjo.database.common.api.JdbcFixture;
import net.codjo.datagen.DatagenFixture;
import net.codjo.dataprocess.common.DataProcessConstants;
import net.codjo.dataprocess.common.context.DataProcessContext;
import net.codjo.dataprocess.common.model.ExecutionListModel;
import net.codjo.dataprocess.common.model.TreatmentModel;
import net.codjo.dataprocess.server.util.TestUtils;
import net.codjo.test.common.fixture.CompositeFixture;
import net.codjo.tokio.TokioFixture;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static net.codjo.test.common.matcher.JUnitMatchers.*;
/**
 *
 */
public class StatusDaoTest {
    private static final TokioFixture TOKIO = new TokioFixture(StatusDaoTest.class);
    private static final DatagenFixture DATAGEN = new DatagenFixture(StatusDaoTest.class);
    private static final CompositeFixture COMPOSITE_FIXTURE = new CompositeFixture(TOKIO, DATAGEN);


    @BeforeClass
    public static void beforeClass() throws Exception {
        COMPOSITE_FIXTURE.doSetUp();
        final JdbcFixture jdbcFixture = TOKIO.getJdbcFixture();
        jdbcFixture.advanced().dropAllObjects();

        try {
            DATAGEN.generate();
            TestUtils.initScript(jdbcFixture, DATAGEN, "PM_TREATMENT_STATUS.tab");
            TestUtils.initScript(jdbcFixture, DATAGEN, "PM_EXECUTION_LIST_STATUS.tab");
        }
        catch (Exception ex) {
            COMPOSITE_FIXTURE.doTearDown();
            fail(ex.getLocalizedMessage());
        }
    }


    @AfterClass
    public static void afterClass() throws Exception {
        COMPOSITE_FIXTURE.doTearDown();
    }


    @Test
    public void fillExecutionListStatus() throws Exception {
        DataProcessContext context = new DataProcessContext();
        TOKIO.executeQuery("delete PM_EXECUTION_LIST_STATUS");
        Connection con = TOKIO.getConnection();
        StatusDao statusDao = new StatusDao();

        ExecutionListModel executionListModel1 = buildModel(con, 1, DataProcessConstants.TO_DO);
        statusDao.updateExecutionListStatus(con, executionListModel1, context,
                                            DataProcessConstants.FAILED, true);
        assertThat(DataProcessConstants.FAILED,
                   equalTo(statusDao.getExecutionListStatus(con, executionListModel1)));

        ExecutionListModel executionListModel2 = buildModel(con, 2, DataProcessConstants.TO_DO);
        statusDao.updateExecutionListStatus(con, executionListModel2, context,
                                            DataProcessConstants.FAILED, false);
        assertThat(DataProcessConstants.FAILED,
                   equalTo(statusDao.getExecutionListStatus(con, executionListModel2)));

        ExecutionListModel executionListModel3 = buildModel(con, 3, DataProcessConstants.DONE);
        statusDao.updateExecutionListStatus(con, executionListModel3, context,
                                            DataProcessConstants.FAILED, true);
        assertThat(DataProcessConstants.FAILED,
                   equalTo(statusDao.getExecutionListStatus(con, executionListModel3)));

        ExecutionListModel executionListModel4 = buildModel(con, 4, DataProcessConstants.DONE);
        statusDao.updateExecutionListStatus(con, executionListModel4, context,
                                            DataProcessConstants.FAILED, false);
        assertThat(DataProcessConstants.FAILED,
                   equalTo(statusDao.getExecutionListStatus(con, executionListModel4)));

        ExecutionListModel executionListModel5 = buildModel(con, 5, DataProcessConstants.FAILED);
        statusDao.updateExecutionListStatus(con, executionListModel5, context,
                                            DataProcessConstants.DONE, true);
        assertThat(DataProcessConstants.DONE,
                   equalTo(statusDao.getExecutionListStatus(con, executionListModel5)));

        ExecutionListModel executionListModel6 = buildModel(con, 6, DataProcessConstants.FAILED);
        statusDao.updateExecutionListStatus(con, executionListModel6, context,
                                            DataProcessConstants.DONE, false);
        assertThat(DataProcessConstants.FAILED,
                   equalTo(statusDao.getExecutionListStatus(con, executionListModel6)));
    }


    @Test
    public void fillTreatmentStatus() throws Exception {
        TOKIO.insertInputInDb("STATUS");
        TreatmentModel treatmentModel = new TreatmentModel();
        treatmentModel.setId("MON_TRAITEMENT");

        ExecutionListModel execListModel = buildModel(TOKIO.getConnection(), 15, DataProcessConstants.TO_DO);
        ExecutionListModel execListModel2 = buildModel(TOKIO.getConnection(), 44, DataProcessConstants.TO_DO);
        ExecutionListModel execListModel3 = buildModel(TOKIO.getConnection(), 98, DataProcessConstants.TO_DO);

        DataProcessContext context = new DataProcessContext();
        StatusDao statusDao = new StatusDao();
        statusDao.updateTreatmentStatus(TOKIO.getConnection(), execListModel, treatmentModel, context,
                                        DataProcessConstants.TO_DO, "Tout est OK");
        statusDao.updateTreatmentStatus(TOKIO.getConnection(), execListModel2, treatmentModel, context,
                                        DataProcessConstants.DONE, "Miam les pommes");
        statusDao.updateTreatmentStatus(TOKIO.getConnection(), execListModel3, treatmentModel, context,
                                        DataProcessConstants.DONE, "Miam les poires");

        TOKIO.assertAllOutputs("STATUS");
    }


    private static ExecutionListModel buildModel(Connection con, int id, int status) throws SQLException {
        ExecutionListModel trtMod = new ExecutionListModel();
        trtMod.setId(id);
        trtMod.setStatus(status);
        PreparedStatement stmt = con.prepareStatement("delete PM_EXECUTION_LIST_STATUS "
                                                      + "insert into PM_EXECUTION_LIST_STATUS (EXECUTION_LIST_ID, STATUS) values (?, ?)");
        try {
            stmt.setInt(1, id);
            stmt.setInt(2, status);
            stmt.execute();
            return trtMod;
        }
        finally {
            stmt.close();
        }
    }
}
