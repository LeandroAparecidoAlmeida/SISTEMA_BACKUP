package backup.drive;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import backup.windows.utilities.powershell.PowerShell;
import static backup.system.Application.extractResource;

/**
 * Implementação de uma unidade de armazenamento de dados em sistemas Microsoft
 * Windows.
 * @author Leandro Aparecido de Almeida
 */
public final class WindowsDrive extends Drive {
    
    /**Nome do arquivo de ícone.*/
    private final String ICON_FILE_NAME = "BackupDrive.ico";
    /**Nome do arquivo autorun.inf*/
    private final String AUTORUN_FILE_NAME = "Autorun.inf";
    /**Lista das partições do HD do computador.*/
    private final List<String> backupSourceDrives = new ArrayList<>();
    /**Arquivo de ícone na raiz da unidade de armazenamento de dados.*/
    private final File iconFile;
    /**Arquivo "autorun.inf" na raiz da unidade de armazenamento de dados.*/
    private final File autorunFile;
    
    /**
     * Criar uma instância de <b>WindowsDrive</b>.
     * @param root diretório raiz da unidade de armazenamento de dados.
     */
    public WindowsDrive(Path root) throws IOException, FileNotFoundException,
    ClassNotFoundException, Exception {
        super(root);
        iconFile = new File(root.toString() + ICON_FILE_NAME);
        autorunFile = new File(root.toString() + AUTORUN_FILE_NAME);
        loadBackupDrives();
    }
    
    /**
     * Carregar as partições do HD interno.
     */
    private void loadBackupDrives() throws Exception {
        List<String> drives = new ArrayList<>();
        Process process = Runtime.getRuntime().exec(System.getProperty("rootdir") + 
        "Partitions.exe");
        try (BufferedReader istr = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String outputLine;
            while((outputLine = istr.readLine()) != null){
                drives.add(outputLine);                
            } 
        }
        process.destroy();
        for (String drive : drives) {
            backupSourceDrives.add(drive);
        }
    }
    
    /**
     * Realizar a instalação de uma <b>Unidade de Backup</b> do Windows e sua
     * configuração.
     */
    @Override
    public void install() throws IOException, BackupException, Exception {
        super.install();
        //Extrai o ícone que está inserido no .jar da aplicação para o
        //diretório raiz da unidade de armazenamento.
        deleteFile(iconFile);
        extractResource("/backup/windows/resources/BackupDrive.ico", iconFile);
        setHiddenFileAttribute(iconFile, true);
        //Extrair o arquivo "Autorun.inf" para o diretório raiz da unidade de
        //armazenamento.
        deleteFile(autorunFile);
        extractResource("/backup/windows/resources/Autorun.inf", autorunFile);
        setHiddenFileAttribute(autorunFile, true);
    }
    
    /**
     * Realizar a desinstalação de uma <b>Unidade de Backup</b> do Windows.
     * @param keepLogDirectory se true, preserva o diretório dos logs de backup
     * após a desinstalação, false, remove também o diretório de log de backup
     * após a desinstalação.
     */
    @Override
    public void uninstall(boolean keepLogDirectory) throws IOException,
    BackupException, Exception {
        super.uninstall(keepLogDirectory);
        deleteFile(iconFile);
        deleteFile(autorunFile);
    }

    /**
     * Formatar o dispositivo de memória para o formato requerido pela plataforma
     * Microsoft Windows (NTFS). Usa-se o prompt de comandos do Windows (cmd.exe)
     * para fazer isso. Primeiro o programa vai abrir o prompt de comandos depois
     * passará a seguinte instrução para o mesmo:<br><br>
     * <i>FORMAT G: /FS:NTFS /X /Q /V: && LABEL G: UNID. BACKUP 01</i>
     * <br><br>
     * Onde:
     * <br><br>
     * <i>G:</i> Letra da unidade de armazenamento (no caso G:).<br>
     * <i>/FS:NTFS</i> Tipo do sistema de arquivos a ser formatado, no caso NTFS.<br>
     * <i>/X</i> Força desmontar o drive antes de formatar.<br>
     * <i>/Q</i> Diretiva para formatação rápida.<br>
     * <i>/V: && LABEL G: UNID. BACKUP 01</i> Rótulo do volume que neste
     * caso é UNID.BACKUP 01.
     * <br><br>
     * <i><b>Obs.:</b> Consultar a documentação do Windows para mais detalhes sobre
     * o comando <b>FORMAT</b>.</i>
     * @param label rótulo do volume.
     */
    @Override
    public void format(String label, String FileSystem, String... args) throws Exception {
        String letter = getLetter();
        Process process = Runtime.getRuntime().exec("CMD.EXE");
        try (OutputStreamWriter ostr = new OutputStreamWriter(process
        .getOutputStream()); BufferedReader istr = new BufferedReader(
        new InputStreamReader(process.getInputStream()))) {
            ostr.write("FORMAT " + letter + " /FS:"+ FileSystem + " /X /Q /V: && LABEL " +
            letter + " " + label + "\n\n");
            ostr.flush();
            String outputLine;
            while (process.isAlive()) {
                while((outputLine = istr.readLine()) != null){
                    if (outputLine.length() == 0) {
                        ostr.write("exit\n");
                        ostr.flush();
                        break;                        
                    }                    
                }            
            }
        }
        process.destroy();
        fileStore = fsProvider.getFileStore(this.root);
        System.gc();
    }

    /**
     * Ejetar o dispositivo de memória. Utiliza o Power Shell do Windows para
     * realizar o processo.
     * @return true, se o dispositivo foi ejetado, false, se não foi.
     */
    @Override
    public boolean eject() throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append("$shellApp = New-Object -comObject Shell.Application;");
        sb.append("$shellApp.Namespace(17).ParseName(\"");
        sb.append(getLetter());
        sb.append("\").InvokeVerb(\"Eject\")");
        PowerShell.executeSingleCommand(sb.toString());
        return !Files.exists(new File(getLetter()).toPath());
    }

    /**
     * Verificar se a unidade de armazenamento é uma partição do disco rígido local.
     * @return true, a unidade de armazenamento é uma partição do disco rígido
     * local, false, a unidade de armazenamento não é uma partição do disco 
     * rígido local.
     */
    @Override
    public boolean isBackupSourceDrive() {
        boolean result = false;
        String letter = getLetter();
        for (String drive : backupSourceDrives) {
            if (drive.equals(letter)) {
                result = true;
                break;
            }
        }
        return result;
    }
    
    /**
     * Obter o número serial do HD principal.
     * @return número serial do HD.
     * @throws Exception 
     */
    @Override
    public String getHddSerial() throws Exception {
        String serial;
        Process process = Runtime.getRuntime().exec(System.getProperty("rootdir") + 
        "HDDSerialNumber.exe");
        try (BufferedReader istr = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            if ((serial = istr.readLine()) == null) {
                throw new Exception("Erro ao obter o número serial do HD");
            } 
        }
        process.destroy();
        final String simb = "";
        return serial.replace("/", simb).replace("|", simb).replace("\\", simb)
        .replace(":", simb).replace("?", simb).replace("<", simb).replace(">", simb)
        .replace("*", simb).replace("\"", simb).replace(";", simb);
    }
    
    /**
     * Extrair a letra da unidade de armazenamento de dados (Exemplo: C:, D:, F:, etc.).
     * @return letra da unidade de armazenamento de dados.
     */
    @Override
    public String getLetter() {
        return root.toFile().getAbsolutePath().substring(0, 2);
    }

    /**
     * Ocultar/Exibir um arquivo do Windows.
     * @param file arquivo a ser oculto/exibido.
     * @param value true, para ocultar o arquivo, false, para exibir o arquivo. 
     */
    @Override
    protected void setHiddenFileAttribute(File file, boolean value) throws Exception {
        if (file.exists()) {
            fsProvider.setAttribute(file.toPath(), "dos:hidden", value);
        }
    }

    /**
     * Definir o atributo somente leitura do arquivo.
     * @param file arquivo a ter o atributo modificado.
     * @param value true, o arquivo é somente leitura, false, o arquivo pode
     * sofrer escrita.
     */
    @Override
    protected void setReadOnlyFileAttribute(File file, boolean value) throws Exception {
        if (file.exists()) {
            fsProvider.setAttribute(file.toPath(), "dos:readonly", value);
        }
    }

    /**
     * Verificar se o arquivo é somente leitura.
     * @param file arquivo a ser verificado.
     * @return true, o arquivo é somente leitura, false, ele pode ser escrito.
     */
    @Override
    protected boolean isReadOnlyFile(File file) throws Exception {
        boolean isReadOnly = true;
        if (file.exists()) {
            Map map = fsProvider.readAttributes(
                file.toPath(),
                "dos:readonly",
                LinkOption.NOFOLLOW_LINKS
            );
            isReadOnly = (boolean) map.get("readonly");
        }
        return isReadOnly;
    }
    
    /**
     * Obter o arquivo correspondente na unidade de armazenamento.
     * @param sourceFile arquivo no sistema local para backup.
     */
    @Override
    protected File getTargetFile(File sourceFile) {   
        String sourcePath = sourceFile.getAbsolutePath();
        String sourceDrive = sourcePath.substring(0, sourcePath.indexOf(":", 0) + 2);
        String targetPath = sourcePath.replace(sourceDrive, root.toString());
        File targetFile = new File(targetPath);
        return targetFile;
    }
    
    /**
     * Obter o caminho do arquivo refFile considerando-se que ele esteja
     * no mesmo drive de refDirectory.
     * @param refDirectory diretório de referência.
     * @param refFile arquivo de referência
     * @return arquivo relativo ao diretório.
     */
    @Override
    protected File getRelativeFile(File refDirectory, File refFile) {  
        String sourcePath = refFile.getAbsolutePath();
        String directoryPath = refDirectory.getAbsolutePath();
        String directoryDrive = directoryPath.substring(0, directoryPath.indexOf(":", 0) + 2);
        String targetDrive = sourcePath.substring(0, sourcePath.indexOf(":", 0) + 2);
        return new File(sourcePath.replace(targetDrive, directoryDrive));
    }
    
    /**
     * Extrair o path de um arquivo, Exemplo: C:\teste\doc.txt retorna
     * teste\doc.txt (sem o C:\).
     * @param file arquivo a se extrair o path.
     */
    @Override
    protected String extractFilePath(File file) {
        String fileName;
        if (file.toPath().getRoot().toFile().getAbsolutePath().length() < file.getAbsolutePath().length()) {
            fileName = file.getAbsolutePath().substring(
                file.toPath().getRoot().toFile().getAbsolutePath().length(),
                file.getAbsolutePath().length()
            );
        } else {
            fileName = file.getAbsolutePath();
        }
        return fileName;
    }
    
    /**Obter o formato do sistema de arquivos requerido pelo Windows.*/
    @Override
    protected String preferredFileSystem() {
        return "NTFS";
    }
    
    @Override
    public String[] getFileSystemList() {
        return new String[] {"NTFS", "FAT32", "FAT", "exFAT"};
    }
    
    /**
     * Sobrescrito para retornar o rótulo do volume, seguido pela letra. Caso
     * não tenha rótulo, retorna WINDOWS DRIVE (X:), aonde X é a letra do drive.
     */
    @Override
    public String toString() {
        String label = getLabel();
        if (!label.equals("")) {
            return label + " (" + getLetter() + ")";
        } else {
            return "WINDOWS DRIVE (" + getLetter() + ")";
        }
    }
    
}