package backup.installer;

import backup.system.OSDetector;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import net.jimmc.jshortcut.JShellLink;
import java.net.URISyntaxException;
import static backup.system.Application.extractResource;

public class Installer {
    
    private final String resPackage = "/backup/windows/resources/";
    private final File path;
    private final File iconFile;
    private final File batFile;
    private final File partitionsFile;
    private final File hddSerialNumber;
    private final File jacobDllx86;
    private final File jacobDllx64;

    public Installer() throws URISyntaxException {
        path = new File(System.getProperty("rootdir"));
        String rootPath = path.getAbsolutePath();
        iconFile = new File(rootPath + "//Backup.ico");
        batFile = new File(rootPath + "//Backup.bat");
        partitionsFile = new File(rootPath + "//Partitions.exe");
        hddSerialNumber = new File(rootPath + "//HDDSerialNumber.exe");
        jacobDllx86 = new File(rootPath + "//lib//jacob-1.20-x86.dll");
        jacobDllx64 = new File(rootPath + "//lib//jacob-1.20-x64.dll");
    }
    
    public void install() throws FileNotFoundException, IOException {
        if (OSDetector.isWindows()) {
            installWindows();
        } else if (OSDetector.isUnix()) {
            //Não implementado.
        } else if (OSDetector.isMac()) {
            //Não implementado.
        }
    }

    private void createWindowsDesktopShortcut() throws FileNotFoundException, IOException {
        JShellLink link = new JShellLink();
        String filePath = JShellLink.getDirectory("") + batFile.getAbsolutePath();
        link.setFolder(JShellLink.getDirectory("desktop"));
        link.setName("Backup");
        link.setPath(filePath);
        link.setIconLocation(iconFile.getAbsolutePath());
        link.setDescription("Programa de backup");
        link.save();
    }
    
    private void installWindows() throws FileNotFoundException, IOException {
        //Extrair arquivos necessários ao funcionamento no sistema Microsoft Windows.
        extractResource(resPackage + "BackupDrive.ico", iconFile);
        extractResource(resPackage + "Partitions.exe", partitionsFile);
        extractResource(resPackage + "HDDSerialNumber.exe", hddSerialNumber);
        extractResource(resPackage + "jacob-1.20-x86.dll", jacobDllx86);
        extractResource(resPackage + "jacob-1.20-x64.dll", jacobDllx64);
        extractResource(resPackage + "Backup.bat", batFile);
        //Criar o atalho na área de trabalho.
        createWindowsDesktopShortcut();
    }
    
}