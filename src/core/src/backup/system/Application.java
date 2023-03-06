package backup.system;

import java.awt.Component;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.swing.JOptionPane;

/**
 * Classe para controle de execução do programa.
 * @author Leandro Aparecido de Almeida
 */
public final class Application {

    /**Constructor private. Não permite a instânciação da classe.*/
    private Application() {
    }

    /**
     * Exibir mensagem de erro capturado em background.
     * @param component componente proprietário.
     * @param sourceClass classe de origem do erro.
     * @param ex exceção capturada.
     */
    public static void catchException(Component component, Class sourceClass,
    Exception ex) {
        JOptionPane.showMessageDialog(
            component,
            sourceClass.getName() + ": " + ex,
            "Erro",
            JOptionPane.ERROR_MESSAGE
        );
    }
    
    /**
     * Extrair um arquivo inserido como recurso no .jar do projeto.
     * @param resourcePath caminho do arquivo de recurso.
     * @param destinationFile arquivo de destino aonde será extraído o
     * arquivo de recurso.
     */
    public static void extractResource(String resourcePath, File destinationFile) throws IOException {
        try (InputStream inputStream = Application.class.getResourceAsStream(resourcePath); 
        FileOutputStream outputStream = new FileOutputStream(destinationFile)) {
            byte[] buffer = new byte[1024];
            int length;
            while((length = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, length);
                outputStream.flush();
            }
        }
    }
    
}