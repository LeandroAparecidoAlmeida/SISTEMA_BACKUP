package gui.dialogs;

import backup.drive.Drive;
import java.io.File;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 * Tela para a manutenção dos diretórios para backup.
 * @author Leandro Aparecido de Almeida
 */
final class BackupDirectoriesDialog extends javax.swing.JDialog {
    
    /**Unidade de Backup selecionada na tela principal.*/
    private final Drive backupDrive;
    /**Status de alteração na lista de diretórios para backup.*/
    private boolean haveChanges = false;

    /**
     * Criar uma instância de <b>BackupDirectoriesDialog</b>.
     * @param parent tela proprietária.
     * @param backupDrive Unidade de Backup selecionada na tela principal.
     */
    public BackupDirectoriesDialog(java.awt.Frame parent, Drive backupDrive) {
        super(parent, true);
        this.backupDrive = backupDrive;
        initComponents();
        update();
        setLocationRelativeTo(parent);
    }
    
    /**
     * Atualizar a lista de diretórios.
     */
    private void update() {
        if (!backupDrive.getBackupDirectoriesList().isEmpty()) {
            DefaultListModel model = new DefaultListModel();
            for (File directory : backupDrive.getBackupDirectoriesList()) {
                model.addElement(directory.getAbsolutePath());
            }
            jlDirectories.setModel(model);
            jlDirectories.setSelectedIndex(0);
            jbDelete.setEnabled(true);
        } else {
            DefaultListModel model = new DefaultListModel();
            jlDirectories.setModel(model);
            jbDelete.setEnabled(false);
        }
    }
    
    /**
     * Inserir diretórios para backup.
     */
    private void insertDiretories() {
        JFileChooser fileDialog = new JFileChooser();
        fileDialog.setDialogTitle("INSERIR DIRETÓRIO PARA BACKUP");
        fileDialog.setDialogType(JFileChooser.OPEN_DIALOG);
        fileDialog.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileDialog.setMultiSelectionEnabled(true);
        int opc = fileDialog.showOpenDialog(this);
        if (opc == JFileChooser.APPROVE_OPTION) {
            try {
                File[] directories = fileDialog.getSelectedFiles();
                backupDrive.insertBackupDirectories(directories);
                haveChanges = true;                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE
                );
            } finally {
                update();
            }
        }                    
    }
    
    /**
     * Remover os diretórios para backup selecionados.
     */
    private void removeDirectories() {
        if (JOptionPane.showConfirmDialog(this, "Excluir os diretórios selecionados?",
        "Atenção", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try {
                List<String> values = jlDirectories.getSelectedValuesList();
                String[] directories = new String[values.size()];
                for (int i = 0; i < values.size(); i++) {
                    directories[i] = values.get(i);
                }                
                backupDrive.deleteBackupDirectories(directories);
                haveChanges = true;
                update();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }
    
    /**Status de mudanças na lista de diretórios para backup.*/
    public boolean haveChanges() {
        return haveChanges;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jlDirectories = new javax.swing.JList<>();
        jbInsert = new javax.swing.JButton();
        jbDelete = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("DIRETÓRIOS PARA BACKUP");
        setResizable(false);

        jScrollPane1.setViewportView(jlDirectories);

        jbInsert.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons16/icon3.png"))); // NOI18N
        jbInsert.setText("Inserir");
        jbInsert.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbInsertActionPerformed(evt);
            }
        });

        jbDelete.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons16/icon4.png"))); // NOI18N
        jbDelete.setText("Excluir");
        jbDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbDeleteActionPerformed(evt);
            }
        });

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons16/icon7.png"))); // NOI18N
        jButton1.setText("Sair");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(jbInsert, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jbDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 434, Short.MAX_VALUE)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addComponent(jScrollPane1)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 456, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jbInsert)
                    .addComponent(jbDelete)
                    .addComponent(jButton1))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jbDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbDeleteActionPerformed
        removeDirectories();
    }//GEN-LAST:event_jbDeleteActionPerformed

    private void jbInsertActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbInsertActionPerformed
        insertDiretories();
    }//GEN-LAST:event_jbInsertActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        setVisible(false);
    }//GEN-LAST:event_jButton1ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton jbDelete;
    private javax.swing.JButton jbInsert;
    private javax.swing.JList<String> jlDirectories;
    // End of variables declaration//GEN-END:variables
}
