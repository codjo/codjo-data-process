package net.codjo.dataprocess.server.util;
import net.codjo.database.common.api.DatabaseFactory;
import net.codjo.database.common.api.DatabaseHelper;
import net.codjo.database.common.api.JdbcFixture;
import net.codjo.database.common.api.structure.SqlTable;
import net.codjo.datagen.DatagenFixture;
import net.codjo.dataprocess.common.Log;
import net.codjo.tokio.JDBCScenario;
import net.codjo.tokio.TokioFixture;
import net.codjo.tokio.model.Table;
import java.io.File;
import java.util.Iterator;
/**
 *
 */
public class TestUtils {
    private TestUtils() {
    }


    public static void initScript(JdbcFixture fixture, DatagenFixture datagen, String tableFileName) {
        fixture.advanced().executeCreateTableScriptFile(new File(datagen.getSqlPath(), tableFileName));
    }


    public static void loadDataFromTokioFile(Class classz, String tokioFilePath, String scenario,
                                             boolean deleteTable)
          throws Exception {
        DatabaseFactory databaseFactory = new DatabaseFactory();
        DatabaseHelper databaseHelper = databaseFactory.createDatabaseHelper();
        TokioFixture tokioFixture = new TokioFixture(classz, tokioFilePath);
        tokioFixture.doSetUp();
        JDBCScenario jdbcScenario = tokioFixture.getJDBCScenario(scenario);
        String str;
        if (deleteTable) {
            str = "' (Seules les tables du fichier seront effacées avant insertion des données).";
        }
        else {
            str = "' (Aucune table ne sera effacée).";
        }
        if (Log.isInfoEnabled()) {
            Log.info(classz, "Insertion des données issues de '" + tokioFilePath + str);
        }
        Iterator iterator = jdbcScenario.getScenario().inputTables();
        while (iterator.hasNext()) {
            Table table = (Table)iterator.next();
            if (deleteTable) {
                SqlTable sqlTable = SqlTable.table(table.getName());
                databaseHelper.truncateTable(tokioFixture.getConnection(), sqlTable);
            }
            jdbcScenario.insertInputTableInDb(tokioFixture.getConnection(), table.getName());
            if (Log.isInfoEnabled()) {
                Log.info(classz, "     Table : " + table.getName());
            }
        }
    }
}
