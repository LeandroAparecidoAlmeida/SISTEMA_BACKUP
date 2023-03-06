package backup.drive;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Parser do arquivo de log. Lê o arquivo e recupera os dados sobre o backup
 * realizado que estão gravados nele.
 */
public final class LogFileParser {
     
    /**Lista de metadados dos arquivos copiados/excluídos no backup.*/
    private final List<FileMetadata> filesMetadataList;
    /**Data e hora da realização do backup.*/
    private final Date backupTime;
    /**Status de backup parcial dos arquivos.*/
    private final boolean partialBackup;
    
    /**
     * Criar um instância de <b>LogFileParser</b>.
     * @param logFile arquivo de log do backup.
     * @param root diretório raiz da Unidade de Backup.
     */
    public LogFileParser(File logFile, Path root) throws FileNotFoundException,
    IOException {
        filesMetadataList = new ArrayList<>();
        String rootDir = root.toFile().getAbsolutePath();
        TextFile textFile = new TextFile(logFile);
        String text = textFile.read("UTF-8");
        String[] lines = text.split(System.lineSeparator());
        backupTime = new Date(lines[0].substring(6, lines[0].length()));
        partialBackup = Boolean.valueOf(lines[1].substring(9, lines[1].length()));
        for (int i = 2; i < lines.length; i++) {
            String[] fields = lines[i].split("\u0001");
            int mode = Integer.valueOf(fields[0]);
            String file = rootDir + fields[1];
            String srcFile = fields[2];
            Date creationTime = new Date(fields[3]);
            Date lastModifiedTime = new Date(fields[4]);
            long size= Long.valueOf(fields[5]);
            Date backTime = new Date(fields[6]);
            FileMetadata fileMetadata = new FileMetadata(
                file,
                srcFile,
                mode,
                creationTime,
                lastModifiedTime,
                size,
                backTime
            );
            filesMetadataList.add(fileMetadata);
        }
    }

    /**
     * Obter a data e hora da realização do backup.
     */
    public Date getBackupTime() {
        return backupTime;
    }

    /**Obter o status de backup parcial dos arquivos.*/
    public boolean isPartialBackup() {
        return partialBackup;
    }

    /**Obter a lista de metados dos arquivos submetidos ao processo de backup.*/
    public List<FileMetadata> getFilesMetadataList() {
        return filesMetadataList;
    }

}