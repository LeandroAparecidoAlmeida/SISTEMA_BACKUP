package gui.dialogs;

import java.awt.Component;
import java.io.File;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;
import backup.drive.BackupListener;
import backup.drive.Drive;

/**
 * Tela para controle do processo de backup na Unidade de Backup selecionada 
 * na tela principal. Implementa a interface {@link BackupListener} para ser 
 * notificada de cada etapa no processamento.
 * @author Leandro Aparecido de Almeida
 */
@Deprecated
final class BackupDialog extends javax.swing.JDialog implements BackupListener {

    /**Unidade de Backup selecionada na tela principal.*/
    private final Drive backupDrive;
    /**Status de backup concluído em todas as etapas.*/
    private boolean backupDone;
    
    /**
     * Criar uma instância de <b>BackupDialog</b>.
     * @param parent tela proprietária.
     * @param backupDrive Unidade de Backup selecionada na tela principal.
     */
    public BackupDialog(java.awt.Frame parent, Drive backupDrive) {
        super(parent, true);
        this.backupDrive = backupDrive;
        this.backupDone = false;
        initComponents();
        jlMessage1.setText("Processando...");       
        jpbBackup.setIndeterminate(true);
        jpbBackup.setStringPainted(false);
        setLocationRelativeTo(parent);        
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    }
    
    /**
     * Fazer o backup dos arquivos na Unidade de Backup.
     */
    private void performBackup() {
        BackupListener listener = this;
        Component comp = this;
        new Thread() {
            @Override
            public void run() {
                try {
                    backupDrive.performBackup(listener);
                } catch (Exception ex) {
                    setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
                    jpbBackup.setIndeterminate(true);
                    jpbBackup.setStringPainted(false);
                    jpbBackup.setValue(jpbBackup.getMaximum());
                    jbCancel.setEnabled(false);
                    JOptionPane.showMessageDialog(
                        comp,
                        ex.getMessage(),
                        "Erro",
                        JOptionPane.ERROR_MESSAGE
                    );
                    setVisible(false);
                } 
            } 
        }.start();          
    }
    
    /**
     * Cancelar o backup dos arquivos.
     */
    public void abortBackup() {
        new Thread() {
            @Override
            public void run() {
                jlMessage1.setText("Cancelando o backup...");
                jbCancel.setEnabled(false);
                backupDrive.abortProcess();
            }
        }.start();        
    }
    
    /**
     * Notificação de backup iniciado. Configura a barra de status e dispara o
     * timer para produzir o efeito no texto de cópia de arquivos.
     * @param numberOfFiles número de arquivos do backup.
     */
    @Override
    public void backupInitialized(int numberOfFiles) {
        jpbBackup.setIndeterminate(false);
        jpbBackup.setStringPainted(true);
        jpbBackup.setMaximum(numberOfFiles);
        jlMessage1.setText("ARQUIVO 0 DE " + String.valueOf(jpbBackup.getMaximum()));
    }
    
    /**
     * Notificação de processamento de uma etapa do backup. Atualiza a
     * barra de status.
     * @param fileNumber número do arquivo atual em backup.
     * @param file arquivo em processamento.
     */
    @Override
    public void processingFile(int fileNumber, File file, int mode) {
        jpbBackup.setValue(fileNumber);
        jlMessage1.setText(
            "ARQUIVO " +
            String.valueOf(fileNumber) +
            " DE " +
            String.valueOf(jpbBackup.getMaximum())
        );
    }
    
    /**
     * Notificação de cancelamento do processo de backup em alguma etapa por
     * motivo de erro no processamento.
     * @param ex exceção associada.
     */
    @Override
    public void backupAbortedByError(Exception ex) {
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        jpbBackup.setIndeterminate(true);
        jpbBackup.setStringPainted(false);
        jpbBackup.setValue(jpbBackup.getMaximum());
        jbCancel.setEnabled(false);
        JOptionPane.showMessageDialog(
            this,
            ex.getMessage(),
            "Erro",
            JOptionPane.ERROR_MESSAGE
        );
        setVisible(false);
    }

    /**
     * Notificação de cancelamento do processo de backup em alguma etapa por
     * decisão do usuário.
     */
    @Override
    public void backupAbortedByUser() {
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setVisible(false);
    }
 
    /**
     * Notificação da conclusão com sucesso do backup.
     */
    @Override
    public void backupDone() {
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        backupDone = true;
        if (jpbBackup.getMaximum() == 0) {
            jpbBackup.setMaximum(100);
            jpbBackup.setValue(100);
        }
        setVisible(false);
    }

    /**
     * Status de backup finalizado com sucesso (não cancelado pelo usuário ou
     * por motivo de erros).
     */
    public boolean isBackupDone() {
        return backupDone;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jpbBackup = new javax.swing.JProgressBar();
        jlMessage1 = new javax.swing.JLabel();
        jbCancel = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("BACKUP DOS ARQUIVOS");
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jpbBackup.setBackground(new java.awt.Color(245, 245, 245));
        jpbBackup.setForeground(new java.awt.Color(153, 153, 153));
        jpbBackup.setStringPainted(true);

        jlMessage1.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jlMessage1.setForeground(new java.awt.Color(102, 102, 102));
        jlMessage1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jlMessage1.setText("[mensagem]");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jpbBackup, javax.swing.GroupLayout.DEFAULT_SIZE, 521, Short.MAX_VALUE)
                    .addComponent(jlMessage1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(24, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(jpbBackup, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addComponent(jlMessage1)
                .addContainerGap(25, Short.MAX_VALUE))
        );

        jbCancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons16/icon7.png"))); // NOI18N
        jbCancel.setText("Cancelar o Backup");
        jbCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbCancelActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addGap(214, 214, 214)
                .addComponent(jbCancel, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jbCancel)
                .addContainerGap())
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jbCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbCancelActionPerformed
        abortBackup();
    }//GEN-LAST:event_jbCancelActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        performBackup();        
    }//GEN-LAST:event_formWindowOpened

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JButton jbCancel;
    private javax.swing.JLabel jlMessage1;
    private javax.swing.JProgressBar jpbBackup;
    // End of variables declaration//GEN-END:variables
    
}