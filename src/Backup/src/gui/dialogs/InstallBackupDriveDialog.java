package gui.dialogs;

import java.awt.Component;
import java.awt.Cursor;
import java.util.List;
import java.util.TimerTask;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import java.util.Timer;
import backup.drive.Drive;
import backup.drive.DrivesManager;

/**
 * Tela para instalação de uma Unidade de Backup.
 * @author Leandro Aparecido de Almeida
 */
final class InstallBackupDriveDialog extends javax.swing.JDialog  {

    /**Lista das unidades de armazenamento de dados não instaladas.*/
    private List<Drive> nonInstalledDrivesList;

    /**
     * Criar uma instância de <b>InstallBackupDriveDialog</b>.
     * @param parent tela proprietária.
     */
    public InstallBackupDriveDialog(java.awt.Frame parent) {
        super(parent, true);
        initComponents();
        listNonInstalledDrives();
        listFileSystemFormats();
        jlMessage.setVisible(false);
        jtfLabel.setEnabled(false);
        setLocationRelativeTo(parent);
    }
    
    /**
     * Listar os sistemas de arquivos disponíveis para a plataforma.
     */
    private void listFileSystemFormats() {
        Drive selectedDrive = getSelectedDrive();
        if (selectedDrive != null) {
            String[] fileSystemList = selectedDrive.getFileSystemList();
            String[] list = new String[selectedDrive.getFileSystemList().length + 1];
            list[0] = "[NÃO FORMATAR]";
            for (int i = 0; i < fileSystemList.length; i++) {
                list[i + 1] = fileSystemList[i];
            }
            DefaultComboBoxModel model = new DefaultComboBoxModel(list);
            jcbFileSystem.setModel(model);
            jcbFileSystem.setSelectedIndex(0);
            jcbFileSystem.setEnabled(true);
        } else {
            DefaultComboBoxModel model = new DefaultComboBoxModel();
            jcbFileSystem.setModel(model);
            jcbFileSystem.setEnabled(false);
        }
    }
    
    /**
     * Listar as unidades de armazenamento de dados não instaladas no JCombobox.
     */
    private void listNonInstalledDrives() {
        setCursor(new Cursor(Cursor.WAIT_CURSOR));
        nonInstalledDrivesList = DrivesManager.getNonInstalledDrives();
        String[] items = new String[nonInstalledDrivesList.size()];
        for (int i = 0; i < nonInstalledDrivesList.size(); i++) {
            items[i] = nonInstalledDrivesList.get(i).getLetter();
        }
        DefaultComboBoxModel model = new DefaultComboBoxModel(items);
        jcbDrive.setModel(model);   
        if (jcbDrive.getItemCount() > 0) {
            jcbDrive.setSelectedIndex(0);
            jbInstall.setEnabled(true);            
        } else {
            jcbDrive.setSelectedIndex(-1);
            jbInstall.setEnabled(false);                
        }
        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }
    
    /**
     * Instalar a unidade de armazenamento de dados selecionada na JCombobox.
     * Caso o formato do sistema de arquivos do dispositivo não seja compatível 
     * com o que é que requerido pela plataforma, será necessário formatá-lo antes
     * de instalar. Isso poderá ser feito no programa mesmo, se o usuário assim
     * o autorizar, confirmando a solicitação de formatação.
     */
    private void installBackupDrive() {
        Component _this = this;
        new Thread() {
            @Override
            public void run() {
                setCursor(new Cursor(Cursor.WAIT_CURSOR));
                try {
                    boolean install = true;
                    jtfLabel.setEnabled(false);
                    jcbDrive.setEnabled(false);
                    jbInstall.setEnabled(false);
                    jbCancel.setEnabled(false);                    
                    Drive selectedDrive = getSelectedDrive();                    
                    if (jcbFileSystem.getSelectedIndex() != 0) {
                        //Vai formatar o dispositivo.
                        String fileSystem = (String) jcbFileSystem.getSelectedItem();
                        int opt = JOptionPane.showConfirmDialog(
                            _this,
                            "Confirma a formatação da Unidade de Backup para\n" +
                            "o formato " + fileSystem + "?",
                            "Atenção!",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE
                        );                        
                        if (opt == JOptionPane.YES_OPTION) {
                            if (jtfLabel.getText().equals("")) {
                                throw new Exception("Defina um rótulo para o dispositivo");
                            }
                            jlMessage.setText("Formatando drive...");
                            jlMessage.setVisible(true);
                            String label = jtfLabel.getText();
                            selectedDrive.format(label, fileSystem);
                        } else if (opt == JOptionPane.NO_OPTION || opt == JOptionPane.CLOSED_OPTION) {
                            install = false;
                        }
                    }
                    if (install) {
                        //Instala a Unidade de Backup.
                        jlMessage.setVisible(true);
                        selectedDrive.install();
                        jlMessage.setText("Instalação concluída!");
                        TimerTask task = new TimerTask() {
                            @Override
                            public void run() {                
                                setVisible(false);
                            }
                        };
                        Timer timer = new java.util.Timer();
                        timer.schedule(task, 2500, 100000);
                    } else {
                        jtfLabel.setEnabled(true);
                        jcbDrive.setEnabled(true);
                        jbInstall.setEnabled(true);
                        jbCancel.setEnabled(true);
                    }
                } catch (Exception ex) {
                    jlMessage.setVisible(false);
                    jcbDrive.setEnabled(true);
                    jbInstall.setEnabled(true);
                    jbCancel.setEnabled(true);
                    checkSelectedDriveFormat();
                    JOptionPane.showMessageDialog(
                        _this,
                        ex.getMessage(),
                        "Erro",
                        JOptionPane.ERROR_MESSAGE
                    );
                }
                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        }.start();
    }
    
    /**
     * Verificar se o formato de sistema de arquivos do drive selecionado é
     * um formato válido.
     */
    private void checkSelectedDriveFormat() {
        Drive selectedDrive = getSelectedDrive();
        if (selectedDrive != null) {
            jtfLabel.setText(selectedDrive.getLabel());
        } else {
            jtfLabel.setText("");
        }
        listFileSystemFormats();
    }
    
    /**Obter a Unidade de Armazenamento de Dados selecionada na lista.*/
    private Drive getSelectedDrive() {
        Drive selectedDrive = null;
        if (!nonInstalledDrivesList.isEmpty()) {
            selectedDrive = nonInstalledDrivesList.get(
                jcbDrive.getSelectedIndex()
            );
        }
        return selectedDrive;
    }

    /**
     * Obter a Unidade de Backup instalada. <b>Null</b>, indica que não houve
     * a instalação.
     */
    public Drive getInstalledDrive() {
        Drive selectedDrive = getSelectedDrive();
        Drive installedDrive = null;
        if (selectedDrive != null) {
            if (selectedDrive.isInstalled()) {
                installedDrive = DrivesManager.getDrive(
                    selectedDrive.getRoot()
                );
            }
        }
        return installedDrive;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jcbDrive = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        jtfLabel = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jcbFileSystem = new javax.swing.JComboBox<>();
        jbCancel = new javax.swing.JButton();
        jbInstall = new javax.swing.JButton();
        jlMessage = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("INSTALAR UNIDADE DE BACKUP");
        setResizable(false);

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel1.setText("DRIVE:");

        jcbDrive.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jcbDrive.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcbDriveActionPerformed(evt);
            }
        });

        jLabel2.setText("RÓTULO:");

        jLabel3.setText("FORMATAR:");

        jcbFileSystem.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jcbFileSystem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcbFileSystemActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addComponent(jLabel1))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jcbFileSystem, 0, 131, Short.MAX_VALUE)
                    .addComponent(jcbDrive, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jtfLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 202, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(25, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jcbDrive, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(jtfLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jcbFileSystem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(153, Short.MAX_VALUE))
        );

        jbCancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons16/icon7.png"))); // NOI18N
        jbCancel.setText("Cancelar");
        jbCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbCancelActionPerformed(evt);
            }
        });

        jbInstall.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons16/icon6.png"))); // NOI18N
        jbInstall.setText("Instalar");
        jbInstall.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbInstallActionPerformed(evt);
            }
        });

        jlMessage.setForeground(java.awt.Color.blue);
        jlMessage.setText("Instalação concluída!");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jlMessage)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jbInstall, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jbCancel, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jbCancel)
                    .addComponent(jbInstall)
                    .addComponent(jlMessage))
                .addContainerGap())
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jbCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbCancelActionPerformed
        setVisible(false);
    }//GEN-LAST:event_jbCancelActionPerformed

    private void jbInstallActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbInstallActionPerformed
        installBackupDrive();
    }//GEN-LAST:event_jbInstallActionPerformed

    private void jcbDriveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcbDriveActionPerformed
        checkSelectedDriveFormat();
    }//GEN-LAST:event_jcbDriveActionPerformed

    private void jcbFileSystemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcbFileSystemActionPerformed
        if (jcbFileSystem.getSelectedIndex() != 0) {
            jtfLabel.setEnabled(true);
        } else {
            jtfLabel.setEnabled(false);
        }
    }//GEN-LAST:event_jcbFileSystemActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JButton jbCancel;
    private javax.swing.JButton jbInstall;
    private javax.swing.JComboBox<String> jcbDrive;
    private javax.swing.JComboBox<String> jcbFileSystem;
    private javax.swing.JLabel jlMessage;
    private javax.swing.JTextField jtfLabel;
    // End of variables declaration//GEN-END:variables

}