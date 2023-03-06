package backup.drive;

/**
 * Ouvinte de conexão de unidade de armazenamento de dados à portas USB.
 * @author Leandro Aparecido de Almeida
 */
public interface DriveInsertionListener {
    /**
     * Notificação de que uma unidade de armazenamento de dados foi inserida
     * a uma porta USB.
     * @param drive Unidade de armazenamento de dados inserida.
     */
    public void drivePlugged(Drive drive);
    /**
     * Notificação de que uma unidade de armazenamento de dados foi removida
     * de uma porta USB.
     * @param drive Unidade de armazenamento de dados removida.
     */
    public void driveEjected(Drive drive);
}