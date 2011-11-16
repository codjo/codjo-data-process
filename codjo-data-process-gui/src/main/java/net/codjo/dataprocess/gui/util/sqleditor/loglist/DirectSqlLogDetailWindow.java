/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.dataprocess.gui.util.sqleditor.loglist;
import net.codjo.dataprocess.gui.util.AbstractDetailWindow;
import net.codjo.mad.client.request.RequestException;
import net.codjo.mad.gui.request.DetailDataSource;
import javax.swing.JTextArea;
import javax.swing.JTextField;
/**
 *
 */
public class DirectSqlLogDetailWindow extends AbstractDetailWindow {
    public DirectSqlLogDetailWindow(DetailDataSource dataSource)
          throws RequestException {
        super(dataSource, "Affichage de la table T_DIRECTSQL_LOG");
    }


    @Override
    protected void declareFields() throws RequestException {
        // Ajout des objets graphiques au panel et à la datasource
        JTextField idField = new JTextField();
        addField("tDirectSqlLogId", "Id ", idField);
        idField.setEnabled(false);

        JTextField flag = new JTextField();
        addField("flag", "Flag ", flag);
        flag.setEditable(true);

        JTextField initiator = new JTextField();
        addField("initiator", "Utilisateur ", initiator);
        initiator.setEditable(false);

        JTextField requestDate = new JTextField();
        addField("requestDate", "Date ", requestDate);
        requestDate.setEditable(false);

        JTextArea sqlRequest = new JTextArea();
        addField("sqlRequest", "Sql ", sqlRequest);
        sqlRequest.setEditable(false);

        JTextArea result = new JTextArea();
        addField("result", "Résultat ", result);
        result.setEditable(false);
    }
}
