package gui.dialogs;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 * Renderizador para a JTable da tela {@link BackupsLogDialog}.
 * @author Leandro Aparecido de Almeida
 */
final class TableCellRenderer1 implements TableCellRenderer {

    /**Obter o componente de exibição de uma célula, devidamente configurado.*/
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
    boolean isSelected, boolean hasFocus, int row, int column) { 
        String id = (String)table.getValueAt(row, 0);
        JLabel label = new JLabel((String)value);
        label.setOpaque(true);
        Font font = new Font("tahoma", Font.PLAIN, 12);
        Color color = null;
        switch (id) {
            case "1": color = Color.BLACK; break;
            case "2": color = new Color(0,102,0); break;
            case "3": color = Color.RED; break;
        }
        label.setFont(font);
        label.setForeground(color);      
        if (isSelected) {
            label.setBackground(table.getSelectionBackground());
        } else {
            label.setBackground(table.getBackground());
        }
        return label;
    } 
    
}