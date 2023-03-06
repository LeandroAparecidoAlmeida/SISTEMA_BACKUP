package backup.drive;

import java.io.File;
import java.io.FileFilter;

/**
 * Filtro padrão para seleção de diretórios em disco.
 */
final class DirectoryFilter implements FileFilter {    
    /**
     * Aceita somente se no parâmetro têm-se uma referência para um diretório.
     * @param pathname arquivo ou diretório.
     * @return true, se no parâmetro está se passando um diretório, false, se é
     * um arquivo.
     */
    @Override
    public boolean accept(File pathname) {
        return pathname.isDirectory();
    }    
}