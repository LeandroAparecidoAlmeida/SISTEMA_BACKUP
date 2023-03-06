package backup.drive;

import java.io.File;
import java.util.List;

/**
 * Ouvinte do processo de busca por atualizações de arquivos no sistema local.
 * @author Leandro Aparecido de Almeida
 */
public interface BackupUpdatesListener {
    /**
     * Notificação de conclusão do processo de busca por atualizações de 
     * arquivos no sistema local.
     * @param createdFilesList lista dos arquivos criados no sistema local e que
     * serão copiados para a Unidade de Backup.
     * @param deletedFilesList lista dos arquivos que serão removidos na Unidade
     * de Backup.
     * @param overwrittenFilesList lista dos arquivos alterados no sistema local
     * e que deverão ser atualizados na Unidade de Backup.
     */
    public void listUpdatedFiles(final List<File> createdFilesList, 
    final List<File> deletedFilesList, final List<File> overwrittenFilesList);    
}