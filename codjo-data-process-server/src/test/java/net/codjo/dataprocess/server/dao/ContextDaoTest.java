package net.codjo.dataprocess.server.dao;
import net.codjo.database.common.api.JdbcFixture;
import net.codjo.datagen.DatagenFixture;
import net.codjo.dataprocess.common.context.DataProcessContext;
import net.codjo.dataprocess.server.util.TestUtils;
import net.codjo.test.common.fixture.CompositeFixture;
import net.codjo.tokio.TokioFixture;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static net.codjo.test.common.matcher.JUnitMatchers.*;
/**
 *
 */
public class ContextDaoTest {
    private static final TokioFixture TOKIO = new TokioFixture(ContextDaoTest.class);
    private static final DatagenFixture DATAGEN = new DatagenFixture(ContextDaoTest.class);
    private static final CompositeFixture COMPOSITE_FIXTURE = new CompositeFixture(TOKIO, DATAGEN);


    @BeforeClass
    public static void beforeClass() throws Exception {
        COMPOSITE_FIXTURE.doSetUp();
        JdbcFixture jdbcFixture = TOKIO.getJdbcFixture();
        jdbcFixture.advanced().dropAllObjects();

        try {
            DATAGEN.generate();
            TestUtils.initScript(jdbcFixture, DATAGEN, "PM_DP_CONTEXT.tab");
        }
        catch (Exception e) {
            COMPOSITE_FIXTURE.doTearDown();
            fail(e.getLocalizedMessage());
        }
    }


    @AfterClass
    public static void afterClass() throws Exception {
        COMPOSITE_FIXTURE.doTearDown();
    }


    @Test
    public void saveContext() throws Exception {
        TOKIO.insertInputInDb("CONTEXT");

        DataProcessContext context = new DataProcessContext();
        context.setProperty("KEY1", "VALUE10");
        context.setProperty("K1", "V1");
        context.setProperty("K2", "V2");
        context.setProperty("K3", "V3");

        new ContextDao().saveContext(TOKIO.getConnection(), "REPO1", context.encode());
        TOKIO.assertAllOutputs("CONTEXT");
    }


    @Test
    public void loadDataProcessContextByContextName() throws Exception {
        TOKIO.insertInputInDb("CONTEXT");

        DataProcessContext context
              = new ContextDao().getDataProcessContext(TOKIO.getConnection(), "REPO1");
        assertThat(context.getProperty("KEY1"), equalTo("VALUE1"));
        assertThat(context.getProperty("KEY2"), equalTo("VALUE2"));
    }
}
