package backup.drive;

import java.io.File;

/**
 * Ouvinte de restauração dos arquivos. Um ouvinte recebe todas as notificações
 * sobre o processamento do restore em cada etapa.
 */
public interface RestoreListener {    
    /**
     * Notificação de ínicio do restore dos arquivos.
     * @param numberOfFiles número de arquivos do restore.
     */
    public void restoreInitialized(int numberOfFiles);
    /**
     * Notificação da conclusão do restore dos arquivos com a sincronização dos
     * conteúdos na Unidade de Backup e a partição de destino.
     */
    public void restoreDone();  
    /**
     * Notificação do processamento de uma etapa do restore.
     * @param fileNumber número do arquivo em processo de restore.
     * @param file arquivo em processo de restore.
     */
    public void processingFile(int fileNumber, File file);
    /**
     * Notificação de cancelamento do processo de restore em alguma etapa por
     * motivo de erro ocorrido no processamento.
     * @param ex exceção associada.
     */
    public void restoreAbortedByError(Exception ex);
    /**
     * Notificação de cancelamento do processo de restore em alguma etapa por
     * decisão do próprio usuário.
     */
    public void restoreAbortedByUser();
}