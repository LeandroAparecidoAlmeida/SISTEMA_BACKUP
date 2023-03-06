package gui.dialogs;

import backup.drive.Drive;
import backup.utils.StrUtils;

/**
 * Tela para exibição dos dados sobre uma Unidade de Backup selecionada na
 * tela principal.
 * @author Leandro Aparecido de Almeida
 */
final class BackupDriveDetailsDialog extends javax.swing.JDialog {

    /**
     * Criar uma instância de <b>BackupDriveDetailsDialog</b>.
     * @param parent tela proprietária.
     * @param backupDrive Unidade de Backup selecionada na tela principal.
     */
    public BackupDriveDetailsDialog(java.awt.Frame parent, Drive backupDrive) {
        super(parent, true);
        initComponents();
        jlIdentifier.setText(backupDrive.getIdentifier());
        jlLabel.setText(!backupDrive.getLabel().equals("") ? backupDrive.getLabel() : backupDrive.toString());
        jlInstallationTime.setText(StrUtils.formatDate1(backupDrive.getInstallationTime()));
        String lastBackupTime = StrUtils.formatDate1(backupDrive.getLastBackupInfo()
        .getTime());
        if (backupDrive.getLastBackupInfo().isPartial()) {
            lastBackupTime += " (Backup Parcial)";
        }
        jlLastBackupTime.setText(lastBackupTime);        
        try {
            jlTotalSpace.setText(StrUtils.formatBytes(backupDrive.getTotalSpace()));
        } catch (Exception ex) {
            jlTotalSpace.setText("[indefinido]");
        }
        try {
            jlFreeSpace.setText(StrUtils.formatBytes(backupDrive.getFreeSpace()));
        } catch (Exception ex) {
            jlFreeSpace.setText("[indefinido]");
        }
        try {
            jlFileSystemFormat.setText(backupDrive.getFileSystemFormat());
        } catch (Exception ex) {
            jlFileSystemFormat.setText("[indefinido]");
        }
        jlRoot.setText(backupDrive.getRoot().toString());        
        setLocationRelativeTo(parent);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel2 = new javax.swing.JLabel();
        jlLabel = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jlInstallationTime = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jlLastBackupTime = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jlFreeSpace = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jlRoot = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jlTotalSpace = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jlFileSystemFormat = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator2 = new javax.swing.JSeparator();
        jSeparator3 = new javax.swing.JSeparator();
        jSeparator4 = new javax.swing.JSeparator();
        jSeparator5 = new javax.swing.JSeparator();
        jSeparator6 = new javax.swing.JSeparator();
        jSeparator7 = new javax.swing.JSeparator();
        jSeparator8 = new javax.swing.JSeparator();
        jSeparator9 = new javax.swing.JSeparator();
        jLabel9 = new javax.swing.JLabel();
        jlIdentifier = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("SOBRE A UNIDADE DE BACKUP");
        setResizable(false);

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(0, 0, 102));
        jLabel2.setText("RÓTULO:");

        jlLabel.setText("[nome_unidade]");

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(0, 0, 102));
        jLabel3.setText("DATA DA INSTALAÇÃO:");

        jlInstallationTime.setText("[data_instalacao]");

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(0, 0, 102));
        jLabel4.setText("DATA DO ULT. BACKUP:");

        jlLastBackupTime.setText("[data_ult_backup]");

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(0, 0, 102));
        jLabel5.setText("ESPAÇO DISPONÍVEL:");

        jlFreeSpace.setText("[espaco_livre]");

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(0, 0, 102));
        jLabel7.setText("DIRETÓRIO RAIZ:");

        jlRoot.setText("[diretorio_raiz]");

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(0, 0, 102));
        jLabel6.setText("ESPAÇO TOTAL:");

        jlTotalSpace.setText("[espaco_total]");

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(0, 0, 102));
        jLabel8.setText("FORMATO:");

        jlFileSystemFormat.setText("[sistema_arquivos]");

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(0, 0, 102));
        jLabel9.setText("IDENTIFICADOR:");

        jlIdentifier.setText("[sistema_arquivos]");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator9, javax.swing.GroupLayout.PREFERRED_SIZE, 454, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel9)
                        .addGap(73, 73, 73)
                        .addComponent(jlIdentifier))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jSeparator7)
                        .addComponent(jSeparator6)
                        .addComponent(jSeparator5)
                        .addComponent(jSeparator4)
                        .addComponent(jSeparator3)
                        .addComponent(jSeparator2)
                        .addComponent(jSeparator1)
                        .addComponent(jSeparator8, javax.swing.GroupLayout.PREFERRED_SIZE, 454, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel3)
                                .addComponent(jLabel2)
                                .addComponent(jLabel4)
                                .addComponent(jLabel6)
                                .addComponent(jLabel5)
                                .addComponent(jLabel7)
                                .addComponent(jLabel8))
                            .addGap(37, 37, 37)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jlFileSystemFormat)
                                .addComponent(jlRoot)
                                .addComponent(jlFreeSpace)
                                .addComponent(jlTotalSpace)
                                .addComponent(jlLastBackupTime)
                                .addComponent(jlLabel)
                                .addComponent(jlInstallationTime)))))
                .addContainerGap(30, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(jSeparator9, javax.swing.GroupLayout.PREFERRED_SIZE, 1, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(jlIdentifier))
                .addGap(10, 10, 10)
                .addComponent(jSeparator8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jlLabel))
                .addGap(10, 10, 10)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jlInstallationTime))
                .addGap(10, 10, 10)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 1, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jlLastBackupTime))
                .addGap(10, 10, 10)
                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(jlTotalSpace))
                .addGap(10, 10, 10)
                .addComponent(jSeparator4, javax.swing.GroupLayout.PREFERRED_SIZE, 1, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jlFreeSpace))
                .addGap(10, 10, 10)
                .addComponent(jSeparator5, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(jlRoot))
                .addGap(10, 10, 10)
                .addComponent(jSeparator6, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(jlFileSystemFormat))
                .addGap(10, 10, 10)
                .addComponent(jSeparator7, javax.swing.GroupLayout.PREFERRED_SIZE, 1, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(30, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JSeparator jSeparator6;
    private javax.swing.JSeparator jSeparator7;
    private javax.swing.JSeparator jSeparator8;
    private javax.swing.JSeparator jSeparator9;
    private javax.swing.JLabel jlFileSystemFormat;
    private javax.swing.JLabel jlFreeSpace;
    private javax.swing.JLabel jlIdentifier;
    private javax.swing.JLabel jlInstallationTime;
    private javax.swing.JLabel jlLabel;
    private javax.swing.JLabel jlLastBackupTime;
    private javax.swing.JLabel jlRoot;
    private javax.swing.JLabel jlTotalSpace;
    // End of variables declaration//GEN-END:variables
}
