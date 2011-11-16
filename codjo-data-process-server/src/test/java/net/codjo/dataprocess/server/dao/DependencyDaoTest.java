package net.codjo.dataprocess.server.dao;
import net.codjo.database.common.api.JdbcFixture;
import net.codjo.datagen.DatagenFixture;
import net.codjo.dataprocess.server.util.TestUtils;
import net.codjo.test.common.fixture.CompositeFixture;
import net.codjo.tokio.TokioFixture;
import java.sql.SQLException;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static net.codjo.test.common.matcher.JUnitMatchers.*;
/**
 *
 */
public class DependencyDaoTest {
    private ExecutionListDependency executionListDependency;
    private static final TokioFixture TOKIO = new TokioFixture(DependencyDaoTest.class);
    private static final DatagenFixture DATAGEN = new DatagenFixture(DependencyDaoTest.class);
    private static final CompositeFixture COMPOSITE_FIXTURE = new CompositeFixture(TOKIO, DATAGEN);


    @BeforeClass
    public static void beforeClass() throws Exception {
        COMPOSITE_FIXTURE.doSetUp();
        JdbcFixture jdbcFixture = TOKIO.getJdbcFixture();
        jdbcFixture.advanced().dropAllObjects();

        try {
            DATAGEN.generate();
            TestUtils.initScript(jdbcFixture, DATAGEN, "PM_DEPENDENCY.tab");
            TestUtils.initScript(jdbcFixture, DATAGEN, "PM_EXECUTION_LIST.tab");
            TestUtils.initScript(jdbcFixture, DATAGEN, "PM_EXECUTION_LIST_STATUS.tab");
        }
        catch (Exception ex) {
            COMPOSITE_FIXTURE.doTearDown();
            fail(ex.getLocalizedMessage());
        }
    }


    @Before
    public void before() {
        executionListDependency = new ExecutionListDependency();
    }


    @AfterClass
    public static void afterClass() throws Exception {
        COMPOSITE_FIXTURE.doTearDown();
    }


    @Test
    public void findImplication() throws Exception {
        TOKIO.insertInputInDb("PAS_DE_CYCLE");
        ExecutionListDependency.DependencyResult dependencyResult =
              executionListDependency.findImplication(TOKIO.getConnection(), 55, "text 1");
        assertThat(dependencyResult.getExecutionList().toString(),
                   equalTo("[text 2, text 8, l'état 10, text 22, text 9, text 5, text 3, text 4]"));
        assertThat(dependencyResult.isCycle(), equalTo(false));

        TOKIO.insertInputInDb("CYCLE");
        dependencyResult = executionListDependency.findImplication(TOKIO.getConnection(), 55, "text 1");
        assertThat(dependencyResult.getExecutionList().toString(),
                   equalTo("[text 2, text 8, l'état 10, text 22, text 9, text 5, text 3, text 4]"));
        assertThat(dependencyResult.isCycle(), equalTo(true));

        TOKIO.insertInputInDb("PAS_DE_CYCLE");

        DependencyDao dependencyDao = new DependencyDao();
        String result = dependencyDao.findImplication(TOKIO.getConnection(), 55, "text 1");
        assertThat(result, equalTo("text 2,text 8,l'état 10,text 22,text 9,text 5,text 3,text 4:false"));

        TOKIO.insertInputInDb("CYCLE");
        result = dependencyDao.findImplication(TOKIO.getConnection(), 55, "text 1");
        assertThat(result, equalTo("text 2,text 8,l'état 10,text 22,text 9,text 5,text 3,text 4:true"));
    }


    @Test
    public void findDependency() throws Exception {
        TOKIO.insertInputInDb("PAS_DE_CYCLE");
        ExecutionListDependency.DependencyResult dependencyResult =
              executionListDependency.findDependency(TOKIO.getConnection(), 55, "text 22");
        assertThat(dependencyResult.getExecutionList().toString(),
                   equalTo("[l'état 10, text 8, text 2, text 1]"));
        assertThat(dependencyResult.isCycle(), equalTo(false));

        TOKIO.insertInputInDb("CYCLE");
        dependencyResult = executionListDependency.findDependency(TOKIO.getConnection(), 55, "text 22");
        assertThat(dependencyResult.getExecutionList().toString(),
                   equalTo("[l'état 10, text 8, text 2, text 1, text 9]"));
        assertThat(dependencyResult.isCycle(), equalTo(true));

        TOKIO.insertInputInDb("PAS_DE_CYCLE");

        DependencyDao dependencyDao = new DependencyDao();
        String result = dependencyDao.findDependency(TOKIO.getConnection(), 55, "text 22");
        assertThat(result, equalTo("l'état 10,text 8,text 2,text 1:false"));

        TOKIO.insertInputInDb("CYCLE");
        result = dependencyDao.findDependency(TOKIO.getConnection(), 55, "text 22");
        assertThat(result, equalTo("l'état 10,text 8,text 2,text 1,text 9:true"));
    }


    @Test
    public void isDependOf() throws SQLException {
        TOKIO.insertInputInDb("PAS_DE_CYCLE");
        assertThat(executionListDependency.isDependOf(TOKIO.getConnection(), 55, "text 8", "text 1"),
                   equalTo(true));
        assertThat(executionListDependency.isDependOf(TOKIO.getConnection(), 55, "bidon", "text 1"),
                   equalTo(false));
    }


    @Test
    public void insertDeleteDependency() throws Exception {
        TOKIO.insertInputInDb("INSERT_DELETE_DEPENDENCY");

        DependencyDao dependencyDao = new DependencyDao();
        dependencyDao.insertDependency(TOKIO.getConnection(), 55, "100", "200");
        dependencyDao.deleteDependency(TOKIO.getConnection(), 55, "100", "200");
        dependencyDao.deleteDependencyPrincOrDep(TOKIO.getConnection(), 55, "text 1");
        dependencyDao.deleteDependencyPrincOrDep(TOKIO.getConnection(), 55, "text 2");
        TOKIO.assertAllOutputs("INSERT_DELETE_DEPENDENCY");
    }


    @Test
    public void updateImplicationIsExecutable() throws Exception {
        TOKIO.insertInputInDb("UpdateDependencies_isExecutable");

        DependencyDao dependencyDao = new DependencyDao();
        dependencyDao.updateImplication(TOKIO.getConnection(), 55, "text 2", 10);
        TOKIO.assertAllOutputs("UpdateDependencies_isExecutable");
        assertThat(dependencyDao.isExecutable(TOKIO.getConnection(), 55, "text 22"), equalTo("TRUE"));
    }
}
