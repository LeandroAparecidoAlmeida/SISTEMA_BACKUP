package gui.dialogs;

import java.awt.Cursor;
import java.awt.Frame;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JOptionPane;
import backup.drive.Drive;

/**
 * Tela para a desinstalação da Unidade de Backup selecionada na tela principal.
 * @author Leandro Aparecido de Almeida
 */
final class UninstallBackupDriveDialog extends javax.swing.JDialog {
    
    /**Unidade de Backup selecionada na tela principal.*/
    private final Drive backupDrive;

    /**
     * Criar uma instância de <b>UninstallBackupDriveDialog</b>.
     * @param parent tela proprietária.
     * @param backupDrive Unidade de Backup selecionada na tela principal.
     */
    public UninstallBackupDriveDialog(Frame parent, Drive backupDrive) {
        super(parent, true);
        this.backupDrive = backupDrive;
        initComponents();
        jlBackupDriveLabel.setText(this.backupDrive.toString());
        jlMessage.setVisible(false);
        setLocationRelativeTo(parent);
    }
    
    /**
     * Desinstalar a Unidade de Backup (necessária a confirmação).
     */
    private void uninstallBackupDrive() {
        setCursor(new Cursor(Cursor.WAIT_CURSOR));
        try {
            int opt = JOptionPane.showConfirmDialog(
                this,
                "Desinstalar a Unidade de Backup [" + backupDrive.toString() +
                "]?",
                "Atenção!",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
            );
            if (opt == JOptionPane.YES_OPTION) {
                jlMessage.setVisible(true);
                jbUninstall.setEnabled(false);
                jbCancel.setEnabled(false);
                jcbKeepBackupsLog.setEnabled(false);
                boolean keepBackupsLog = jcbKeepBackupsLog.isSelected();
                backupDrive.uninstall(keepBackupsLog);
                TimerTask task = new TimerTask() {
                    @Override
                    public void run() {                
                        setVisible(false);
                    }
                };
                Timer timer = new java.util.Timer();
                timer.schedule(task, 2500, 100000); 
            }
        } catch (Exception ex) {
            jlMessage.setVisible(false);
            jbUninstall.setEnabled(true);
            jbCancel.setEnabled(true);
            jcbKeepBackupsLog.setEnabled(true);
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
     * Status de desinstalação da Unidade de Backup.
     * @return true, a Unidade de Backup foi desinstalada, false, ela não
     * foi desinstalada.
     */
    public boolean uninstalledBackupDrive() {
        return !backupDrive.isInstalled();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jbCancel = new javax.swing.JButton();
        jbUninstall = new javax.swing.JButton();
        jlMessage = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jlBackupDriveLabel = new javax.swing.JLabel();
        jcbKeepBackupsLog = new javax.swing.JCheckBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("DESINSTALAR UNIDADE DE BACKUP");
        setResizable(false);

        jbCancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons16/icon7.png"))); // NOI18N
        jbCancel.setText("Cancelar");
        jbCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbCancelActionPerformed(evt);
            }
        });

        jbUninstall.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons16/icon11.png"))); // NOI18N
        jbUninstall.setText("Desinstalar");
        jbUninstall.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbUninstallActionPerformed(evt);
            }
        });

        jlMessage.setForeground(java.awt.Color.blue);
        jlMessage.setText("Desinstalação concluída!");

        jLabel1.setText("Desinstalar a Unidade de Backup:");

        jlBackupDriveLabel.setText("[Unidade Backup]");

        jcbKeepBackupsLog.setSelected(true);
        jcbKeepBackupsLog.setText("Manter o histórico dos backups");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jlMessage)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 70, Short.MAX_VALUE)
                .addComponent(jbUninstall, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jbCancel, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jcbKeepBackupsLog)
                    .addComponent(jlBackupDriveLabel)
                    .addComponent(jLabel1))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 28, Short.MAX_VALUE)
                .addComponent(jlBackupDriveLabel)
                .addGap(26, 26, 26)
                .addComponent(jcbKeepBackupsLog)
                .addGap(46, 46, 46)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jbCancel)
                    .addComponent(jbUninstall)
                    .addComponent(jlMessage))
                .addContainerGap())
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jbUninstallActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbUninstallActionPerformed
        uninstallBackupDrive();
    }//GEN-LAST:event_jbUninstallActionPerformed

    private void jbCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbCancelActionPerformed
        setVisible(false);
    }//GEN-LAST:event_jbCancelActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JButton jbCancel;
    private javax.swing.JButton jbUninstall;
    private javax.swing.JCheckBox jcbKeepBackupsLog;
    private javax.swing.JLabel jlBackupDriveLabel;
    private javax.swing.JLabel jlMessage;
    // End of variables declaration//GEN-END:variables

}