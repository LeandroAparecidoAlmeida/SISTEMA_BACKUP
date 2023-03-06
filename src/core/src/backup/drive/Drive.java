package backup.drive;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileStore;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.spi.FileSystemProvider;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.jdom2.JDOMException;

/**
 * Classe que representa uma <b>Unidade de Armazenamento de Dados</b>. Uma unidade
 * de armazenamento de dados pode ser qualquer dispositivo de memória acessível
 * ao computador como pendrive, HD interno, HD Externo, Memory Card, etc.<br>
 * Essa classe é abstract, pois uma parte de suas funcionalidades é dependente
 * da plataforma de Sistema Operacional subjacente, por exemplo, o método para 
 * formatar o dispositivo de memória. Tais funcionalidades devem ser implementadas
 * nas subclasses especializadas que dela irão herdar.<br>
 * No contexo do programa, uma unidade de armazenamento de dados pode ser instalada
 * para receber os arquivos de backup do usuário, passando então a ser designada
 * de <b>Unidade de Backup</b>. A instalação é feita com a implantação de um
 * conjunto de arquivos especiais que serão criados no diretório raiz da unidade
 * de armazenamento de dados, através da chamada ao método {@link #install()}.<br> 
 * Pode ser que as subclasses sobrescrevam <i>install()</i> para extenderem a sua
 * funcionalidade, por exemplo, para fazer configurações adicionais no dispositivo,
 * mas a chamada ao método desta classe é requerida para que a instalação seja
 * efetuada e os arquivos de instalação implantados.
 * @author Leandro Aparecido de Almeida
 */
public abstract class Drive {
    
    /**Nome do diretório de configurações da instalação.*/  
    public final String CONFIG_DIRECTORY_NAME;
    /**Constante nome do arquivo de instalação da Unidade de Backup.*/
    public final String INSTALLATION_FILE_NAME = "BackupDriveInfo.xml";
    /**Constante nome do arquivo com os dados do último backup realizado.*/
    public final String BACKUP_INFO_FILE_NAME = "LastBackupData.xml";
    /**Constante nome do arquivo com a lista de diretórios para backup na 
    Unidade de Backup.*/
    public final String DIRECTORIES_FILE_NAME = "BackupDirectories.xml";
    /**Constante nome do arquivo Leia-me na raiz da Unidade de Backup.*/
    public final String READ_ME_FILE_NAME = "Leia-me.txt";
    /**Constante nome do diretório com os arquivos de log de backup realizados.*/
    public final String LOG_DIRECTORY_NAME = "History";
    /**Quebra de linha para arquivos em formato texto.*/
    public final String LINE_BREAK = "\r\n";
    /**Charset padrão.*/
    public final String DEFAULT_CHARSET = "UTF-8";
    /**Lista dos diretórios raiz na hierarquia dos diretórios locais para backup.*/
    private final List<File> rootDirectoriesList;
    /**Lista dos diretórios locais definidos para backup na Unidade de Backup.*/
    private final List<File> backupDirectoriesList;
    /**Lista dos diretórios a serem excluídos da Unidade de Backup.*/
    private final List<File> deletedDirectoriesList;
    /**Lista dos diretórios locais a serem criados na Unidade de Backup.*/
    private final List<File> createdDirectoriesList;
    /**Lista dos arquivos a serem excluídos da Unidade de Backup.*/
    private final List<File> deletedFilesList;
    /**Lista dos arquivos locais a serem copiados para a Unidade de Backup.*/
    private final List<File> createdFilesList;
    /**Lista dos arquivos locais a serem sobrescritos na Unidade de Backup.*/
    private final List<File> overwrittenFilesList; 
    /**Lista de registros de log que será salva em arquivo.*/
    private final List<FileMetadata> logEntriesList;
    /**Objeto para a manipulação de arquivos no sistema (cópia, exclusão, etc).*/
    final FileSystemProvider fsProvider;
    /**Objeto para informações sobre a unidade de armazenamento.*/
    FileStore fileStore;     
    /**Diretório raiz da unidade de armazenamento.*/
    final Path root;
    /**Arquivo de instalação da Unidade de Backup.*/
    private final File installationFile;
    /**Arquivo com informações sobre backup de arquivos.*/
    private final File lastBackupDataFile;
    /**Arquivo com a lista dos diretórios para backup na Unidade de Backup.*/
    private final File backupDirectoriesFile;
    /**Arquivo Leia-me.txt.*/
    private final File readMeFile;    
    /**Diretório de Log da Unidade de Backup.*/
    private final File logDirectory; 
    /**Diretório de configurações da instalação.*/
    private final File configDirectory;
    /**Informações do último backup.*/
    private final LastBackupData lastBackupData;
    /**Data da Instalação da Unidade de Backup.*/
    private Date installationTime;
    /**Identificador da Unidade de Backup.*/
    private String UID;
    /**Status de Unidade de Backup instalada.*/
    private boolean installed;             
    /**Status de cancelamento de processo pelo usuário.*/
    private boolean abortedByUser;
    /**Status de processamento das atualizações nos arquivos locais.*/
    private boolean processedUpdates;
    /**Total de bytes a liberar na Unidade de Backup.*/
    private long bytesToRelease;
    /**Total de bytes a gravar na Unidade de Backup.*/
    private long bytesToRecord;
    
    /**
     * Criar uma instancia de <b>Drive</b>.
     * @param root caminho do diretório raiz.
     */
    public Drive(Path root) throws IOException, FileNotFoundException,
    ClassNotFoundException, Exception {
        this.root = root;
        fsProvider = FileSystemManager.getProvider();
        fileStore = fsProvider.getFileStore(this.root); 
        rootDirectoriesList = new ArrayList<>();
        backupDirectoriesList = new ArrayList<>();
        deletedDirectoriesList = new ArrayList<>();        
        createdDirectoriesList = new ArrayList<>();
        deletedFilesList = new ArrayList<>();
        createdFilesList = new ArrayList<>();        
        overwrittenFilesList = new ArrayList<>();
        logEntriesList = new ArrayList<>();
        abortedByUser = false;  
        installed = false;
        lastBackupData = new LastBackupData();
        installationTime = null;
        CONFIG_DIRECTORY_NAME = ".BackupDriveInstallation" + File.separator + 
        getHddSerial() + File.separator;
        installationFile = new File(this.root.toString() + CONFIG_DIRECTORY_NAME + INSTALLATION_FILE_NAME);        
        lastBackupDataFile = new File(this.root.toString() + CONFIG_DIRECTORY_NAME + BACKUP_INFO_FILE_NAME);
        backupDirectoriesFile = new File(this.root.toString() + CONFIG_DIRECTORY_NAME + DIRECTORIES_FILE_NAME);        
        readMeFile = new File(this.root.toString() + READ_ME_FILE_NAME);
        logDirectory = new File(this.root.toString() + CONFIG_DIRECTORY_NAME + LOG_DIRECTORY_NAME);
        configDirectory = new File(this.root.toString() + CONFIG_DIRECTORY_NAME);
        if (installationFile.exists()) {
            installed = true;
            //Carregar os dados sobre a instalação do arquivo XML.
            {
                InstallationFile file = new InstallationFile(installationFile);
                installationTime = file.getInstallationTime();
                UID = file.getUID();
            }
            //Lê os dados do último backup realizado na Unidade de Backup.
            if (lastBackupDataFile.exists()) {
                LastBackupDataFile file = new LastBackupDataFile(lastBackupDataFile);
                LastBackupData data = file.getLastBackupData();
                lastBackupData.setTime(data.getTime());
                lastBackupData.setPartial(data.isPartial());
            }
            //Carrega a lista de diretórios de backup na Unidade de Backup.
            if (backupDirectoriesFile.exists()) {
                BackupDirectoriesFile file = new BackupDirectoriesFile(backupDirectoriesFile);
                List<File> directories = file.getBackupDirectoriesList();
                backupDirectoriesList.addAll(directories);
            }            
        } 
    }
    
    //Todos os métodos abstract abaixo necessitam de uma implementação
    //específica, de acordo com a plataforma de Sistema Operacional
    //subjacente, portanto, devem ser implementados nas classes especializadas
    //que herdarem de Drive.
    
    /**
     * Formatar o dispositivo de memória para o formato requerido pela
     * plataforma subjacente.
     * @param label rótulo do volume.
     * @param FS formato do sistema de arquivos.
     */
    public abstract void format(String label, String FileSystem, String... args) throws Exception;
    
    /**
     * Ejetar o dispositivo de memória.
     * @return true, o dispositivo foi ejetado, false, não foi ejetado.
     */
    public abstract boolean eject() throws Exception;
    
    /**
     * Retornar qual o sistema de arquivos recomendado de acordo com a plataforma
     * subjacente. 
     */
    abstract String preferredFileSystem();
    
    /**Obter a letra da unidade de armazenamento.*/
    public abstract String getLetter();
    
    /**Obter os formatos de arquivo diponíveis para a plataforma.*/
    public abstract String[] getFileSystemList();
    
    /**Obter o número serial do HD.*/
    public abstract String getHddSerial() throws Exception;
    
    /**
     * Ocultar/Mostrar um arquivo.
     * @param file arquivo a ser ocultado.
     * @param value true, oculta o arquivo, false, mostra o arquivo.
     */
    abstract void setHiddenFileAttribute(File file, boolean value) throws Exception;    
    
    /**
     * Definir o atributo somente leitura do arquivo.
     * @param file arquivo a ter o atributo modificado.
     * @param value true, o arquivo será somente leitura, false, não é.
     */
    abstract void setReadOnlyFileAttribute(File file, boolean value) throws Exception;
   
    /**
     * Verificar se o arquivo é somente leitura.
     * @param file arquivo a ser verificado.
     * @return true, o arquivo é somente leitura, false, não é.
     */
    abstract boolean isReadOnlyFile(File file) throws Exception;
    
    /**
     * Obter o arquivo de destino na Unidade de Backup.
     * @param sourceFile arquivo de origem no sistema local.
     * @return arquivo de destino na Unidade de Backup.
     */
    abstract File getTargetFile(File sourceFile);
    
    /**
     * Obter o caminho do arquivo refFile considerando-se que ele esteja
     * no mesmo drive de refDirectory.
     * @param refDirectory diretório de referência.
     * @param refFile arquivo de referência.
     * @return arquivo de referência.
     */
    abstract File getRelativeFile(File refDirectory, File refFile);
    
    /**
     * Extrair o caminho (path) do arquivo, independente de local de 
     * armazenamento.
     * @param file arquivo a ter o caminho extraído.
     */
    abstract String extractFilePath(File file);
    
    /**
     * Verificar se a unidade de armazenamento é uma partição do disco rígido local.
     * @return true, a unidade de armazenamento é uma partição do disco rígido
     * local, false, a unidade de armazenamento não é uma partição do disco 
     * rígido local.
     */
    public abstract boolean isBackupSourceDrive();
    
    /**
     * Obter um identificador único para a Unidade de Backup que será instalada.
     * O identificador é uma String alfanumérica com 16 caracteres, gerada por 
     * um processo pseudo-aleatório.
     * @return identificador único para a Unidade de Backup.
     */
    private String generateIdentifier() {
        final int length = 16;
        char[] alphabet = new char[] {
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
            'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'X', 'Y', 'W', 'Z',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'
        };
        char[] chars = new char[length];
        chars[0] = 'B';
        chars[1] = 'D';
        chars[2] = 'I';
        chars[3] = '#';
        SecureRandom rnd = new SecureRandom(SecureRandom.getSeed(16));
        for (int i = 5; i <= length; i++) {
            if (i%2 != 0) {
                chars[i-1] = alphabet[rnd.nextInt(26)];
            } else {
                chars[i-1] = alphabet[26 + rnd.nextInt(10)];
            }
        }
        return new String(chars);
    }
    
    /**
     * Inicializar os contadores para atualização dos arquivos para backup.
     */
    private void releaseCounters() {
        bytesToRecord = bytesToRelease = 0;
        abortedByUser = processedUpdates = false;
        rootDirectoriesList.clear();
        deletedDirectoriesList.clear();
        createdDirectoriesList.clear();
        createdFilesList.clear();
        deletedFilesList.clear();
        overwrittenFilesList.clear();        
    }

    /**
     * Instalar a Unidade de Backup. Isso é feito com a implantação dos arquivos
     * de controle no diretório raiz da unidade de armazenamento de dados.
     */
    public void install() throws IOException, BackupException, Exception {
        if (SafeRestore.isPendingRestore()) {
            throw new BackupException("Restauração de arquivos pendente.");
        }
        if (installed) {
            throw new BackupException("Este dipositivo já foi instalado.");
        }
        if (isBackupSourceDrive()) {
            throw new BackupException("Partições de origem de backup não podem ser instaladas.");
        }
        if (!configDirectory.exists()) {
            File parentDirectory = configDirectory.getParentFile();
            if (!parentDirectory.exists()) {
                createDirectory(parentDirectory);
                setHiddenFileAttribute(parentDirectory, true);
            }            
            createDirectory(configDirectory);
            setHiddenFileAttribute(configDirectory, true);
        }
        installationTime = new Date();
        UID = generateIdentifier();
        InstallationFile file = new InstallationFile(installationFile);
        file.save(installationTime, UID);
        saveLastBackupDataFile(installationTime, false);                
        setHiddenFileAttribute(installationFile, true);
        setHiddenFileAttribute(lastBackupDataFile, true);
        installed = true;
    }
    
    /**
     * Desinstalar a Unidade de Backup.
     * @param keepLogDirectory se true, preserva o diretório de log dos backups
     * após a desinstalação, se false, exclui o diretório de log dos backups.
     */
    public void uninstall(boolean keepLogDirectory) throws IOException,
    BackupException, Exception {
        if (SafeRestore.isPendingRestore()) {
            throw new BackupException("Restauração de arquivos pendente.");
        }
        if (!installed) {
            throw new BackupException("Dispositivo não está instalado.");
        }
        if (!keepLogDirectory) {
            List<File> logFiles = getLogFilesList();
            for (File logFile : logFiles) {
                deleteFile(logFile);
            }
            deleteFile(logDirectory);
        }            
        deleteFile(installationFile);
        deleteFile(lastBackupDataFile);
        deleteFile(backupDirectoriesFile);      
        deleteFile(readMeFile);
        backupDirectoriesList.clear();
        releaseCounters();
        installed = false;
    }
    
    /**
     * Gravar o arquivo "Leia-me" na raiz da Unidade de Backup.
     */
    private void createReadMeFile() throws IOException, Exception {
        deleteFile(readMeFile);
        StringBuilder sb = new StringBuilder();  
        List<File> allBackupDirectoriesList = getAllBackupDirectories();
        List<String> directoriesPath = new ArrayList<>(allBackupDirectoriesList.size());
        for (File directory : allBackupDirectoriesList) {
            directoriesPath.add(extractFilePath(directory));
        }
        directoriesPath.sort(new FileNameComparator());
        sb.append("Diretórios sob supervisão neste dispositivo de backup:");
        sb.append(LINE_BREAK);
        for (String path : directoriesPath) {
            sb.append(LINE_BREAK);
            sb.append("> ");
            sb.append(path);
        }
        sb.append(LINE_BREAK);
        sb.append(LINE_BREAK);
        sb.append("Não copie quaisquer arquivos para estes diretórios");
        sb.append(LINE_BREAK);
        sb.append("pois estes serão removidos da próxima vez que você");
        sb.append(LINE_BREAK);
        sb.append("realizar o backup.");
        TextFile textFile = new TextFile(readMeFile);
        textFile.write(sb.toString(), DEFAULT_CHARSET, false);    
    }
    
    /**
     * Gravar o arquivo com a lista dos diretórios locais para backup na raiz 
     * da Unidade de Backup.
     */
    private void saveBackupDirectoriesList() throws FileNotFoundException, 
    IOException, ClassNotFoundException, Exception {
        if (!backupDirectoriesList.isEmpty()) {
            FileComparator comparator = new FileComparator();
            backupDirectoriesList.sort(comparator);
            setHiddenFileAttribute(backupDirectoriesFile, false);
            BackupDirectoriesFile file = new BackupDirectoriesFile(backupDirectoriesFile);
            file.save(backupDirectoriesList);
            setHiddenFileAttribute(backupDirectoriesFile, true);
            createReadMeFile();
        } else {
            deleteFile(backupDirectoriesFile);
        }
    }
    
    /**
     * Inserir um registro de log.
     * @param mode modo de operação com o arquivo.
     * @param targetFile arquivo de destino do backup.
     * @param sourceFile arquivo de origem do backup.
     */
    private void insertLogEntry(int mode, File targetFile, File sourceFile) throws
    IOException {
        BasicFileAttributes attrs = Files.readAttributes(targetFile.toPath(),
        BasicFileAttributes.class);
        Date time1 = new Date(attrs.creationTime().toMillis());
        Date time2 = new Date(attrs.lastModifiedTime().toMillis());
        Date time3 = new Date();
        long size = attrs.size();
        FileMetadata fileMetadata = new FileMetadata(
            extractFilePath(targetFile),
            sourceFile.getAbsolutePath(),
            mode,
            time1,
            time2,
            size,
            time3
        );
        logEntriesList.add(fileMetadata);
    }
    
    /**
     * Gravar o arquivo de log do backup. Este arquivo será criado dentro do
     * diretório de log na raiz da Unidade de Backup.
     */
    private void saveLogFile(Date backupTime, boolean isPartialBackup) throws IOException,
    Exception {
        if (!logEntriesList.isEmpty()) {
            final String TOKEN = "\u0001";
            if (!logDirectory.exists()) {
                createDirectory(logDirectory);
                setHiddenFileAttribute(logDirectory, true);
            }
            StringBuilder sb = new StringBuilder();
            sb.append("Time: ");
            sb.append(backupTime.toGMTString());
            sb.append(LINE_BREAK);
            sb.append("Partial: ");
            sb.append(String.valueOf(isPartialBackup));
            for (FileMetadata fileMetadata : logEntriesList) {
                sb.append(LINE_BREAK);
                sb.append(String.valueOf(fileMetadata.getOperation()));
                sb.append(TOKEN);
                sb.append(fileMetadata.getTargetPath());
                sb.append(TOKEN);
                sb.append(fileMetadata.getSourcePath());
                sb.append(TOKEN);
                sb.append(fileMetadata.getCreationTime().toGMTString());
                sb.append(TOKEN);
                sb.append(fileMetadata.getLastModifiedTime().toGMTString());
                sb.append(TOKEN);
                sb.append(String.valueOf(fileMetadata.getSize()));
                sb.append(TOKEN);
                sb.append(fileMetadata.getBackupTime().toGMTString());
            }
            String name = String.valueOf(backupTime.getTime()) + ".log";
            File logFile = new File(logDirectory.getAbsolutePath() + 
            File.separator + name);
            TextFile textFile = new TextFile(logFile);
            textFile.write(sb.toString(), DEFAULT_CHARSET, false);
            setHiddenFileAttribute(logFile, true);
        }
    }
    
    /**
     * Gravar o arquivo de dados do backup na raiz da Unidade de Backup.
     */
    private void saveLastBackupDataFile(Date backupTime, boolean isPartialBackup) throws IOException,
    Exception {
        setHiddenFileAttribute(lastBackupDataFile, false);        
        lastBackupData.setTime(backupTime);
        lastBackupData.setPartial(isPartialBackup);
        LastBackupDataFile file = new LastBackupDataFile(lastBackupDataFile);
        file.save(lastBackupData);
        setHiddenFileAttribute(lastBackupDataFile, true);
    }
    
    /**
     * Verificar se o processo corrente foi abortado pelo usuário. 
     */
    private void checkProcessAbortedByUser() throws Exception {
        if (abortedByUser) {
            throw new Exception();
        }
    }
    
    /**
     * Verificar se houve uma alteração no arquivo de origem.
     * @param sourceFile arquivo de origem.
     * @param backupFile arquivo de backup.
     * @return true, houve modificação, false, não houve modificação.
     */
    private boolean updatedFile(File sourceFile, File backupFile) {        
        return sourceFile.lastModified() > backupFile.lastModified();
    }
    
    /**
     * Listar todos os arquivos e diretórios a serem copiados para a Unidade de
     * Backup e os que serão excluídos dela no processo de backup, calculando o 
     * total de bytes a gravar e a liberar no processo.
     * @param listeners ouvintes de atualizações para backup.
     */
    public final void checkUpdates(BackupUpdatesListener... listeners) throws BackupException {
        if (SafeRestore.isPendingRestore()) {
            throw new BackupException("Restauração de arquivos pendente.");
        }
        if (!installed) {
            throw new BackupException("Dispositivo não está instalado.");
        }
        boolean empty = true;
        for (File directory : backupDirectoriesList) {
            if (directory.exists()) {
                empty = false;
                break;
            }
        }
        if (empty) {
            return;
        }
        releaseCounters();
        boolean complete = false;
        try {
            FileFilter filesFilter = new FileFilter();
            for (File backupDirectory : backupDirectoriesList) {
                checkProcessAbortedByUser();
                if (!backupDirectory.exists()) continue;            
                //Lista os arquivos e subdiretórios do diretório para backup
                //a serem criados na Unidade de Backup.
                File[] files1 = backupDirectory.listFiles(filesFilter);
                for (File file : files1) {
                    //Arquivos na raiz do diretório para backup.
                    checkProcessAbortedByUser();
                    File targetFile = getTargetFile(file);
                    if (!targetFile.exists()) {
                        createdFilesList.add(file);
                        bytesToRecord += file.length();
                    } else {                        
                        if (updatedFile(file, targetFile)) {
                            overwrittenFilesList.add(file);
                            bytesToRecord += file.length() - targetFile.length();
                        }
                    }
                } 
                checkProcessAbortedByUser();
                List<File> subdirectories1 = getSubdirectories(backupDirectory);
                for (File subdirectory : subdirectories1) {   
                    checkProcessAbortedByUser();
                    File targetSubdirectory = getTargetFile(subdirectory);
                    if (!targetSubdirectory.exists()) {
                        createdDirectoriesList.add(subdirectory);
                    }
                    File[] files2 = subdirectory.listFiles(filesFilter);
                    for (File file : files2) {
                        //Arquivos na raiz de cada subdiretório do diretório para 
                        //backup.
                        checkProcessAbortedByUser();
                        File targetFile = getTargetFile(file);
                        if (!targetFile.exists()) {
                            createdFilesList.add(file);
                            bytesToRecord += file.length();
                        } else {
                            if (updatedFile(file, targetFile)) {
                                overwrittenFilesList.add(file);
                                bytesToRecord += file.length() - targetFile.length();
                            }
                        }
                    }                 
                }
                checkProcessAbortedByUser();
                File targetDirectory = getTargetFile(backupDirectory);
                if (!targetDirectory.exists()) {
                    //Os diretórios raiz na hierarquia do diretório de backup são
                    //colocados em lista separada para que sejam criados primeiro, 
                    //caso eles não tenham um correspondente na Unidade de Backup.
                    createdDirectoriesList.add(0, backupDirectory);                
                    File parent = backupDirectory.getParentFile();
                    while (parent != null) {
                        checkProcessAbortedByUser();
                        File directory = getTargetFile(parent);
                        if (!directory.exists()) {
                            if (!rootDirectoriesList.contains(parent)) {
                                rootDirectoriesList.add(0, parent);
                            }
                        }
                        parent = parent.getParentFile();
                    }
                    //Se não há correspondente na Unidade de Backup, não haverá
                    //arquivos e subdiretórios a excluir dela, por isso ignora
                    //as próximas etapas, passando já para a verificação do 
                    //próximo diretório.
                    continue;
                }
                checkProcessAbortedByUser();
                //Lista os arquivos e subdiretórios a excluir do diretório de
                //destino na Unidade de Backup.
                File[] files3 = targetDirectory.listFiles(filesFilter);
                for (File file : files3) {
                    checkProcessAbortedByUser();
                    //Arquivos na raiz do diretório de destino e sem equivalentes
                    //no diretório local.
                    File localFile = getRelativeFile(backupDirectory, file);
                    if (!localFile.exists()) {
                        deletedFilesList.add(file);
                        bytesToRelease += file.length();
                    }
                } 
                checkProcessAbortedByUser();
                List<File> subdirectories2 = getSubdirectories(targetDirectory);
                for (File subdirectory : subdirectories2) {
                    //Arquivos na raiz dos subdiretórios do diretório de destino e 
                    //sem equivalentes nos subdiretórios locais.
                    checkProcessAbortedByUser();                
                    File[] files4 = subdirectory.listFiles(filesFilter);
                    for (File file : files4) {
                        checkProcessAbortedByUser();
                        File localFile = getRelativeFile(backupDirectory, file);
                        if (!localFile.exists()) {
                            deletedFilesList.add(file);
                            bytesToRelease += file.length();
                        }
                    }
                    File localSubdirectory = getRelativeFile(backupDirectory,
                    subdirectory);
                    if (!localSubdirectory.exists()) {
                        deletedDirectoriesList.add(subdirectory);
                    }
                }
            }
            complete = true;
            processedUpdates = true;
            for (BackupUpdatesListener listener : listeners) {
                listener.listUpdatedFiles(
                    createdFilesList,
                    deletedFilesList,
                    overwrittenFilesList
                );
            }
        } catch (Exception ex) {
            if (!abortedByUser) {
                throw new BackupException(ex.getMessage());
            }
        } finally {
            if (!complete) {
                releaseCounters();
            }
        }
    }
    
    /**
     * Copiar arquivo em <b>sourceFile</b> para <b>targetFile</b>, 
     * sobrescrevendo-se ele já estiver criado.
     * @param sourceFile arquivo de origem a ser copiado.
     * @param targetFile arquivo de destino.
     */
    void copyFile(File sourceFile, File targetFile) throws FileNotFoundException,
    IOException, Exception {
        if (sourceFile.exists()) {
            if (isReadOnlyFile(targetFile)) {
                setReadOnlyFileAttribute(targetFile, false);
            }
            fsProvider.copy(
                sourceFile.toPath(),
                targetFile.toPath(),
                StandardCopyOption.REPLACE_EXISTING
            );
        }
    }
    
    /**
     * Excluir um arquivo.
     * @param file arquivo a ser excluído.
     * @return true, o arquivo foi excluído, false, o arquivo não foi excluído.
     */
    boolean deleteFile(File file) throws Exception {
        boolean deleted = false;
        if (isReadOnlyFile(file)) {
            setReadOnlyFileAttribute(file, false);
        }
        fsProvider.deleteIfExists(file.toPath());
        return deleted;
    }
    
    /**
     * Criar um diretório.
     * @param file diretório a ser criado.
     */
    void createDirectory(File file) throws IOException {
        fsProvider.createDirectory(file.toPath());
    }
    
    /**
     * Fazer o backup dos arquivos na Unidade de Backup.
     * @param listeners lista dos ouvintes de backup.
     */
    public final void performBackup(BackupListener ... listeners) throws BackupException,
    IOException, Exception {
        if (SafeRestore.isPendingRestore()) {
            throw new BackupException("Restauração de arquivos pendente.");
        }
        if (!installed) {
            throw new BackupException("Dispositivo não está instalado.");
        }
        abortedByUser = false;  
        //Se não processou as atualizações, processa.
        if (!processedUpdates) {
           checkUpdates();
        }
        try {
            boolean copyFiles = !abortedByUser && (!createdFilesList.isEmpty() || 
            !overwrittenFilesList.isEmpty() || !deletedFilesList.isEmpty());      
            if (copyFiles) {
                logEntriesList.clear();
                //Verifica se o volume de dados cabe no espaço vazio a ser alocado
                //na Unidade de Backup.
                if (fileStore.getUnallocatedSpace() <= (bytesToRecord - bytesToRelease)) {    
                    //Não há espaço livre suficiente em disco para gravar os arquivos
                    //novos. Lança um BackupException.
                    throw new BackupException("Espaço em disco insuficiente para " +
                    "realizar o backup dos arquivos.");
                }   
                int numberFiles = createdFilesList.size() + deletedFilesList.size() +
                overwrittenFilesList.size();
                //Notifica os ouvintes do início do backup.
                for (BackupListener listener : listeners) {
                    listener.backupInitialized(numberFiles);
                }
                int currentStep = 1;
                //Exclui os arquivos da Unidade de Backup, liberando espaço
                //para copiar novos arquivos.
                for (File file : deletedFilesList) {
                    checkProcessAbortedByUser();
                    for (BackupListener listener : listeners) {
                        listener.processingFile(currentStep, file, 2);
                    }
                    insertLogEntry(3, file, file);                    
                    deleteFile(file);                    
                    currentStep++;
                }
                //Exclui os diretórios da Unidade de Backup.                
                for (int i = deletedDirectoriesList.size() - 1; i >= 0 ; i--) {
                    checkProcessAbortedByUser();
                    File directory = deletedDirectoriesList.get(i);
                    deleteFile(directory);
                }
                //Cria os diretórios raiz das hierarquias dos diretórios para backup.
                for (File directory : rootDirectoriesList) {
                    checkProcessAbortedByUser();
                    File targetDirectory = getTargetFile(directory);
                    createDirectory(targetDirectory);
                } 
                //Cria os novos diretórios na Unidade de Backup, preparando
                //para receber novos arquivos.
                for (File directory : createdDirectoriesList) {
                    checkProcessAbortedByUser();
                    File targetDirectory = getTargetFile(directory);
                    createDirectory(targetDirectory);
                }
                //Copia os arquivos novos no disco local para a Unidade de Backup.
                for (File file : createdFilesList) {
                    checkProcessAbortedByUser();
                    File targetFile = getTargetFile(file);
                    for (BackupListener listener : listeners) {
                        listener.processingFile(currentStep, targetFile, 1);
                    }
                    copyFile(file, targetFile);
                    insertLogEntry(1, targetFile, file);  
                    currentStep++;
                } 
                //Copia os arquivos alterados no disco local para a Unidade de
                //Backup.
                for (File file : overwrittenFilesList) {
                    checkProcessAbortedByUser();
                    File targetFile = getTargetFile(file);
                    for (BackupListener listener : listeners) {
                        listener.processingFile(currentStep, targetFile, 3);
                    }
                    copyFile(file, targetFile);
                    insertLogEntry(2, targetFile, file);
                    currentStep++;
                }
            }            
            for (BackupListener listener : listeners) {
                listener.backupDone();
            }            
        } catch (Exception ex) {
            if (abortedByUser) {
                for (BackupListener listener : listeners) {
                    listener.backupAbortedByUser();
                }
            } else {
                for (BackupListener listener : listeners) {
                    listener.backupAbortedByError(ex);
                }
            }
        } finally {            
            Date date = new Date();
            boolean partial = abortedByUser;
            saveLastBackupDataFile(date, partial);
            saveLogFile(date, partial);
            releaseCounters();
        }
    }
    
    /**
     * Fazer a restauração dos arquivos da Unidade de Backup para o drive
     * de destino.
     * @param targetDrive drive de destino da restauração.
     * @param listeners ouvintes do processo de restauração dos arquivos.
     */
    public final void performRestore(Drive targetDrive, RestoreListener... listeners) {        
        try {
            abortedByUser = false;
            SafeRestore.restoreInitialized(this, targetDrive);
            List<File> directoriesTree = new ArrayList<>();
            List<File> restoreFiles = new ArrayList<>();
            for (File directory : backupDirectoriesList) {
                checkProcessAbortedByUser();
                File backupDirectory = Drive.this.getRelativeFile(root.toFile(), directory);
                directoriesTree.add(backupDirectory);
                List<File> subdirectories = new ArrayList<>();
                loadSubdirectoriesTree(subdirectories, backupDirectory);
                for (File subdirectory : subdirectories) {
                    directoriesTree.add(subdirectory);
                }
            }
            for (File directory : directoriesTree) {
                checkProcessAbortedByUser();
                if (directory.exists()) {
                    File[] files = directory.listFiles(new FileFilter());
                    if (files.length > 0) {
                        for (File file : files) {
                            File targetFile = Drive.this.getRelativeFile(
                                targetDrive.getRoot().toFile(),
                                file
                            );
                            if (!targetFile.exists()) {
                                restoreFiles.add(file);
                            }
                        }
                    }
                }
            }            
            for (File directory : directoriesTree) {
                if (directory.exists()) {
                    checkProcessAbortedByUser();
                    File targetDirectory = Drive.this.getRelativeFile(
                        targetDrive.getRoot().toFile(),
                        directory
                    );
                    if (!targetDirectory.exists()) {
                        createDirectory(targetDirectory);
                    }
                }
            }
            int currentStep = 1;
            for (RestoreListener listener : listeners) {
                listener.restoreInitialized(restoreFiles.size());
            }            
            for (File file : restoreFiles) {
                checkProcessAbortedByUser();
                File targetFile = Drive.this.getRelativeFile(
                    targetDrive.getRoot().toFile(),
                    file
                );
                copyFile(file, targetFile);
                for (RestoreListener listener : listeners) {
                    listener.processingFile(currentStep, file);
                }
                currentStep++;
            }            
            for (RestoreListener listener : listeners) {
                listener.restoreDone();
            }
            SafeRestore.restoreDone();
        } catch (Exception ex) {
            if (abortedByUser) {
                for (RestoreListener listener : listeners) {
                    listener.restoreAbortedByUser();
                }
            } else {
                for (RestoreListener listener : listeners) {
                    listener.restoreAbortedByError(ex);
                }
            }            
        }
    }
    
    /**
     * Adicionar um novo diretório à lista de diretórios para backup na Unidade
     * de Backup.
     * @param directories novos diretórios para backup.
     */
    public final void insertBackupDirectories(File ... directories) throws BackupException,
    IOException, FileNotFoundException, ClassNotFoundException, Exception {  
        if (SafeRestore.isPendingRestore()) {
            throw new BackupException("Restauração de arquivos pendente.");
        }
        if (!installed) {
            throw new BackupException("Dispositivo não está instalado.");
        }
        List<File> allBackupDirectoriesList = getAllBackupDirectories();
        List<File> invalidDirectories = new ArrayList<>();
        boolean inserted, addedDirectories = false;
        String path1, path2;
        File parent;
        for (File directory : directories) {
            inserted = false;
            //Verificar se um diretório com o mesmo nome já está sob o controle
            //de backup para impedir mesclagem de diretórios.
            path1 = extractFilePath(directory);
            for (File backupDir : allBackupDirectoriesList) {                        
                path2 = extractFilePath(backupDir);
                if (path1.equals(path2)) {
                    invalidDirectories.add(directory);
                    inserted = true;
                    break;
                }
            }
            if (inserted) continue;
            //Verificar se dentro da hierarquia do diretório a ser inserido,
            //um dos diretórios pais já está sob controle de backup. Caso esteja,
            //o diretório já estará sob controle de backup.
            parent = directory;
            while (((parent = parent.getParentFile()) != null) && !inserted) {
                path1 = extractFilePath(parent);
                for (File backupDir : allBackupDirectoriesList) {                        
                    path2 = extractFilePath(backupDir);
                    if (path1.equals(path2)) {
                        invalidDirectories.add(directory);
                        inserted = true;
                        break;
                    }
                }
            }
            if (inserted) continue;
            //Verificar se dentro da hierarquia de qualquer dos diretórios que já
            //estão sob controle de backup se o diretório a ser inserido é um dos
            //diretórios pai. Se for, também impede a inserção, pois deverá ser
            //removido o diretório de backup que tem o diretório a ser inserido
            //como pai, para depois ser inserido o diretório.
            path1 = extractFilePath(directory);
            for (File backupDir : allBackupDirectoriesList) {
                parent = backupDir;
                while ((parent = parent.getParentFile()) != null) {
                    path2 = extractFilePath(parent);
                    if (path1.equals(path2)) {
                        invalidDirectories.add(directory);
                        inserted = true;
                        break;
                    }
                }
                if (inserted) break;
            }
            if (!inserted) {
                backupDirectoriesList.add(directory);
                addedDirectories = true;
            }
        }
        if (addedDirectories) {
            saveBackupDirectoriesList();
        }
        if (!invalidDirectories.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            sb.append("Diretórios inválidos:");
            for (File directory : invalidDirectories) {
                sb.append("\n");
                sb.append(directory.getAbsolutePath());
            }            
            throw new BackupException(sb.toString());
        }
    }
    
    /**
     * Remover um ou mais diretórios da lista de diretórios para backup na 
     * Unidade de Backup.
     * @param directories lista de diretórios a serem removidos;
     */
    public final void deleteBackupDirectories(String ... directories) throws Exception, 
    BackupException, IOException, FileNotFoundException, ClassNotFoundException {
        if (SafeRestore.isPendingRestore()) {
            throw new BackupException("Restauração de arquivos pendente.");
        }
        if (!installed) {
            throw new BackupException("Dispositivo não está instalado.");
        }
        for (String directory : directories) {
            for (int i = 0; i < backupDirectoriesList.size(); i++) {
                File backupDir = backupDirectoriesList.get(i);
                if (directory.equals(backupDir.getAbsolutePath())) {
                    backupDirectoriesList.remove(i);
                    break;
                }
            }
        }
        saveBackupDirectoriesList();
    }
    
    /**
     * Obter todos os diretórios que estão sob controle de backup na unidade,
     * de todos os computadores que usam a mesma unidade de backup.
     * @return lista de todos os diretórios sob controle de backup.
     */
    private List<File> getAllBackupDirectories() throws IOException, JDOMException {
        List<File> allBackupDirectoriesList = new ArrayList<>();
        List<File> subDirectories = getSubdirectories(configDirectory.getParentFile());
        for (File subDirectory : subDirectories) {
            File directoriesFile = new File(subDirectory.getAbsolutePath() + 
            File.separator + DIRECTORIES_FILE_NAME);
            if (directoriesFile.exists()) {
                BackupDirectoriesFile file = new BackupDirectoriesFile(directoriesFile);
                List<File> backupDirectories = file.getBackupDirectoriesList();
                for (File backupDirectory : backupDirectories) {
                    allBackupDirectoriesList.add(backupDirectory);
                }
            }
        }
        return allBackupDirectoriesList;
    }
    
    /**
     * Obter a lista com todos os arquivos no diretório de log. 
     */
    public final List<File> getLogFilesList() {
        List<File> list = new ArrayList<>();
        if (logDirectory.exists()) {
            FileFilter filesFilter = new FileFilter(".log");
            File[] files = logDirectory.listFiles(filesFilter);
            if (files != null) {
                list.addAll(Arrays.asList(files));
                FileComparator comparator = new FileComparator();
                list.sort(comparator);
                return list;
            }
        } 
        return list;
    }
    
    /**
     * Método recursivo que recupera em uma lista todos os subdiretórios na
     * hierarquia de um diretório passado como parâmetro.
     * @param list lista dos subdiretórios.
     * @param parentDirectory diretório.
     * @return lista com todos os subdiretórios.
     */
    private void loadSubdirectoriesTree(List<File> list, File parentDirectory) {  
        File[] subdirectories = parentDirectory.listFiles(new DirectoryFilter());
        if (subdirectories != null) {
            for (File subdirectory : subdirectories) {
                list.add(subdirectory);
                loadSubdirectoriesTree(list, subdirectory);
            }
        }
    }
    
    /**
     * Obter a lista com todos os subdiretórios do diretório passado como
     * parâmetro.
     * @param parentDirectory diretório a obter os subdiretórios.
     * @return lista com os subdiretórios.
     */
    private List<File> getSubdirectories(File parentDirectory) {
        List<File> list = new ArrayList<>();
        loadSubdirectoriesTree(list, parentDirectory);
        return list;
    }
    
    /**
     * Obter a lista com os diretórios na raiz da unidade de armazenamento de
     * dados (não lista os subdiretórios).
     */
    public final File[] getRootDirectories() {
        File rootDir = root.toFile();
        return rootDir.listFiles(new DirectoryFilter());
    }
    
    /**
     * Verificar se o formato do sistema de arquivos da unidade de armazenamento
     * de dados é um formato válido.
     */
    public final boolean isPreferredFileSystemFormat() {
        return fileStore.type().toUpperCase().equals(preferredFileSystem()
        .toUpperCase());
    }
    
    /**
     * Obter o identificador da Unidade de Backup. O Identificador é uma string
     * com 16 caracteres de comprimento gerada por um processo pseudo-aleatório
     * durante o processo de instalação da Unidade de Backup.
     */
    public final String getIdentifier() {
        return UID;
    }
    
    /**
     * Status de instalada. Uma Unidade de Backup está instalada se o arquivo 
     * de instalação se encontra na raiz desta Unidade.
     * @return <b>true</b> se a Unidade de Backup está instalada, <b>false</b>
     * se ela não está instalada.
     */
    public final boolean isInstalled() {
        return installed;
    }
    
    /**
     * Obter o rótulo do dispositivo.
     */
    public final String getLabel() {
        return fileStore.name();
    }

    /**Obter a data da instalação da Unidade de Backup.*/
    public final Date getInstallationTime() {
        return installationTime;
    }

    /**Obter o caminho do diretório raiz da Unidade de Backup.*/
    public final Path getRoot() {
        return root;
    }

    /**Cancelar o processamento dos arquivos.*/
    public final void abortProcess() {
        abortedByUser = true;
    }

    /**Obter o status de processo cancelado pelo usuário.*/
    public final boolean processAbortedByUser() {
        return abortedByUser;
    }

    /**Obter objeto com o status do último backup realizado na Unidade de Backup.*/
    public final LastBackupData getLastBackupInfo() {
        return lastBackupData;
    }
    
    /**Obter a lista com os diretórios sob controle de backup na Unidade de Backup.*/
    public final List<File> getBackupDirectoriesList() {
        return backupDirectoriesList;
    }
    
    /**Retornar o número de bytes não alocados (livres) na unidade de armazenamento
    de dados.*/
    public final long getFreeSpace() throws IOException {
        return fileStore.getUnallocatedSpace();
    }
    
    /**Retornar o número de bytes da unidade de armazenamento de dados.*/
    public final long getTotalSpace() throws IOException {
        return fileStore.getTotalSpace();
    }
    
    /**Retornar o formato do sistema de arquivos do dispositivo.*/
    public final String getFileSystemFormat() throws IOException {
        return fileStore.type();
    }

    /**
     * Sobrescrito para comparação entre unidades de armazenamento de dados.
     * @param obj outra unidade de armazenamento de dados a ser comparada.
     * @return true, se ambas as unidades tem o mesmo diretório raiz, false, se
     * tem diretórios raiz diferentes.
     */
    @Override
    public final boolean equals(Object obj) {
        if (obj != null) {
            if (obj == this) {
                return true;
            } else if (obj instanceof Drive) {
                return ((Drive) obj).root.equals(this.root);
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * Sobrescrito para retornar o rótulo do volume, seguido pela letra.
     */
    @Override
    public String toString() {
        String label = getLabel();
        if (!label.equals("")) {
            return label + " (" + getLetter() + ")";
        } else {
            return getLetter();
        }
    }
    
}