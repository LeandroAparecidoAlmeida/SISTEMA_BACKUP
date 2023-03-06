package backup.drive;

import backup.system.Application;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Classe para monitoramento das unidades de armazenamento de dados que são 
 * inseridas/removidas em portas USB do computador. Quando uma unidade é inserida,
 * essa classe detecta e notifica aos ouvintes de conexão de unidade de armazenamento
 * de dados (intância de {@link DriveInsertionListener}) sobre o evento ocorrido.
 * Da mesma forma, quando uma unidade é removida de uma porta USB essa classe 
 * também faz a notificação para os ouvintes sobre a remoção do dispositivo.
 */
public final class DrivesMonitor {
    
    /**Lista de ouvintes de conexão de unidade de armazenamento de dados.*/
    private static final List<DriveInsertionListener> listeners = new ArrayList<>();
    /**Lista de unidades de armazenamento de dados.*/
    private static final List<Drive> drivesList = new ArrayList<>();
    /**Instância de {@link Timer} para o execução periódica da verificação.*/
    private static Timer timer;

    //Constructor private. Não permite a instânciação da classe.
    private DrivesMonitor() {
    }
    
    /**
     * Iniciar a verificação por unidades de armazenamento de dados
     * inseridas/removidas em portas USB.
     */
    public static void start() {
        timer = new java.util.Timer();
        timer.schedule(new Task(), 0, 2000);
    }
    
    /**
     * Interromper a verificação por unidades de armazenamento de dados
     * inseridas/removidas em portas USB.
     */
    public static void stop() {
        timer.cancel();
        timer.purge();
        timer = null;
    }
    
    /**
     * Notificar os ouvintes sobre a inserção de novas unidades de armazenamento
     * de dados à portas USB do computador.
     * @param drivesList lista das unidades de armazenamento de dados inseridas.
     */
    private static void notifyDrivePluggedEvent(List<Drive> drivesList) {
        for (Drive drive : drivesList) {            
            for (DriveInsertionListener listener : listeners) {
                listener.drivePlugged(drive);
            }
        }
    }
    
    /**
     * Notificar os ouvintes sobre a remoção de unidades de armazenamento de dados
     * que estavam conectadas à portas USB do computador.
     * @param drivesList lista das unidades de armazenamento de dados removidas.
     */
    private static void notifyDriveEjectedEvent(List<Drive> drivesList) {
        for (Drive drive : drivesList) {            
            for (DriveInsertionListener listener : listeners) {
                listener.driveEjected(drive);                    
            }
        }
    }
    
    /**
     * Adicionar um ouvinte de conexão de unidade de armazenamento de dados.
     * @param listener ouvinte a ser adicionado.
     */
    public static void addDriveInsertionListener(DriveInsertionListener listener) {
        synchronized (listeners) {
            if (!listeners.contains(listener)) {
                listeners.add(listener);
            }
        }
    }
    
    /**
     * Remover um ouvinte de conexão de unidade de armazenamento de dados.
     * @param listener ouvinte a ser removido.
     * @return true, o ouvinte foi removido, false, não foi removido.
     */
    public static boolean removeDriveInsertionListener(DriveInsertionListener listener) {
        synchronized (listeners) {
            int idx = -1;
            for (int i = 0; i < listeners.size(); i++) {
                if (listeners.get(i) == listener) {
                    idx = i;
                    break;
                }
            }
            if (idx >= 0) {
                listeners.remove(idx);
                return true;
            } else {
                return false;
            }
        }
    }
    
    /**
     * Classe interna para execução da tarefa de monitoramento das portas
     * USB. Essa tarefa é executada em períodos de tempo determinados na
     * criação do Timer.
     */
    private static class Task extends TimerTask {     
        @Override
        public void run() {
            try {
                List<Drive> allDrivesList = DrivesManager.getAllDrives();
                List<Drive> pluggedDrivesList = new ArrayList<>();
                List<Drive> ejectedDrivesList = new ArrayList<>();
                for (Drive drive : allDrivesList) {
                    if (!drivesList.contains(drive)) {
                        pluggedDrivesList.add(drive);                    
                    }
                }           
                for (Drive drive : drivesList) {
                    if (!allDrivesList.contains(drive)) {
                        ejectedDrivesList.add(drive);                    
                    }
                }
                boolean changed = false;
                if (!pluggedDrivesList.isEmpty()) {
                    notifyDrivePluggedEvent(pluggedDrivesList);
                    changed = true;
                }
                if (!ejectedDrivesList.isEmpty()) {
                    notifyDriveEjectedEvent(ejectedDrivesList);
                    changed = true;
                }
                if (changed) {
                    drivesList.clear();
                    drivesList.addAll(allDrivesList);
                }
            } catch (Exception ex) {
                Application.catchException(
                    null,
                    DrivesMonitor.class,
                    ex
                );
            }
        }   
    }
    
}