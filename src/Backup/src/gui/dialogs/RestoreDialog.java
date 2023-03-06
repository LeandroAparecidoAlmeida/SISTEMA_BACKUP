package gui.dialogs;

import java.awt.Cursor;
import java.io.File;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;
import backup.drive.Drive;
import backup.drive.DrivesManager;
import backup.drive.RestoreListener;
import backup.drive.SafeRestore;

/**
 * Tela para fazer a restauração de arquivos para o sistema local.
 * @author Leandro Aparecido de Almeida
 */
final class RestoreDialog extends javax.swing.JDialog implements RestoreListener {
    
    /**Timer para produzir os efeitos no texto de cópia de arquivos.*/
    private final Timer timer;
    /**Lista de partições do sistema local.*/
    private List<Drive> partitionsList;
    /**Unidade de Backup selecionada na tela principal.*/
    private final Drive backupDrive;

    /**
     * Criar uma instância de <b>RestoreDialog</b>.
     * @param parent tela proprietária.
     * @param backupDrive Unidade de Backup selecionada na tela principal.
     */
    public RestoreDialog(java.awt.Frame parent, Drive backupDrive) {
        super(parent, true);
        this.backupDrive = backupDrive;
        this.timer = new java.util.Timer();
        initComponents();
        listHardDiskPartitions();
        jlMessage.setVisible(false);
        setLocationRelativeTo(parent);
        jbCancel.setEnabled(false);
        jbRestore.setEnabled(true);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);        
    }
    
    /**Listar as partições do disco rígido local.*/
    private void listHardDiskPartitions() {
        setCursor(new Cursor(Cursor.WAIT_CURSOR));
        try {
            partitionsList = DrivesManager.getHardDiskPartitions();
            String[] items = new String[partitionsList.size()];
            for (int i = 0; i < partitionsList.size(); i++) {
                items[i] = partitionsList.get(i).getLetter();
            }
            DefaultComboBoxModel model = new DefaultComboBoxModel(items);
            jcbDrive.setModel(model); 
            if (SafeRestore.isPendingRestore()) {
                jcbDrive.setEnabled(false);
                int idx = -1;
                for (int i = 0; i < partitionsList.size(); i++) {
                    Drive partition = partitionsList.get(i);
                    String id1 = partition.getLetter();
                    String id2 = SafeRestore.getTargetDriveId();
                    if (id1 != null) {
                        if (id1.equals(id2)) {
                            idx = i;
                            break;
                        }
                    }
                }
                jcbDrive.setSelectedIndex(idx);
            }                      
        } catch (Exception ex) {
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
     * Restaurar os arquivos para a partição local.
     */
    private void performRestore() {
        RestoreListener listener = this;
        new Thread() {
            @Override
            public void run() {                
                jcbDrive.setEnabled(false);
                jbCancel.setEnabled(true);
                jbRestore.setEnabled(false);
                jpbStatus.setIndeterminate(true);
                setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
                Drive partition = partitionsList.get(jcbDrive.getSelectedIndex());
                backupDrive.performRestore(partition, listener);
            }
        }.start();
    }
    
    /**
     * Interromper a restauração dos arquivos.
     */
    private void cancelRestore() {
        new Thread() {
            @Override
            public void run() {
                setCursor(new Cursor(Cursor.WAIT_CURSOR));
                timer.cancel();
                jlMessage.setText("Cancelando a restauração...");
                jbCancel.setEnabled(false);
                backupDrive.abortProcess();
                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }            
        }.start();
    }
    
    @Override
    public void restoreInitialized(int numberOfFiles) {
        timer.schedule(new Task(), 0, 900);
        jlMessage.setVisible(true);
        jpbStatus.setIndeterminate(false);
        jpbStatus.setMinimum(0);
        jpbStatus.setMaximum(numberOfFiles);
        jpbStatus.setValue(0);
    }

    @Override
    public void processingFile(int fileNumber, File file) {
        jpbStatus.setValue(fileNumber);
    }

    @Override
    public void restoreAbortedByError(Exception ex) {
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        timer.cancel();
        JOptionPane.showMessageDialog(
            this,
            ex.getMessage(),
            "Erro",
            JOptionPane.ERROR_MESSAGE
        );
        setVisible(false);
    }

    @Override
    public void restoreAbortedByUser() {
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        timer.cancel();
        setVisible(false);
    }

    @Override
    public void restoreDone() {
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        timer.cancel();
        setVisible(false);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jbRestore = new javax.swing.JButton();
        jbCancel = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jcbDrive = new javax.swing.JComboBox<>();
        jpbStatus = new javax.swing.JProgressBar();
        jlMessage = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("RESTAURAÇÃO DOS ARQUIVOS");
        setResizable(false);

        jbRestore.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons16/icon42.png"))); // NOI18N
        jbRestore.setText("Restaurar");
        jbRestore.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbRestoreActionPerformed(evt);
            }
        });

        jbCancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons16/icon7.png"))); // NOI18N
        jbCancel.setText("Abortar");
        jbCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbCancelActionPerformed(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel1.setText("PARTIÇÃO DE DESTINO:");

        jcbDrive.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jpbStatus.setStringPainted(true);

        jlMessage.setText("Restaurando os arquivos...");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(15, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jcbDrive, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 244, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jpbStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 468, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jlMessage)))
                .addGap(15, 15, 15))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jcbDrive, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(35, 35, 35)
                .addComponent(jpbStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jlMessage)
                .addContainerGap(40, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(138, 138, 138)
                        .addComponent(jbRestore, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jbCancel, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jbRestore)
                    .addComponent(jbCancel))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jbRestoreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbRestoreActionPerformed
        performRestore();
    }//GEN-LAST:event_jbRestoreActionPerformed

    private void jbCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbCancelActionPerformed
        cancelRestore();
    }//GEN-LAST:event_jbCancelActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JButton jbCancel;
    private javax.swing.JButton jbRestore;
    private javax.swing.JComboBox<String> jcbDrive;
    private javax.swing.JLabel jlMessage;
    private javax.swing.JProgressBar jpbStatus;
    // End of variables declaration//GEN-END:variables

    /**TimerTask para produzir efeito de animação no texto de cópia de arquivos,
     fazendo os 3 pontos no final do texto intermitentes.*/
    private class Task extends TimerTask {        
        private int count = 0;        
        @Override
        public void run() {
            switch (this.count) {
                case 0: jlMessage.setText("Restaurando arquivos"); break;
                case 1: jlMessage.setText("Restaurando arquivos ."); break;
                case 2: jlMessage.setText("Restaurando arquivos . ."); break;
                case 3: jlMessage.setText("Restaurando arquivos . . ."); break;
            }
            this.count = (this.count == 3 ? 0 : this.count + 1);
        }   
    }
    
}