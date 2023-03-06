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
 * Classe para leitura e escrita do arquivo XML com os dados do último backup
 * realizado na Unidade de Backup.
 * @author Leandro Aparecido de Almeida
 */
final class LastBackupDataFile {
    
    /**Chave data do último backup.*/
    private final String NODE1 = "time";
    /**Chave backup parcial.*/
    private final String NODE2 = "partial";
    /**Dados do último backup.*/
    private LastBackupData lastBackupData;
    /**Arquivo XML.*/
    private final File file;

    /**
     * Criar uma instância de <b>LastBackupDataFile</b>. Caso o arquivo exista,
     * carrega os dados do último backup gravados nele.
     * @param file arquivo XML.
     */
    public LastBackupDataFile(File file) throws JDOMException, IOException {
        this.file = file;
        if (file.exists()) {
            SAXBuilder builder = new SAXBuilder();
            Document doc = (Document) builder.build(this.file);
            Element root = doc.getRootElement();
            Date lastBackupTime = new Date(Long.valueOf(root.getChild(NODE1).getText()));
            boolean partial = Boolean.valueOf(root.getChild(NODE2).getText());
            lastBackupData = new LastBackupData(lastBackupTime, partial);
        }
    }

    /**Obter os dados do último backup realizado na Unidade de Backup.*/
    public LastBackupData getLastBackupData() {
        return lastBackupData;
    }

    /**Obter o arquivo XML.*/
    public File getFile() {
        return file;
    }
    
    /**
     * Salvar os dados do último backup realizado na Unidade de Backup no arquivo
     * XML.
     * @param lastBackupData dados do último backup realizado na Unidade de Backup.
     */
    public void save(LastBackupData lastBackupData) throws FileNotFoundException, IOException {
        this.lastBackupData = lastBackupData;
        Document doc = new Document();
        Element root = new Element("last-backup-data");
        Element e1 = new Element(NODE1);
        e1.setText(String.valueOf(this.lastBackupData.getTime().getTime()));
        Element e2 = new Element(NODE2);
        e2.setText(String.valueOf(this.lastBackupData.isPartial()));
        root.addContent(e1);
        root.addContent(e2);
        doc.setRootElement(root);
        XMLOutputter xout = new XMLOutputter();
        try (OutputStream out = new FileOutputStream(file)) {
            xout.output(doc, out);
        }
    }

}