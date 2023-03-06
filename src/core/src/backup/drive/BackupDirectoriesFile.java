package backup.drive;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

/**
 * Classe para leitura e escrita do arquivo XML que contém os diretórios para
 * backup na Unidade de Backup.
 * @author Leandro Aparecido de Almeida
 */
final class BackupDirectoriesFile {
    
    /**Chave diretório.*/
    private final String NODE1 = "directory";
    /**Arquivo XML.*/
    private final File file;
    /**Lista de diretórios para backup.*/
    private final List<File> backupDirectories;

    /**
     * Criar uma instância de <b>BackupDirectoriesFile</b>. Se o arquivo existir,
     * carrega a lista de diretórios para backup contida no mesmo.
     * @param file arquivo XML. 
     */
    public BackupDirectoriesFile(File file) throws JDOMException, IOException {
        this.file = file;
        backupDirectories = new ArrayList<>();
        if (file.exists()) {
            SAXBuilder builder = new SAXBuilder();
            Document doc = (Document) builder.build(this.file);
            Element root = doc.getRootElement();
            for (Element element : root.getChildren()) {
                if (element.getName().equals(NODE1)) {
                    backupDirectories.add(new File(element.getText()));
                }
            }
        }
    }
    
    /**Obter a lista de diretórios para backup.*/
    public List<File> getBackupDirectoriesList() {
        return backupDirectories;
    }

    /**Obter o arquivo XML.*/
    public File getFile() {
        return file;
    }
    
    /**
     * Salvar a lista de diretórios para backup no arquivo XML.
     * @param directories lista de diretórios para backup.
     */
    public void save(List<File> directories) throws FileNotFoundException, IOException {
        backupDirectories.clear();
        backupDirectories.addAll(directories);
        Document doc = new Document();
        Element root = new Element("backup-directories");
        for (File backupDirectory : backupDirectories) {
            Element element = new Element(NODE1);
            element.setText(backupDirectory.getAbsolutePath());
            root.addContent(element);
        }
        doc.setRootElement(root);
        Format format = Format.getPrettyFormat();
        XMLOutputter xout = new XMLOutputter(format);
        try (OutputStream out = new FileOutputStream(file)) {
            xout.output(doc, out);
        }
    }

}