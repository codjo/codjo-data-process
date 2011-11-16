package net.codjo.dataprocess.server.handlerselect;
import net.codjo.database.api.Database;
import net.codjo.mad.server.handler.HandlerException;
import net.codjo.mad.server.handler.sql.Getter;
import net.codjo.mad.server.handler.sql.SqlHandler;
import java.util.Map;
/**
 *
 */
public class SelectCountHandler extends SqlHandler {
    public SelectCountHandler(Database queryHelper) {
        super(new String[0], "", queryHelper);
        addGetter("nb", new Getter(1));
    }


    @Override
    protected String buildQuery(Map<String, String> arguments) throws HandlerException {
        return "select count(*) as NB from " + arguments.get("table");
    }
}
