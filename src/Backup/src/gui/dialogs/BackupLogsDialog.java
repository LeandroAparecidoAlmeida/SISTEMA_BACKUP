package gui.dialogs;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import backup.drive.LogFileParser;
import backup.utils.StrUtils;
import backup.drive.FileMetadata;
import backup.drive.Drive;

/**
 * Tela para exibição dos arquivos de log na Unidade de Backup selecionada
 * na tela principal.
 * @author Leandro Aparecido de Almeida
 */
final class BackupLogsDialog extends javax.swing.JFrame {

    /**Unidade de Backup selecionada na tela principal.*/
    private final Drive backupDrive;
    /**Lista dos arquivos de log na raiz da Unidade de Backup.*/
    private final List<File> logFilesList;
    /**Lista de metadados de arquivos em um arquivo de log.*/
    private List<FileMetadata> filesMetadataList = null;
    /**Posição do último arquivo.*/
    private int lastFile;
    /**Cursor para o arquivo atual.*/
    private int cursor;    

    /**
     * Criar uma instância de <b>BackupLogsDialog</b>.
     * @param parent tela proprietária.
     * @param backupDrive Unidade de Backup selecionada na tela principal.
     */
    public BackupLogsDialog(java.awt.Frame parent, Drive backupDrive) {
        filesMetadataList = new ArrayList<>();
        this.backupDrive = backupDrive;
        initComponents();
        URL url = getClass().getResource("/images/img2.png");
        Image image = new ImageIcon(url).getImage();
        setIconImage(image); 
        //Carrega os arquivos de log na raiz da Unidade de Backup.
        logFilesList = this.backupDrive.getLogFilesList();          
        if (!logFilesList.isEmpty()) {
            lastFile = logFilesList.size() - 1;
            jlMaximum.setText(String.valueOf(lastFile + 1));
            putCursor(lastFile);
        } else {                       
            putCursor(-1);
        }            
        setLocationRelativeTo(parent);
    }
   
    /**
     * Exibir o arquivo de log na posição atual do cursor.
     */
    private void printSelectedLogFile() {
        setCursor(new Cursor(Cursor.WAIT_CURSOR));
        try {
            String[] titles = new String[] {"", "ARQUIVO", "AÇÃO"};
            DefaultTableModel tableModel;
            if (cursor != -1) {
                jftNumber.setText(String.valueOf(cursor + 1));
                File logFile = logFilesList.get(cursor);
                LogFileParser parser = new LogFileParser(
                    logFile,
                    backupDrive.getRoot()
                );
                filesMetadataList = parser.getFilesMetadataList();
                tableModel = new DefaultTableModel(titles, filesMetadataList.size()) {
                    @Override
                    public boolean isCellEditable(int row, int column) {
                        return false;
                    }            
                };
                int row = -1;
                for (FileMetadata fileMetadata : filesMetadataList) {
                    row++;
                    int operation = fileMetadata.getOperation();
                    tableModel.setValueAt(String.valueOf(operation), row, 0);
                    tableModel.setValueAt(fileMetadata.getTargetPath(), row, 1);
                    String action = null;
                    switch (operation) {
                        case 1: action = "COPIADO"; break;
                        case 2: action = "ATUALIZADO"; break;
                        case 3: action = "EXCLUÍDO"; break;
                    }
                    tableModel.setValueAt(action, row, 2);
                }
                Date backupTime = parser.getBackupTime();
                String backupTimeStr = StrUtils.formatDate1(backupTime);
                if (parser.isPartialBackup()) {
                    backupTimeStr += " (Backup Parcial)";
                }
                jlBackupTime.setText("DATA DO BACKUP: " + backupTimeStr);
            } else {
                tableModel = new DefaultTableModel(titles, 0);
            } 
            jtLogsEntry.setModel(tableModel);
            TableColumnModel tableColumnModel = jtLogsEntry.getColumnModel();
            TableCellRenderer renderer = new TableCellRenderer1();     
            tableColumnModel.getColumn(1).setCellRenderer(renderer);
            tableColumnModel.getColumn(2).setCellRenderer(renderer);
            tableColumnModel.getColumn(0).setMinWidth(0);
            tableColumnModel.getColumn(0).setMaxWidth(0);  
            tableColumnModel.getColumn(2).setMinWidth(80);
            tableColumnModel.getColumn(2).setMaxWidth(80);       
            if (jtLogsEntry.getRowCount() > 0) {
                jtLogsEntry.setRowSelectionInterval(0, 0);
                printSelectedFileMetadata();
            }        
            jlFilesCount.setText(
                String.valueOf(jtLogsEntry.getRowCount()) +
                " ARQUIVOS"
            );
        } catch (IOException ex) {
            putCursor(-1);           
            JOptionPane.showMessageDialog(
                this,
                ex.getMessage(),
                "Erro",
                JOptionPane.ERROR_MESSAGE
            );
        }
        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }
    
    /**
     * Exibir os metadados do arquivo de backup selecionado na JTable.
     */
    private void printSelectedFileMetadata() {
        if (jtLogsEntry.getSelectedRow() >= 0) {
            FileMetadata fileMetadata = filesMetadataList.get(jtLogsEntry.getSelectedRow());
            StringBuilder sb = new StringBuilder();
            sb.append("Detalhes:\n\n");
            sb.append("Arquivo Selecionado....: ");
            sb.append(fileMetadata.getTargetPath());
            sb.append("\n");
            sb.append("Origem do Arquivo......: ");
            sb.append(fileMetadata.getSourcePath());
            sb.append("\n");
            sb.append("Data de criação........: ");
            sb.append(StrUtils.formatDate1(fileMetadata.getCreationTime()));
            sb.append("\n");
            sb.append("Data da modificação....: ");
            sb.append(StrUtils.formatDate1(fileMetadata.getLastModifiedTime()));
            sb.append("\n");
            sb.append("Tamanho................: ");            
            sb.append(StrUtils.formatBytes((long) fileMetadata.getSize()));
            sb.append("\n");
            sb.append("Hora do Backup.........: ");
            sb.append(StrUtils.formatHour(fileMetadata.getBackupTime()));
            jtaFileDetails.setText(sb.toString());
        }
    }
    
    /**
     * Mover o cursor e imprimir o arquivo naquela posição.
     * @param position nova posição do cursor.
     */
    private void putCursor(int position) {
        cursor = position;
        if (cursor >= 0 && cursor <= lastFile) {              
            jbFirst.setEnabled(true);
            jbPrior.setEnabled(true);
            jbNext.setEnabled(true);
            jbLast.setEnabled(true);
            if (cursor == 0) {
                jbPrior.setEnabled(false); 
                jbFirst.setEnabled(false);
            } 
            if (cursor == lastFile) {
                jbNext.setEnabled(false);
                jbLast.setEnabled(false);
            }
            printSelectedLogFile();
        } else if (cursor == -1) {
            //Lista de log vazia.
            jbFirst.setEnabled(false);
            jbNext.setEnabled(false);
            jbPrior.setEnabled(false);
            jbLast.setEnabled(false);
            jftNumber.setEnabled(false);
            jlMaximum.setText("9999999999");
            jftNumber.setText("0");  
            jlBackupTime.setText("");
            jlFilesCount.setText("");
            printSelectedLogFile();
        }          
    }
    
    /**Mover o cursor para o arquivo na posição digitada na caixa de texto.*/
    private void moveToDesignated() {
        int position = Integer.valueOf(jftNumber.getText()) - 1;
        if (position >= 0 && position <= lastFile) {
            putCursor(position);
        } else {
            putCursor(cursor);
        }
    }
    
    /**Mover o cursor para o primeiro arquivo.*/
    private void moveToFirst() {
        putCursor(0);
    }
    
    /**Mover o cursor para o último arquivo.*/
    private void moveToLast() {
        putCursor(lastFile);
    }
    
    /**Mover o cursor para o próximo arquivo.*/
    private void moveToNext() {
        putCursor(cursor + 1);
    }
    
    /**Mover o cursor para o arquivo anterior.*/    
    private void moveToPrior() {
        putCursor(cursor - 1);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jToolBar1 = new javax.swing.JToolBar();
        jbFirst = new javax.swing.JButton();
        jbPrior = new javax.swing.JButton();
        jbNext = new javax.swing.JButton();
        jbLast = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        jftNumber = new javax.swing.JFormattedTextField();
        jLabel1 = new javax.swing.JLabel();
        jlMaximum = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        jlBackupTime = new javax.swing.JLabel();
        jlFilesCount = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jtLogsEntry = new javax.swing.JTable();
        final TableCellRenderer tcr = jtLogsEntry.getTableHeader().getDefaultRenderer();
        jtLogsEntry.getTableHeader().setDefaultRenderer(
            new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table,
                    Object value, boolean isSelected, boolean hasFocus, int row,
                    int column) {
                    Component c = tcr.getTableCellRendererComponent(table, value,
                        isSelected, hasFocus, row, column);
                    if (c instanceof DefaultTableCellRenderer) {
                        DefaultTableCellRenderer dtcr = (DefaultTableCellRenderer) c;
                        dtcr.setHorizontalAlignment(SwingConstants.LEFT);
                        dtcr.setBorder(new CompoundBorder(
                            dtcr.getBorder(),
                            new EmptyBorder(0, 1, 0, 1))
                    );
                }
                return c;
            }
        }
    );
    jScrollPane2 = new javax.swing.JScrollPane();
    jtaFileDetails = new javax.swing.JTextArea();

    setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
    setTitle("HISTÓRICO DOS BACKUPS");

    jToolBar1.setFloatable(false);
    jToolBar1.setRollover(true);

    jbFirst.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons16/icon19.png"))); // NOI18N
    jbFirst.setFocusable(false);
    jbFirst.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    jbFirst.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    jbFirst.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            jbFirstActionPerformed(evt);
        }
    });
    jToolBar1.add(jbFirst);

    jbPrior.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons16/icon17.png"))); // NOI18N
    jbPrior.setFocusable(false);
    jbPrior.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    jbPrior.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    jbPrior.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            jbPriorActionPerformed(evt);
        }
    });
    jToolBar1.add(jbPrior);

    jbNext.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons16/icon18.png"))); // NOI18N
    jbNext.setFocusable(false);
    jbNext.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    jbNext.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    jbNext.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            jbNextActionPerformed(evt);
        }
    });
    jToolBar1.add(jbNext);

    jbLast.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons16/icon20.png"))); // NOI18N
    jbLast.setFocusable(false);
    jbLast.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    jbLast.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    jbLast.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            jbLastActionPerformed(evt);
        }
    });
    jToolBar1.add(jbLast);
    jToolBar1.add(jSeparator1);

    jftNumber.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
    jftNumber.setMaximumSize(new java.awt.Dimension(80, 2147483647));
    jftNumber.addFocusListener(new java.awt.event.FocusAdapter() {
        public void focusLost(java.awt.event.FocusEvent evt) {
            jftNumberFocusLost(evt);
        }
    });
    jftNumber.addKeyListener(new java.awt.event.KeyAdapter() {
        public void keyPressed(java.awt.event.KeyEvent evt) {
            jftNumberKeyPressed(evt);
        }
    });
    jToolBar1.add(jftNumber);

    jLabel1.setText(" DE ");
    jToolBar1.add(jLabel1);

    jlMaximum.setEditable(false);
    jlMaximum.setMaximumSize(new java.awt.Dimension(80, 2147483647));
    jToolBar1.add(jlMaximum);

    jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

    jlBackupTime.setText("[data]");

    jlFilesCount.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jlFilesCount.setText("[num arquivos]");

    javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
    jPanel1.setLayout(jPanel1Layout);
    jPanel1Layout.setHorizontalGroup(
        jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(jPanel1Layout.createSequentialGroup()
            .addContainerGap()
            .addComponent(jlBackupTime, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGap(18, 18, 18)
            .addComponent(jlFilesCount, javax.swing.GroupLayout.PREFERRED_SIZE, 309, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addContainerGap())
    );
    jPanel1Layout.setVerticalGroup(
        jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addComponent(jlFilesCount, javax.swing.GroupLayout.DEFAULT_SIZE, 27, Short.MAX_VALUE)
        .addComponent(jlBackupTime, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
    );

    jtLogsEntry.setModel(new javax.swing.table.DefaultTableModel(
        new Object [][] {
            {},
            {},
            {},
            {}
        },
        new String [] {

        }
    ));
    jtLogsEntry.setFillsViewportHeight(true);
    jtLogsEntry.setRowHeight(21);
    jtLogsEntry.setShowHorizontalLines(false);
    jtLogsEntry.setShowVerticalLines(false);
    jtLogsEntry.getTableHeader().setResizingAllowed(false);
    jtLogsEntry.getTableHeader().setReorderingAllowed(false);
    jtLogsEntry.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mouseClicked(java.awt.event.MouseEvent evt) {
            jtLogsEntryMouseClicked(evt);
        }
    });
    jtLogsEntry.addKeyListener(new java.awt.event.KeyAdapter() {
        public void keyReleased(java.awt.event.KeyEvent evt) {
            jtLogsEntryKeyReleased(evt);
        }
    });
    jScrollPane1.setViewportView(jtLogsEntry);

    jtaFileDetails.setEditable(false);
    jtaFileDetails.setColumns(20);
    jtaFileDetails.setFont(new java.awt.Font("Courier New", 0, 13)); // NOI18N
    jtaFileDetails.setRows(5);
    jScrollPane2.setViewportView(jtaFileDetails);

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
            .addGap(2, 2, 2)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane2)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 806, Short.MAX_VALUE))
            .addGap(2, 2, 2))
    );
    layout.setVerticalGroup(
        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(layout.createSequentialGroup()
            .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(0, 0, 0)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 339, Short.MAX_VALUE)
            .addGap(0, 0, 0)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(0, 0, 0)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(2, 2, 2))
    );

    pack();
    setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jbFirstActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbFirstActionPerformed
        moveToFirst();
    }//GEN-LAST:event_jbFirstActionPerformed

    private void jbLastActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbLastActionPerformed
        moveToLast();
    }//GEN-LAST:event_jbLastActionPerformed

    private void jbPriorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbPriorActionPerformed
        moveToPrior();
    }//GEN-LAST:event_jbPriorActionPerformed

    private void jbNextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbNextActionPerformed
        moveToNext();
    }//GEN-LAST:event_jbNextActionPerformed

    private void jftNumberFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jftNumberFocusLost
        moveToDesignated();
    }//GEN-LAST:event_jftNumberFocusLost

    private void jftNumberKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jftNumberKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            moveToDesignated();
        }
    }//GEN-LAST:event_jftNumberKeyPressed

    private void jtLogsEntryMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jtLogsEntryMouseClicked
        printSelectedFileMetadata();
    }//GEN-LAST:event_jtLogsEntryMouseClicked

    private void jtLogsEntryKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtLogsEntryKeyReleased
        printSelectedFileMetadata();
    }//GEN-LAST:event_jtLogsEntryKeyReleased

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JButton jbFirst;
    private javax.swing.JButton jbLast;
    private javax.swing.JButton jbNext;
    private javax.swing.JButton jbPrior;
    private javax.swing.JFormattedTextField jftNumber;
    private javax.swing.JLabel jlBackupTime;
    private javax.swing.JLabel jlFilesCount;
    private javax.swing.JTextField jlMaximum;
    private javax.swing.JTable jtLogsEntry;
    private javax.swing.JTextArea jtaFileDetails;
    // End of variables declaration//GEN-END:variables

}