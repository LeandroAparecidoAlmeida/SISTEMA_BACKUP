package backup.drive;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Classe para leitura e escrita de arquivos de texto.
 * @author Leandro Aparecido de Almeida
 */
final class TextFile {
    
    /**Arquivo de texto a ser processado.*/
    private final File file;

    /**
     * Criar uma instancia de <b>TextFile</b>.
     * @param file arquivo a ser processado.
     */
    public TextFile(File file) {
        this.file = file;
    }

    public File getFile() {
        return file;
    }

    /**
     * Ler o texto do arquivo de texto.
     * @param charset charset do texto.
     * @return texto do arquivo.
     */
    public String read(String charset) throws FileNotFoundException, IOException {
        try (FileInputStream istr = new FileInputStream(file);
        ByteArrayOutputStream ostr = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[4096];
            int length;
            while ((length = istr.read(buffer)) > 0) {
                ostr.write(buffer, 0, length);
                ostr.flush();
            }
            String text = ostr.toString(charset);
            return text;
        }
    }
    
    /**
     * Gravar o texto no arquivo de texto.
     * @param text texto a ser gravado no arquivo.
     * @param charset charset do texto.
     * @param append se true, vai concatenar o arquivo, senão regrava o
     * arquivo com o novo texto.
     */
    public void write(String text, String charset, boolean append) throws 
    FileNotFoundException, IOException {
        try (FileOutputStream ostr = new FileOutputStream(file, append)) {
            ostr.write(text.getBytes(charset));
            ostr.flush();
        }
    }
    
}