package backup.drive;

/**
 * Exceção para tratamento dos erros no processamento do backup/restore dos arquivos.
 * @author Leandro Aparecido de Almeida
 */
public final class BackupException extends Exception {
    public BackupException(String message) {
        super(message);
    }
}