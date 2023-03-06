package backup.drive;

import backup.system.Application;
import java.io.File;
import java.io.IOException;
import java.nio.file.spi.FileSystemProvider;

/**
 * Classe para controle de segurança do processo de restauração. A operação de
 * restauração não poderá ser parcial, ou seja, se ela for iniciada, deve ser
 * concluída. Dessa forma, enquanto o último arquivo não for copiado para a partição
 * de destino, ela deverá ficar pendente, sem opção de realizar outras operações
 * com quaisquer outras Unidades de Backup a não ser a de origem do restore. Isso
 * é feito para garantir a integridade dos arquivos, haja vista que um restore
 * parcial seguido de um backup na mesma Unidade de Backup resultará na exclusão
 * dos arquivos que não foram copiados no restore, havendo assim perda de dados.
 * @author Leandro Aparecido de Almeida
 */
public final class SafeRestore {
 
    /**Nome do arquivo de controle de restauração.*/
    public static final String RESTORE_FILE_NAME = "RestoreInfo.backupdrive.tmp";
    /**Arquivo de controle de restauração na raiz do sistema.*/
    private static final File controlFile;
    /**Instância de {@link FileSystemProvider} para acesso ao sistema de arquivos.*/
    private static final FileSystemProvider fsProvider;
    /**Identificador (id) da Unidade de Backup de origem da restauração.*/
    private static String sourceDriveId;
    /**Letra da partição de destino da restauração.*/
    private static String targetDriveId;
    /**Status de restauração pendente.*/
    private static boolean pendingRestore;
    
    static {
        controlFile = new File(System.getProperty("rootdir") + RESTORE_FILE_NAME);
        fsProvider = FileSystemManager.getProvider();
        pendingRestore = false;
        sourceDriveId = null;
        targetDriveId = null;
        try {
            //Se o arquivo de controle existe no diretório raiz do sistema,
            //faz a leitura deste arquivo para recuperar os dados sobre a
            //restauração pendente.
            if (controlFile.exists()) {
                pendingRestore = true;
                TextFile textFile = new TextFile(controlFile);
                String text = textFile.read("UTF-8");
                String[] lines = text.split(System.lineSeparator()); 
                sourceDriveId = lines[0].substring(14, lines[0].length());
                targetDriveId = lines[1].substring(14, lines[1].length());                
            }
        } catch (Exception ex) {
            Application.catchException(
                null,
                SafeRestore.class,
                ex
            );
        }
    }

    //Constructor private. Não permite a instânciação da classe.
    private SafeRestore() {
    }

    /**
     * Sinaliza o início de uma operação de restore. Este controle é feito com a
     * criação do arquivo de restore no diretório raiz do sistema. Esse arquivo
     * contém os dados sobre a Unidade de Backup de origem da restauração e a
     * partição de destino dos arquivos. Enquanto a restauração não estiver 
     * completa o arquivo deve ser mantido na raiz do sistema só sendo removido,
     * através da chamada ao método {@link #restoreDone()}, quando o último arquivo
     * for copiado para a partição de destino.
     * @param sourceDrive Unidade de Backup de origem da restauração.
     * @param targetDrive partição do HD de destino da restauração.
     */
    static void restoreInitialized(Drive sourceDrive, Drive targetDrive) throws Exception {
        if (!pendingRestore) {
            sourceDriveId = sourceDrive.getIdentifier();
            targetDriveId = targetDrive.getLetter();
            StringBuilder sb = new StringBuilder();
            sb.append("SourceDriveId=");
            sb.append(sourceDriveId);
            sb.append(System.lineSeparator());
            sb.append("TargetDriveId=");
            sb.append(targetDriveId);
            TextFile textFile = new TextFile(controlFile);
            textFile.write(sb.toString(), "UTF-8", false);        
            sourceDrive.setHiddenFileAttribute(controlFile, true);
            pendingRestore = true;
        }
    }
    
    /**
     * Sinaliza a conclusão do processo de restore. A chamada a esse método deverá
     * ser feita quando todos os arquivos foram copiados para a partição de
     * destino, liberando dessa forma o programa para as funcionalidades normais.<br>
     * O término do controle se dá com a remoção do arquivo de restore do diretório
     * raiz do sistema.
     */
    static void restoreDone() throws IOException {
        fsProvider.deleteIfExists(controlFile.toPath());
        sourceDriveId = null;
        targetDriveId = null;
        pendingRestore = false;        
        System.gc();
    }
    
    /**Obter o status de restauração pendente.*/
    public static boolean isPendingRestore() {
        return pendingRestore;
    }
    
    /**Obter o identificador (id) da Unidade de Backup de origem da restauração.*/
    public static String getSourceDriveId() {
        return sourceDriveId;
    }
    
    /**Obter a letra da partição de destino da restauração.*/
    public static String getTargetDriveId() {
        return targetDriveId;
    }

}