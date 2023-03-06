package backup.drive;

import java.io.File;
import java.util.Comparator;

/**
 * Classe para ordenação de listas de arquivos e diretórios.
 */
final class FileComparator implements Comparator<File> {
    
   @Override
   public int compare(File o1, File o2) {
       return o1.getAbsolutePath().compareTo(o2.getAbsolutePath());
   }
   
}