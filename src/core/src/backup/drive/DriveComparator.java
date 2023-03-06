package backup.drive;

import java.util.Comparator;

/**
 * Classe para a ordenação de listas de unidades de armazenamento de dados.
 * @author Leandro Aparecido de Almeida
 */
final class DriveComparator implements Comparator<Drive> {
    @Override
    public int compare(Drive o1, Drive o2) {
        String path1 = o1.getLetter();
        String path2 = o2.getLetter();
        return path1.compareTo(path2);
    }    
}