package backup.drive;

import java.io.File;

/**
 * Ouvinte de backup dos arquivos. Um ouvinte recebe todas as notificações sobre
 * o processamento do backup em cada etapa.
 */
public interface BackupListener {
    /**
     * Notificação de ínicio do backup dos arquivos.
     * @param numberOfFiles número de arquivos do backup.
     */
    public void backupInitialized(int numberOfFiles);
    /**
     * Notificação da conclusão do backup dos arquivos com a sincronização dos
     * conteúdos na Unidade de Backup e o disco local.
     */
    public void backupDone(); 
    /**
     * Notificação do processamento de uma etapa do backup.
     * @param fileNumber número do arquivo em processo de backup.
     * @param file arquivo em processo de backup.
     * @param mode 1 - cópia, 2 - exclusão, 3 - sobrescrita.
     */
    public void processingFile(int fileNumber, File file, int mode);
    /**
     * Notificação de cancelamento do processo de backup em alguma etapa por
     * motivo de erro ocorrido no processamento.
     * @param ex exceção associada.
     */
    public void backupAbortedByError(Exception ex);
    /**
     * Notificação de cancelamento do processo de backup em alguma etapa por
     * decisão do próprio usuário.
     */
    public void backupAbortedByUser();       
}