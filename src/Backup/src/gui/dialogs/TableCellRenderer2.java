package gui.dialogs;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.io.File;
import java.util.Date;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellRenderer;
import backup.utils.StrUtils;
import javax.swing.filechooser.FileSystemView;

/**
 * Renderizador para a JTable da tela {@link MainWindow}.
 * @author Leandro Aparecido de Almeida
 */
final class TableCellRenderer2 implements TableCellRenderer {
     
    /**Retorna o componente de exibição de uma célula, devidamente configurado.*/
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
    boolean isSelected, boolean hasFocus, int row, int column) {
        JLabel label = new JLabel(); 
        label.setOpaque(true);
        //Formata as células da JTable, de acordo com a coluna a que pertencem.
        switch (column) {
            //Coluna 1: ícone do arquivo e seu texto.
            case 1: {
                File file = (File)value;
                Icon icon = FileSystemView.getFileSystemView().getSystemIcon(file);
                label.setIcon(icon);
                label.setText(file.getAbsolutePath());
            } break;
            //Coluna 2: data da criação do arquivo.
            case 2: {
                label.setText(StrUtils.formatDate3((Date)value));
                label.setHorizontalAlignment(SwingConstants.LEFT);
            } break;
            //Coluna 3: data da última alteração do arquivo.
            case 3: {
                label.setText(StrUtils.formatDate3((Date)value));
                label.setHorizontalAlignment(SwingConstants.LEFT);
            } break;
            //Coluna 4: tamanho do arquivo.
            case 4: {
                label.setText(StrUtils.formatBytes((long) value));
            } break;
            //Coluna 5: operação com o arquivo.
            case 5: {
                label.setText((String) value);
            } break;
        }   
        //Altera a cor do texto da célula, de acordo com a operação com o
        //arquivo.
        Font font = new Font("tahoma", Font.PLAIN, 12);
        label.setFont(font);
        Color color = null;
        switch ((String)table.getValueAt(row, 0)) {
            case "1": color = Color.BLACK; break;
            case "2": color = new Color(0,102,0); break;
            case "3": color = Color.RED; break;
        }        
        label.setForeground(color);      
        if (isSelected) {
            label.setBackground(table.getSelectionBackground());
        } else {
            label.setBackground(table.getBackground());
        }
        return label;
    } 
    
}
