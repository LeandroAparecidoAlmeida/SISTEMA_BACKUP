package main;

import gui.dialogs.MainWindow;
import javax.swing.JOptionPane;
import backup.installer.Installer;
import java.io.File;

public class Main {
    
    static {
        System.setProperty("version.date", "08 de Maio de 2022");
        System.setProperty("version.author", "Leandro A. Almeida");
        System.setProperty("version.number", "1.0");
        try {
            String rootDir = new File(Main.class.getProtectionDomain().getCodeSource()
            .getLocation().toURI()).getParent() + File.separator;
            System.setProperty("rootdir", rootDir);
        } catch (Exception ex) {
        }
    }
    
    /**
     * Iniciar a execução do programa.
     * @param args não trata qualquer argumento externo.
     */
    public static void main(String args[]) {
        try {
            //UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            if (args.length > 0) {
                if (args[0].equals("/i")) {
                    new Installer().install();
                    System.exit(0);
                }
            }
            java.awt.EventQueue.invokeLater(() -> {
                new MainWindow().setVisible(true);
            });
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                null,
                ex.toString(),
                "Erro ao inicializar",
                JOptionPane.ERROR_MESSAGE
            );
            System.exit(1);
        }
    }
    
}