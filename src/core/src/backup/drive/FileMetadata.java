package backup.drive;

import java.util.Date;

/**
 * Metadados de um arquivo sob controle de backup.
 * @author Leandro Aparecido de Almeida
 */
public final class FileMetadata {
    
    /**Operação realizada com o arquivo no backup.*/
    private final int operation;
    /**Caminho do arquivo na Unidade de Backup.*/
    private final String targetPath;
    /**Caminho do arquivo no sistema local.*/
    private final String sourcePath;
    /**Data da criação do arquivo.*/
    private final Date creationTime;
    /**Data do backup do arquivo.*/
    private final Date backupTime;
    /**Data da última modificação no arquivo.*/
    private final Date lastModifiedTime;
    /**Tamanho do arquivo (em bytes).*/
    private final long size;

    /**
     * Criar uma instância de <b>FileMetadata</b>.
     * @param targetPath caminho do arquivo na Unidade de Backup (destino).
     * @param sourcePath caminho do arquivo no sistema local (origem).
     * @param operation operação realizada com o arquivo:<br>
     * <ol>
     * <li>arquivo criado no sistema local e copiado para a Unidade de Backup;</li>
     * <li>arquivo alterado no sistema local e sobrescrito na Unidade de Backup;</li>
     * <li>arquivo excluído no sistema local e excluído na Unidade de Backup.</li>
     * </ol>
     * @param creationTime data da criação do arquivo.
     * @param lastModifiedTime data da última modificação no arquivo.
     * @param size tamanho do arquivo (em bytes).
     * @param backupTime data do backup do arquivo.
     */
    public FileMetadata(String targetPath, String sourcePath, int operation,
    Date creationTime, Date lastModifiedTime, long size, Date backupTime) {
        this.targetPath = targetPath;
        this.sourcePath = sourcePath;
        this.operation = operation;
        this.creationTime = creationTime;
        this.lastModifiedTime = lastModifiedTime;
        this.size = size;
        this.backupTime = backupTime;
    }

    /**Obter o caminho do arquivo na Unidade de Backup (destino).*/
    public String getTargetPath() {
        return targetPath;
    }

    /**Obter o caminho do arquivo no sistema local (origem).*/
    public String getSourcePath() {
        return sourcePath;
    }

    /**
     * Obter a operação realizada com o arquivo:<br>
     * <ol>
     * <li>arquivo criado no sistema local e copiado para a Unidade de Backup;</li>
     * <li>arquivo alterado no sistema local e sobrescrito na Unidade de Backup;</li>
     * <li>arquivo excluído no sistema local e excluído na Unidade de Backup.</li>
     * </ol>
     * @return operação realizada com o arquivo.
     */
    public int getOperation() {
        return operation;
    }

    /**Obter a data da criação do arquivo.*/
    public Date getCreationTime() {
        return creationTime;
    }

    /**Obter a data da última modificação no arquivo.*/
    public Date getLastModifiedTime() {
        return lastModifiedTime;
    }

    /**Obter a data do backup do arquivo.*/
    public Date getBackupTime() {
        return backupTime;
    }

    /**Obter o tamanho do arquivo (em bytes).*/
    public long getSize() {
        return size;
    }

}