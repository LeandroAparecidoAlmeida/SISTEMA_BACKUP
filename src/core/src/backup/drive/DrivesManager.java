package backup.drive;

import backup.system.Application;
import backup.system.OSDetector;
import java.lang.reflect.Constructor;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.nio.file.spi.FileSystemProvider;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe para o gerenciamento de unidades de armazenamento de dados. Uma unidade
 * de armazenamento de dados é qualquer dispositivo de memória não-volátil conectado
 * ao computador incluindo HD interno, HD's externos, cartões de memória, pendrives,
 * dispositivos SSD, etc.<br><br>
 * No contexto do programa, as unidades de armazenamento de dados serão divididas
 * em três categorias, a saber:<br><br>
 * <ol>
 * <li><i>Partições do HD interno:</i> o HD interno pode estar formatado em uma
 * ou mais partições. No contexto do programa, qualquer destas partições do HD
 * poderão ser utilizadas como origem dos arquivos para backup, ou como destino
 * para o restore.</li>
 * <li><i>Unidades de armazenamento instaladas:</i> uma unidade de armazenamento
 * de dados estará instalada se nela foi implantado um conjunto de arquivos de
 * controle, na raiz da mesma. Nesse contexto ela é chamada de <b>Unidade de Backup</b>,
 * pois sua finalidade será servir como destino para os arquivos de backup do
 * usuário.</li>
 * <li><i>Unidades de armazenamento não-instaladas:</i> Se os arquivos de controle
 * não estiverem implantados, a unidade de armazenamento não estará instalada.</li>
 * </ol>
 * @author Leandro Aparecido de Almeida
 */
public final class DrivesManager {

    /**Instância de {@link FileSystemProvider}.*/
    private static final FileSystemProvider fsProvider;
    /**Instância de {@link FileSystem}.*/
    private static final FileSystem fileSystem;
    /**Objeto para instânciação da subclasse adequada de {@link Drive},
     de acordo com a plataforma de Sistema Operacional.*/
    private static Constructor<?> constructor;
    /**Lista de todas as unidades de armazenamento de dados do computador.*/
    private static final List<Drive> drivesList;
    
    static {
        fsProvider = FileSystemManager.getProvider();
        fileSystem = FileSystemManager.getFileSystem(); 
        drivesList = new ArrayList<>();
        Class<?> driveClass = null;        
        if (OSDetector.isWindows()) {
            driveClass = WindowsDrive.class;
        } else if (OSDetector.isUnix()) {
            //driveClass = WindowsDrive.class;
        }
        try {
            constructor = driveClass.getDeclaredConstructor(Path.class);
        } catch (Exception ex) {
            Application.catchException(
                null,
                DriverManager.class,
                ex
            );
        }              
    }
    
    //Constructor private. Não permite a instânciação da classe.
    private DrivesManager() {
    }
        
    /**
     * Verificar e atualizar a lista de unidades de armazenamento de dados.
     */
    private static List<Drive> updateDrivesList() {
        synchronized (drivesList) {
            List<Drive> pluggedDrivesList = new ArrayList<>();
            List<Drive> ejectedDrivesList = new ArrayList<>();
            List<Path> rootDirectoriesList = new ArrayList<>();
            for (Path root : fileSystem.getRootDirectories()) {
                rootDirectoriesList.add(root);
            }
            //Detecta unidades de armazenamento de dados removidas.
            for (Drive drive : drivesList) {
                boolean ejected = true;
                for (Path root : rootDirectoriesList) {
                    if (drive.getRoot().equals(root)) {
                        ejected = false;
                        break;
                    }
                }
                if (ejected) {
                    ejectedDrivesList.add(drive);
                }
            }
            if (!ejectedDrivesList.isEmpty()) {
                for (Drive drive : ejectedDrivesList) {
                    drivesList.remove(drive);
                } 
            }
            //Detecta unidades de armazenamento de dados inseridas.
            for (Path root : rootDirectoriesList) {
                    boolean plugged = true;
                    for (Drive drive : drivesList) {
                        if (drive.getRoot().equals(root)) {
                            plugged = false;
                            break;
                        }
                    }
                    if (plugged) {
                        try {
                            if (!fsProvider.getFileStore(root).isReadOnly()) {
                                Drive drive = (Drive)constructor.newInstance(root);
                                pluggedDrivesList.add(drive);
                            }                            
                        } catch (Exception ex) {
                        }
                    }          
            }
            if (!pluggedDrivesList.isEmpty()) {
                for (Drive drive : pluggedDrivesList) {
                    drivesList.add(drive);
                }
                drivesList.sort(new DriveComparator());
            }
            return drivesList;
        }
    } 
    
    /**
     * Obter a lista de todas as unidades de armazenamento de dados que estão
     * instaladas (Unidades de Backup).
     */
    public static List<Drive> getInstalledDrives() {
        List<Drive> list0 = updateDrivesList();
        List<Drive> list1 = new ArrayList<>();
        for (Drive drive : list0) {
            if (!drive.isBackupSourceDrive()) {
                if (drive.isInstalled()) {
                    list1.add(drive);
                }
            }
        }
        return list1;
    }
    
    /**
     * Obter a lista de todas as unidades de armazenamento de dados que não estão
     * instaladas. Não se incluem nesta lista as partições do HD interno.
     */
    public static List<Drive> getNonInstalledDrives() {
        List<Drive> list0 = updateDrivesList();
        List<Drive> list1 = new ArrayList<>();
        for (Drive drive : list0) {
            if (!drive.isBackupSourceDrive()) {
                if (!drive.isInstalled()) {
                    list1.add(drive);
                }
            }
        }
        return list1;
    }
    
    /**
     * Obter a lista de todas as unidades de armazenamento de dados instaladas
     * e não instaladas (inclusive partições do HD interno).
     */
    public static List<Drive> getAllDrives() {
        return updateDrivesList();
    }
    
    /**
     * Obter a lista de todas as unidades de armazenamento de dados que são
     * partições do HD interno.
     */
    public static List<Drive> getHardDiskPartitions() {
        List<Drive> list0 = updateDrivesList();
        List<Drive> list1 = new ArrayList<>();
        for (Drive drive : list0) {
            if (drive.isBackupSourceDrive()) {
                list1.add(drive);
            }
        }
        return list1;
    }
    
    /**
     * Obter a unidade de armazenamento de dados identificada pelo diretório
     * raiz (root).
     * @param root diretório raiz da unidade de armazenamento de dados.
     * @return unidade de armazenamento de dados associada, ou null, caso
     * não haja associação.
     */
    public static Drive getDrive(Path root) {
        synchronized (drivesList) {
            Drive ref = null;        
            for (Drive drive : drivesList) {
                if (drive.getRoot().equals(root)) {
                    ref = drive;
                    break;
                }
            }
            return ref;
        }
    }
    
}