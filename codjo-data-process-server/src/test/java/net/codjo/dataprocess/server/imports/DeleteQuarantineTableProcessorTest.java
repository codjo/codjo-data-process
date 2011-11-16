package net.codjo.dataprocess.server.imports;
import static net.codjo.test.common.matcher.JUnitMatchers.assertThat;
import static net.codjo.test.common.matcher.JUnitMatchers.equalTo;
import org.junit.Test;
/**
 *
 */
public class DeleteQuarantineTableProcessorTest {
    @Test
    public void getFinalTableName() {
        DeleteQuarantineTableProcessor deleteQuarantineTableProcessor = new DeleteQuarantineTableProcessor();

        assertThat("AP_JE_SUIS_UNE_TABLE",
                   equalTo(deleteQuarantineTableProcessor.quarantineToFinalTableName(
                         "Q_AP_JE_SUIS_UNE_TABLE")));
        assertThat("PM_MULTIPART",
                   equalTo(deleteQuarantineTableProcessor.quarantineToFinalTableName("Q_PM_MULTIPART")));
    }


    @Test
    public void getUserQuarantineTableName() throws Exception {
        DeleteQuarantineTableProcessor deleteQuarantineTableProcessor = new DeleteQuarantineTableProcessor();

        assertThat("Q_AP_USER_JE_SUIS_UNE_TABLE",
                   equalTo(deleteQuarantineTableProcessor.quarantineToUserQuarantineTableName(
                         "Q_AP_JE_SUIS_UNE_TABLE")));
        assertThat("Q_PM_USER_MULTIPART",
                   equalTo(deleteQuarantineTableProcessor.quarantineToUserQuarantineTableName(
                         "Q_PM_MULTIPART")));
    }


    @Test
    public void buildQuery() throws Exception {
        DeleteQuarantineTableProcessor deleteQuarantineTableProcessor = new DeleteQuarantineTableProcessor(
              true);
        assertThat("truncate table Q_AP_C84_BALAVL",
                   equalTo(deleteQuarantineTableProcessor.buildDeleteQuarantineQuery("Q_AP_C84_BALAVL")));
        assertThat("truncate table Q_AP_USER_C84_BALAVL",
                   equalTo(deleteQuarantineTableProcessor.buildDeleteQUserQuery("Q_AP_C84_BALAVL")));

        deleteQuarantineTableProcessor = new DeleteQuarantineTableProcessor(false);
        assertThat(
              " set rowcount 1000  while exists (select 1 from Q_AP_C84_BALAVL ) begin  begin tran      delete from Q_AP_C84_BALAVL      if @@error > 0          rollback      else          commit  end  set rowcount 0 ",
              equalTo(deleteQuarantineTableProcessor.buildDeleteQuarantineQuery("Q_AP_C84_BALAVL")));
        assertThat(
              " set rowcount 1000  while exists (select 1 from Q_AP_USER_C84_BALAVL ) begin  begin tran     delete from Q_AP_USER_C84_BALAVL      if @@error > 0          rollback      else          commit  end  set rowcount 0 ",
              equalTo(deleteQuarantineTableProcessor.buildDeleteQUserQuery("Q_AP_C84_BALAVL")));

        deleteQuarantineTableProcessor = new DeleteQuarantineTableProcessor();
        assertThat(
              " set rowcount 1000  while exists (select 1 from Q_AP_C84_BALAVL ) begin  begin tran      delete from Q_AP_C84_BALAVL      if @@error > 0          rollback      else          commit  end  set rowcount 0 ",
              equalTo(deleteQuarantineTableProcessor.buildDeleteQuarantineQuery("Q_AP_C84_BALAVL")));
        assertThat(
              " set rowcount 1000  while exists (select 1 from Q_AP_USER_C84_BALAVL ) begin  begin tran     delete from Q_AP_USER_C84_BALAVL      if @@error > 0          rollback      else          commit  end  set rowcount 0 ",
              equalTo(deleteQuarantineTableProcessor.buildDeleteQUserQuery("Q_AP_C84_BALAVL")));

        deleteQuarantineTableProcessor = new DeleteQuarantineTableProcessor(true);
        assertThat("truncate table Q_AP_C84_BALAVL",
                   equalTo(deleteQuarantineTableProcessor.buildDeleteQuarantineQuery("Q_AP_C84_BALAVL")));
        assertThat("truncate table Q_AP_USER_C84_BALAVL",
                   equalTo(deleteQuarantineTableProcessor.buildDeleteQUserQuery("Q_AP_C84_BALAVL")));

        String whereClause = "where PERIODE = 'NO_PER' or PLAN_COMPTE = 'NO_P'";
        deleteQuarantineTableProcessor = new DeleteQuarantineTableProcessor(whereClause);
        assertThat(
              " set rowcount 1000  while exists (select 1 from Q_AP_C84_BALAVL where PERIODE = 'NO_PER' or PLAN_COMPTE = 'NO_P') begin  begin tran      delete from Q_AP_C84_BALAVL where PERIODE = 'NO_PER' or PLAN_COMPTE = 'NO_P'     if @@error > 0          rollback      else          commit  end  set rowcount 0 ",
              equalTo(deleteQuarantineTableProcessor.buildDeleteQuarantineQuery("Q_AP_C84_BALAVL")));
        assertThat(
              " set rowcount 1000  while exists (select 1 from Q_AP_USER_C84_BALAVL where PERIODE = 'NO_PER' or PLAN_COMPTE = 'NO_P') begin  begin tran     delete from Q_AP_USER_C84_BALAVL where PERIODE = 'NO_PER' or PLAN_COMPTE = 'NO_P'     if @@error > 0          rollback      else          commit  end  set rowcount 0 ",
              equalTo(deleteQuarantineTableProcessor.buildDeleteQUserQuery("Q_AP_C84_BALAVL")));
    }
}
