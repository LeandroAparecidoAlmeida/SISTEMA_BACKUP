package backup.drive;

import java.util.Comparator;

/**
 * Classe para ordenação de listas de arquivos e diretórios.
 */
final class FileNameComparator implements Comparator<String> {
    
   @Override
   public int compare(String o1, String o2) {
       return o1.compareTo(o2);
   }
   
}