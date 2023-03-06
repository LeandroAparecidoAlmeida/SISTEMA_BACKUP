package backup.drive;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.XMLOutputter;

/**
 * Classe para leitura e escrita do arquivo XML de instalação da Unidade de Backup.
 * @author Leandro Aparecido de Almeida
 */
final class InstallationFile {
    
    /**Chave data da instalação da Unidade de Backup.*/
    private final String NODE1 = "installation-time";
    /**Chave identificador único da Unidade de Backup.*/
    private final String NODE2 = "uid";
    /**Data da Instalação da Unidade de Backup.*/
    private Date installationTime;
    /**Identificador da Unidade de Backup.*/
    private String uid;
    /**Arquivo XML.*/
    private final File file;

    /**
     * Criar uma instância de <b>InstallationFile</b>. Caso o arquivo exista,
     * faz a leitura dos dados da instalação gravados nele.
     * @param file arquivo XML.
     */
    public InstallationFile(File file) throws JDOMException, IOException {
        this.file = file;
        this.installationTime = null;
        this.uid = null;
        if (this.file.exists()) {
            SAXBuilder builder = new SAXBuilder();
            Document doc = (Document) builder.build(this.file);
            Element root = doc.getRootElement();
            installationTime = new Date(Long.valueOf(root.getChild(NODE1).getText()));
            uid = root.getChild(NODE2).getText();
        }
    }

    /**Obter a data de instalação da Unidade de Backup.*/
    public Date getInstallationTime() {
        return installationTime;
    }

    /**Obter o identificador único da Unidade de Backup.*/
    public String getUID() {
        return uid;
    }

    /**Obter o arquivo XML.*/
    public File getFile() {
        return file;
    }

    /**
     * Salvar os dados da instalação da Unidade de Backup no arquivo XML.
     * @param installationTime data da instalação da Unidade de Backup.
     * @param uid identificador único da Unidade de Backup. 
     */
    public void save(Date installationTime, String uid) throws FileNotFoundException, IOException, JDOMException {
        this.installationTime = installationTime;
        this.uid = uid;
        Document doc = new Document();
        Element root = new Element("backup-drive-info");
        Element e1 = new Element(NODE1);
        e1.setText(String.valueOf(this.installationTime.getTime()));
        Element e2 = new Element(NODE2);
        e2.setText(this.uid);
        root.addContent(e1);
        root.addContent(e2);
        doc.setRootElement(root);
        XMLOutputter xout = new XMLOutputter();
        try (OutputStream out = new FileOutputStream(file)) {
            xout.output(doc, out);
        }
    }
   
}