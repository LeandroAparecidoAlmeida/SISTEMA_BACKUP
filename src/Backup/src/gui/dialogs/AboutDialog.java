package gui.dialogs;

/**
 * Tela com os créditos da versão.
 * @author Leandro Aparecido de Almeida
 */
final class AboutDialog extends javax.swing.JDialog {

    public AboutDialog(java.awt.Frame parent) {
        super(parent, true);
        initComponents();
        jlVersionDate.setText(System.getProperty("version.date"));
        jlAuthor.setText(System.getProperty("version.author"));        
        jlVersionNumber.setText(System.getProperty("version.number"));        
        setLocationRelativeTo(parent);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel6 = new javax.swing.JLabel();
        jlAuthor = new javax.swing.JLabel();
        jlTitleAuthor = new javax.swing.JLabel();
        jlTitleVersion = new javax.swing.JLabel();
        jlVersionDate = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jlTitleVersionNumber = new javax.swing.JLabel();
        jlVersionNumber = new javax.swing.JLabel();

        jLabel6.setText("jLabel6");

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("SOBRE");
        setBackground(new java.awt.Color(255, 255, 255));
        setResizable(false);

        jlAuthor.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jlAuthor.setForeground(new java.awt.Color(102, 102, 102));
        jlAuthor.setText("[autor]");

        jlTitleAuthor.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jlTitleAuthor.setText("AUTOR:");

        jlTitleVersion.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jlTitleVersion.setText("DATA DA VERSÃO:");

        jlVersionDate.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jlVersionDate.setForeground(new java.awt.Color(102, 102, 102));
        jlVersionDate.setText("[data]");

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel9.setText("DATA DA VERSÃO:");

        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel11.setText("AUTOR:");

        jlTitleVersionNumber.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jlTitleVersionNumber.setText("Nº DA VERSÃO:");

        jlVersionNumber.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jlVersionNumber.setForeground(new java.awt.Color(102, 102, 102));
        jlVersionNumber.setText("[versao]");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jlVersionNumber, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jlAuthor, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jlVersionDate, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jlTitleVersionNumber)
                    .addComponent(jLabel9)
                    .addComponent(jlTitleVersion)
                    .addComponent(jlTitleAuthor)
                    .addComponent(jLabel11))
                .addContainerGap(273, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel9)
                    .addComponent(jlTitleVersion))
                .addGap(15, 15, 15)
                .addComponent(jlVersionDate)
                .addGap(32, 32, 32)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jlTitleAuthor)
                    .addComponent(jLabel11))
                .addGap(15, 15, 15)
                .addComponent(jlAuthor)
                .addGap(32, 32, 32)
                .addComponent(jlTitleVersionNumber)
                .addGap(15, 15, 15)
                .addComponent(jlVersionNumber)
                .addContainerGap(24, Short.MAX_VALUE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jlAuthor;
    private javax.swing.JLabel jlTitleAuthor;
    private javax.swing.JLabel jlTitleVersion;
    private javax.swing.JLabel jlTitleVersionNumber;
    private javax.swing.JLabel jlVersionDate;
    private javax.swing.JLabel jlVersionNumber;
    // End of variables declaration//GEN-END:variables
}
