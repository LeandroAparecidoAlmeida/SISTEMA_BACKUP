package backup.drive;

import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.spi.FileSystemProvider;

/**
 * Classe para gerenciamento do acesso ao sistema de arquivos.
 * @author Leandro Aparecido de Almeida
 */
final class FileSystemManager {
    
    /**Instância de FileSystemProvider para acesso ao sistema de arquivos.*/
    private static final FileSystemProvider fsProvider;
    /**Instância de FileSystem para informações sobre o sistema de arquivos.*/
    private static final FileSystem fileSystem;
    
    static {
        fileSystem = FileSystems.getDefault();
        fsProvider = fileSystem.provider();
    }
    
    //Constructor private. Não permite a instânciação da classe.
    private FileSystemManager() {
    }

    /**Obter a instância de {@link FileSystemProvider} para acesso ao sistema
    de arquivos.*/
    public static FileSystemProvider getProvider() {
        return fsProvider;
    }

    /**Obter a instância de {@link FileSystem} para informações sobre o sistema 
    de arquivos.*/
    public static FileSystem getFileSystem() {
        return fileSystem;
    }

}