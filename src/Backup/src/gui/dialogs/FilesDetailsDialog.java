package gui.dialogs;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;
import javax.swing.JOptionPane;
import backup.utils.StrUtils;

/**
 * Tela para exibir os detalhes dos arquivos selecionados na JTable da tela
 * principal.
 * @author Leandro Aparecido de Almeida
 */
final class FilesDetailsDialog extends javax.swing.JDialog {
    
    /**Lista dos arquivos selecionados na tela principal.*/
    private final File[] files;
    /**Índice do último arquivo.*/
    private final int lastFile;
    /**Cursor para o arquivo na posição atual.*/
    private int cursor;

    /**
     * Criar uma instância de <b>FilesDetailsDialog</b>.
     * @param parent frame proprietário.
     * @param file lista dos arquivos selecionados na JTable da tela principal.
     */
    public FilesDetailsDialog(java.awt.Frame parent, File... files) {
        super(parent, true);
        initComponents();
        this.files = files;
        lastFile = files.length - 1;      
        setLocationRelativeTo(parent);
        putCursor(0);
    }
    
    /**
     * Imprimir o arquivo na posição atual do cursor.
     */
    private void printSelectedFileDetails() {
        try {
            setTitle("ARQUIVO " + String.valueOf(cursor + 1) +
            " DE " + String.valueOf(lastFile + 1));
            File file = files[cursor];
            String name = file.getAbsolutePath();
            BasicFileAttributes attrs = Files.readAttributes(file.toPath(), 
            BasicFileAttributes.class);
            String creationTime = StrUtils.formatDate1(new Date(attrs.creationTime().toMillis()));
            String lastModifiedTime = StrUtils.formatDate1(new Date(attrs.lastModifiedTime().toMillis()));
            String lastAccessTime = StrUtils.formatDate1(new Date(attrs.lastAccessTime().toMillis()));
            String size = StrUtils.formatBytes(attrs.size());
            StringBuilder sb = new StringBuilder();
            sb.append(name);
            sb.append("\n\n\nData Criação......:  ");
            sb.append(creationTime);
            sb.append("\n\nÚltima Alteração..:  ");
            sb.append(lastModifiedTime);
            sb.append("\n\nÚltimo Acesso.....:  ");
            sb.append(lastAccessTime);
            sb.append("\n\nTamanho...........:  ");
            sb.append(size);            
            jtaFileDetails.setText(sb.toString());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                this,
                ex.getMessage(),
                "Erro",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
    
    /**
     * Mover o cursor e exibir as informações do arquivo naquela posição.
     * @param position nova posição do cursor.
     */
    private void putCursor(int position) {
        cursor = position;
        jbFirst.setEnabled(true);
        jbPrior.setEnabled(true);
        jbNext.setEnabled(true);
        jbLast.setEnabled(true);
        if (cursor == 0) {
            jbFirst.setEnabled(false);
            jbPrior.setEnabled(false);
        } 
        if (cursor == lastFile) {
            jbLast.setEnabled(false);
            jbNext.setEnabled(false);
        }
        printSelectedFileDetails();
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
        jScrollPane1 = new javax.swing.JScrollPane();
        jtaFileDetails = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("DETALHES DO ARQUIVO");
        setResizable(false);

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

        jScrollPane1.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        jtaFileDetails.setEditable(false);
        jtaFileDetails.setBackground(new java.awt.Color(240, 240, 240));
        jtaFileDetails.setColumns(20);
        jtaFileDetails.setFont(new java.awt.Font("Courier New", 0, 17)); // NOI18N
        jtaFileDetails.setLineWrap(true);
        jtaFileDetails.setRows(5);
        jScrollPane1.setViewportView(jtaFileDetails);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 660, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 29, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 385, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jbFirstActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbFirstActionPerformed
        moveToFirst();
    }//GEN-LAST:event_jbFirstActionPerformed

    private void jbPriorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbPriorActionPerformed
        moveToPrior();
    }//GEN-LAST:event_jbPriorActionPerformed

    private void jbNextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbNextActionPerformed
        moveToNext();
    }//GEN-LAST:event_jbNextActionPerformed

    private void jbLastActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbLastActionPerformed
        moveToLast();
    }//GEN-LAST:event_jbLastActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JButton jbFirst;
    private javax.swing.JButton jbLast;
    private javax.swing.JButton jbNext;
    private javax.swing.JButton jbPrior;
    private javax.swing.JTextArea jtaFileDetails;
    // End of variables declaration//GEN-END:variables

}