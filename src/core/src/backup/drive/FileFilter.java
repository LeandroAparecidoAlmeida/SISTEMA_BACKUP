package backup.drive;

import java.io.File;

/**
 * Filtro padrão para seleção de arquivos em disco.
 */
final class FileFilter implements java.io.FileFilter {
    
    /**Extensões de arquivos aceitas.*/
    private final String[] extensions;

    /**
     * Criar uma instância de <b>FileFilter</b>. Pode-se definir quais extensões
     * de arquivos serão aceitas, passando-se cada uma delas como parâmetro.<br>
     * Não passando qualquer parâmetro, significa que vai aceitar qualquer arquivo,
     * independentemente da sua extensão.
     * @param extensions lista das extensões de arquivos aceitas.<br>
     * Ex.:<br>
     * FileFilter filter = new FileFilter(".txt", ".dat", ".xls");
     */
    public FileFilter(String ... extensions) {
        this.extensions = new String[extensions.length];
        for (int i = 0; i < extensions.length; i++) {
            String ext = extensions[i];
            this.extensions[i] = (ext.startsWith(".") ? ext : ".".concat(ext));
        }
    }
    
    /**
     * Aceita somente se no parâmetro têm-se uma referência para um arquivo e
     * se este arquivo têm uma extensão aceita, de acordo com a lista passada
     * no constructor.
     * @param pathname arquivo ou diretório.
     * @return <b>true</b> se no parâmetro está se passando um arquivo e com
     * uma extensão aceita, <b>false</b> se é um diretório ou um arquivo com
     * uma extensão inválida.
     */
    @Override
    public boolean accept(File pathname) {
        if (!pathname.isFile()) return false;        
        if (extensions.length == 0) return true;
        boolean aceito = false;
        for (String ext : extensions) {
            if (pathname.getAbsolutePath().toLowerCase().endsWith(ext
            .toLowerCase())) {
                aceito = true;
                break;
            }
        }                
        return aceito;
    }       
    
}