package backup.drive;

import java.util.Date;

/**
 * Classe para status do último backup realizado na Unidade de Backup.
 * @author Leandro Aparecido de Almeida
 */
public final class LastBackupData {
    
    /**Data do último Backup na Unidade de Backup.*/
    private Date time;
    /**Status de backup parcial dos arquivos.*/
    private boolean partial;

    public LastBackupData() {
    }

    public LastBackupData(Date time, boolean partial) {
        this.time = time;
        this.partial = partial;
    }
    
    /**Obter a data do último backup realizado na Unidade de Backup.*/
    public Date getTime() {
        return time;
    }

    /**Status de backup parcial para o último backup realizado na Unidade de Backup.*/
    public boolean isPartial() {
        return partial;
    }

    /**
     * Definir a data do último backup realizado na Unidade de Backup.
     * @param backupTime data do backup. 
     */
    void setTime(Date backupTime) {
        this.time = backupTime;
    }

    /**
     * Definir o status de backup parcial para o último backup realizado na
     * Unidade de backup.
     * @param partialBackup status de backup parcial dos arquivos.
     */
    void setPartial(boolean partialBackup) {
        this.partial = partialBackup;
    }

}