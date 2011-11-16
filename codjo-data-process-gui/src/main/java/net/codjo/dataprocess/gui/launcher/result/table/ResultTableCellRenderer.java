/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.dataprocess.gui.launcher.result.table;
import java.awt.Color;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
/**
 *
 */
public class ResultTableCellRenderer extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table,
                                                   Object value,
                                                   boolean isSelected,
                                                   boolean hasFocus,
                                                   int row,
                                                   int column) {
        TreatmentResult treatmentResult = (TreatmentResult)value;
        JLabel jLabel = new JLabel();
        jLabel.setOpaque(true);
        jLabel.setBorder(noFocusBorder);
        if (!isSelected) {
            jLabel.setBackground(table.getBackground());
        }
        else {
            jLabel.setBackground(table.getSelectionBackground());
            jLabel.setForeground(table.getSelectionForeground());
        }
        switch (column) {
            case 0:
                jLabel.setText(treatmentResult.getTitle());
                break;
            case 1:
                jLabel.setHorizontalTextPosition(JLabel.CENTER);
                jLabel.setText(treatmentResult.getStateLabel());
                jLabel.setBackground(new Color(treatmentResult.getStateColor()));
                break;
            case 2:
                jLabel.setText(treatmentResult.getMessage());
                break;
            default:
                jLabel.setText("???");
                break;
        }
        return jLabel;
    }
}
