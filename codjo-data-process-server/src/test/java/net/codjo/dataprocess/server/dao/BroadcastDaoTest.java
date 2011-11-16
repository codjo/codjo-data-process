package net.codjo.dataprocess.server.dao;
import java.util.Arrays;
import org.junit.Test;

import static net.codjo.test.common.matcher.JUnitMatchers.*;
/**
 *
 */
public class BroadcastDaoTest {
    private BroadcastDao broadcastDao = new BroadcastDao();


    @Test
    public void createDeleteSql() {
        String result = broadcastDao.createDeleteSql(Arrays.asList("%_[09]%.%"));
        assertThat(result, equalTo("delete from PM_BROADCAST_FILES where  FILE_NAME like '%_[09]%.%' "));

        result = broadcastDao.createDeleteSql(Arrays.asList("%.ali%", "%_[09]%.%"));
        assertThat(result,
                   equalTo(
                         "delete from PM_BROADCAST_FILES where  FILE_NAME like '%.ali%'  and  FILE_NAME like '%_[09]%.%' "));
    }


    @Test
    public void createSelectSql() {
        String result = broadcastDao.createSelectSql(Arrays.asList("%_$periode$.%"));
        assertThat(result,
                   equalTo(
                         "select FILE_ID, FILE_NAME from PM_BROADCAST_FILES where  FILE_NAME like '%_$periode$.%'  order by FILE_ID, FILE_NAME"));

        result = broadcastDao.createSelectSql(Arrays.asList("%.ali%", "%_$periode$.%"));
        assertThat(result,
                   equalTo(
                         "select FILE_ID, FILE_NAME from PM_BROADCAST_FILES where  FILE_NAME like '%.ali%'  and  FILE_NAME like '%_$periode$.%'  order by FILE_ID, FILE_NAME"));

        result = broadcastDao.createSelectSql(Arrays.asList(
              "not %.ali%", "%_$periode$.%"));
        assertThat(result,
                   equalTo(
                         "select FILE_ID, FILE_NAME from PM_BROADCAST_FILES where  FILE_NAME not like '%.ali%'  and  FILE_NAME like '%_$periode$.%'  order by FILE_ID, FILE_NAME"));
    }
}
