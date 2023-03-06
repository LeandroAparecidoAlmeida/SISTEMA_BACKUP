package gui.dialogs;

import backup.drive.BackupListener;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Image;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import backup.drive.SafeRestore;
import backup.drive.DrivesMonitor;
import backup.utils.StrUtils;
import backup.drive.Drive;
import backup.drive.DrivesManager;
import backup.drive.BackupUpdatesListener;
import backup.drive.DriveInsertionListener;
import backup.system.FileLock;
import java.awt.Dimension;
import javax.swing.WindowConstants;
import javax.swing.table.JTableHeader;

/**
 * Tela principal do programa.
 */
public final class MainWindow extends javax.swing.JFrame implements BackupUpdatesListener,
DriveInsertionListener, BackupListener  {
    
    /**Lista das Unidades de Backup instaladas.*/
    private List<Drive> backupDrivesList;
    /**Unidade de Backup selecionada na JCombobox.*/
    private Drive backupDrive;
    /**Status de processamento de atualizações na Unidade de Backup.*/
    private boolean processingUpdate;
    /**Status de Unidade de Backup removida da porta USB.*/
    private boolean backupDriveRemoved;
    /**Status de backup concluído em todas as etapas.*/
    private boolean backupDone;
    /**Bloqueio de arquivo para controle da unidade selecionada.*/
    private FileLock fileLock;
    
    /**
     * Criar uma instancia de <b>MainWindow</b>.
     */
    public MainWindow() {          
        initComponents();          
        backupDrive = null;
        listBackupDrives(true);
        URL url = getClass().getResource("/images/img2.png");
        Image image = new ImageIcon(url).getImage();
        setIconImage(image); 
        setBackupMode(false);
        setExtendedState(MAXIMIZED_BOTH);
    }
    
    /**
     * Listar as Unidades de Backup na JComboBox.
     * @param changeFilesList atualizar a lista de arquivos da JTable.
     */
    private void listBackupDrives(boolean changeFilesList) {
        ActionListener[] copy = null;
        if (!changeFilesList) {
            ActionListener[] listeners = jcbInstaledDrives.getActionListeners();
            copy = new ActionListener[listeners.length];
            System.arraycopy(listeners, 0, copy, 0, listeners.length);
            for (ActionListener listener : listeners) {
                jcbInstaledDrives.removeActionListener(listener);
            }
        }       
        backupDrivesList = DrivesManager.getInstalledDrives();
        Drive[] items = new Drive[backupDrivesList.size()];
        for (int i = 0; i < backupDrivesList.size(); i++) {
            items[i] = backupDrivesList.get(i);
        }
        DefaultComboBoxModel model = new DefaultComboBoxModel(items);
        jcbInstaledDrives.setModel(model);          
        int idx = -1; //Índice da Unidade de Backup selecionada.
        for (int i = 0; i < backupDrivesList.size(); i++) {
            if (backupDrivesList.get(i).equals(backupDrive)) {
                idx = i;
                break;
            }
        }
        jcbInstaledDrives.setSelectedIndex(idx);
        if (copy != null) {
            for (ActionListener listener : copy) {
                jcbInstaledDrives.addActionListener(listener);
            }
        }
    }
    
    /**
     * Obter o {@link TableModel} para a JTable.
     * @param rowCount número de linhas da JTable.
     * @return TableModel para a JTable.
     */
    private TableModel getTableModel(int rowCount) {
        String[] columnTitles = new String[] {"", "ARQUIVO", "CRIADO EM",
        "ALTERADO EM", "TAMANHO", "AÇÃO"};
        TableModel tableModel = new DefaultTableModel(columnTitles,
        rowCount) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }                                    
        };
        return tableModel;
    }
    
    /**
     * Redimensionar as colunas da JTable.
     */
    private void resizeTableColumns() {
        TableColumnModel tableColunmModel = jtFiles.getColumnModel();
        tableColunmModel.getColumn(0).setMinWidth(0);
        tableColunmModel.getColumn(0).setMaxWidth(0);
        tableColunmModel.getColumn(2).setMinWidth(90);
        tableColunmModel.getColumn(2).setMaxWidth(90);
        tableColunmModel.getColumn(3).setMinWidth(90);
        tableColunmModel.getColumn(3).setMaxWidth(90);
        tableColunmModel.getColumn(4).setMinWidth(90);
        tableColunmModel.getColumn(4).setMaxWidth(90);  
        tableColunmModel.getColumn(5).setMinWidth(70);
        tableColunmModel.getColumn(5).setMaxWidth(70);
    }

    /**
     * Atualizar os controles de interface, conforme contexto de operação.
     */
    private void update(boolean updateFilesList) {
        if (SafeRestore.isPendingRestore()) {
            setTitle("BACKUP (RESTAURAÇÃO PENDENTE)");
            jbRestore.setVisible(true);
            jbRestore.setEnabled(backupDrive != null);
            jbBackup.setVisible(false);
            jbUpdate.setVisible(false);
            jmiBackup.setVisible(false);
            jsRestore.setVisible(false);
            jmiDirectories.setVisible(false);
            jsDirectories.setVisible(false);
            jmiUninstall.setVisible(false);
            jsUnistall.setVisible(false);
        } else {
            setTitle("BACKUP");
            jbRestore.setVisible(false);
            jbRestore.setEnabled(true);
            jbBackup.setVisible(true);
            jbUpdate.setVisible(true);
            jmiBackup.setVisible(true);
            jsRestore.setVisible(true);
            jmiDirectories.setVisible(true);
            jsDirectories.setVisible(true);
            jmiUninstall.setVisible(true);
            jsUnistall.setVisible(true);
            jmiRestore.setEnabled(false);
        }
        jtFiles.setModel(getTableModel(0));
        try {resizeTableColumns();} catch (Exception ex){}
        jcbInstaledDrives.setEnabled(false);
        backupDriveRemoved = false;
        processingUpdate = true;
        jmBackupDrive.setVisible(false);
        jmInstallation.setVisible(false);
        jmiFilesDetails.setEnabled(false);
        jmiOpenFiles.setEnabled(false);
        jbBackup.setEnabled(false);
        jmiBackup.setEnabled(false);
        jbEject.setEnabled(false);
        jbUpdate.setEnabled(false);
        jbCancel.setVisible(false);
        jlLastBackupTime.setText("");
        jlFilesCount.setText("");
        jlFilesCount.setIcon(null);
        if (backupDrive != null) {
            jmBackupDrive.setText(backupDrive.toString().toUpperCase());
            jmBackupDrive.setVisible(true);
            jmBackupDrive.setEnabled(false);
            jmInstallation.setVisible(false);
            if (updateFilesList) {
                try {
                    jbCancel.setVisible(true);
                    jbCancel.setEnabled(true);
                    jbUpdate.setVisible(false);                    
                    jlLastBackupTime.setText("Verificando atualizações. Por favor, aguarde.");
                    jlFilesCount.setIcon(new ImageIcon(getClass().getResource(
                    "/icons16/gif1.gif")));
                    backupDrive.checkUpdates(this);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(
                        this,
                        ex.getMessage(),
                        "Erro",
                        JOptionPane.ERROR_MESSAGE
                    );
                } finally {
                    jlFilesCount.setIcon(null);
                }
                jbCancel.setVisible(false);
                jbUpdate.setVisible(true);
            }            
            String lastBackupTime = StrUtils.formatDate1(backupDrive.getLastBackupInfo()
            .getTime());
            if (backupDrive.getLastBackupInfo().isPartial()) {
                lastBackupTime += " (Backup Parcial)";
            }
            jlLastBackupTime.setText("ÚLTIMO BACKUP: " + lastBackupTime);
            jmBackupDrive.setEnabled(true);
            jbEject.setEnabled(true);
            jbUpdate.setEnabled(true);
            if (jtFiles.getRowCount() > 0) {
                jtFiles.setRowSelectionInterval(0, 0);
                jtFiles.setEnabled(true);
                jbBackup.setEnabled(true);
                jmiBackup.setEnabled(true);
                jmiRestore.setEnabled(false);
                jmiFilesDetails.setEnabled(true);
                jmiOpenFiles.setEnabled(true);
                jlFilesCount.setText(String.format("%,d", jtFiles.getRowCount()) +
                " ATUALIZAÇÕES PENDENTES");
            } else {
                jtFiles.setEnabled(false);
                jbBackup.setEnabled(false);
                jmiBackup.setEnabled(false);
                jmiRestore.setEnabled(true);
                jmiFilesDetails.setEnabled(false);
                jmiOpenFiles.setEnabled(false);
                jlFilesCount.setText(" ");
            }
        } else {
            jmBackupDrive.setVisible(false);
            jmInstallation.setVisible(true);
        }
        JTableHeader tableHeader = jtFiles.getTableHeader();
        tableHeader.setPreferredSize(new Dimension(0, 23));
        processingUpdate = false;
        jcbInstaledDrives.setEnabled(true);
        if (backupDriveRemoved) {
            closeSelectedBackupDrive();
        }
    }
    
    /**
     * Abrir a Unidade de Backup selecionada no JCombobox.
     */
    private void openSelectedBackupDrive() {
        Component _this = this;
        new Thread() {
            @Override
            public void run() {
                setCursor(new Cursor(Cursor.WAIT_CURSOR));
                int idx = jcbInstaledDrives.getSelectedIndex();
                if (idx >= 0) {
                    if (!backupDrivesList.get(idx).equals(backupDrive)) {
                        backupDrive = backupDrivesList.get(idx);
                        try {
                            fileLock = null;
                            System.gc();
                            fileLock = new FileLock(backupDrive.getIdentifier());
                            if (fileLock.tryLock()) {
                                if (SafeRestore.isPendingRestore()) {
                                    if (!backupDrive.getIdentifier().equals(SafeRestore
                                    .getSourceDriveId())) {
                                        jcbInstaledDrives.setSelectedIndex(-1);
                                        JOptionPane.showMessageDialog(_this,
                                            "Há uma operação de backup pendente. Por " +
                                            "favor, conecte\na Unidade de Backup " + 
                                            SafeRestore.getSourceDriveId() + 
                                            " e clique no\nbotão Restaurar para concluir "+
                                            "esta operação e prosseguir.",
                                            "Erro",
                                            JOptionPane.ERROR_MESSAGE
                                        );                            
                                    } else {
                                        jbRestore.setEnabled(true);
                                        update(false);
                                    }                        
                                } else {
                                    update(true);
                                }
                            } else {
                                jcbInstaledDrives.setSelectedIndex(-1);
                                fileLock = null;
                                System.gc();
                                JOptionPane.showMessageDialog(
                                    _this,
                                    "Unidade de backup já selecionada por outra instância.",
                                    "Erro",
                                    JOptionPane.ERROR_MESSAGE
                                );
                            }
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(_this,
                                ex.toString(),
                                "Erro",
                                JOptionPane.ERROR_MESSAGE
                            );
                        }
                    }
                } else {
                    backupDrive = null;
                    update(false);                    
                }
                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));       
            }            
        }.start();        
    }
    
    /**
     * Fechar a Unidade de Backup selecionada no JCombobox.
     */
    private void closeSelectedBackupDrive() {
        Component _this = this;
        new Thread() {            
            @Override
            public void run() {
                setCursor(new Cursor(Cursor.WAIT_CURSOR));
                backupDrive = null;
                listBackupDrives(true);
                try {
                    if (fileLock != null) {
                        fileLock.release();
                        fileLock = null;
                        System.gc();
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(
                        _this,
                        ex.toString(),
                        "Erro",
                        JOptionPane.ERROR_MESSAGE
                    );
                }
                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        }.start();        
    }
    
    /**
     * Ejetar a Unidade de Backup selecionada na JCombobox.
     */
    private void ejectBackupDrive() {
        MainWindow _this = this;
        new Thread() {
            @Override
            public void run() {
                setCursor(new Cursor(Cursor.WAIT_CURSOR));
                jlLastBackupTime.setText("Ejetando dispositivo...");                
                jlFilesCount.setIcon(new ImageIcon(getClass().getResource(
                "/icons16/gif1.gif")));
                jlFilesCount.setText("");
                jlFilesCount.setVisible(true);
                jmBackupDrive.setEnabled(false);
                jcbInstaledDrives.setEnabled(false);
                jtFiles.setEnabled(false);
                jbEject.setEnabled(false);                
                jbUpdate.setEnabled(false);
                jbBackup.setEnabled(false);
                jbRestore.setEnabled(false);
                try {
                    if (backupDrive.eject()) {
                        closeSelectedBackupDrive();                    
                    } else {
                        jbEject.setEnabled(true); 
                        jbUpdate.setEnabled(true);
                        jbRestore.setEnabled(true);
                        jcbInstaledDrives.setEnabled(true);
                        jmBackupDrive.setEnabled(true);
                        jlFilesCount.setIcon(null);
                        jlFilesCount.setVisible(false);
                        String lastBackupTime = StrUtils.formatDate1(backupDrive.getLastBackupInfo()
                        .getTime());
                        if (backupDrive.getLastBackupInfo().isPartial()) {
                            lastBackupTime += " (Backup Parcial)";
                        }
                        jlLastBackupTime.setText("ÚLTIMO BACKUP: " + lastBackupTime);
                        if (jtFiles.getRowCount() > 0) {
                            jbBackup.setEnabled(true);                            
                            jlFilesCount.setVisible(true);
                            jlFilesCount.setText(String.format("%,d", jtFiles.getRowCount()) +
                            " ATUALIZAÇÕES PENDENTES");
                        }
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(
                        _this,
                        ex.getMessage(),
                        "Erro",
                        JOptionPane.ERROR_MESSAGE
                    );
                }
                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }            
        }.start();        
    }
    
    /**
     * Cancelar o processo de backup.
     */
    private void abortProcess() {
        new Thread() {
            @Override
            public void run() {
                jbCancel.setEnabled(false);
                jlLastBackupTime.setText(
                    "Cancelando o processo. Por favor, aguarde."
                );
                backupDrive.abortProcess();
            }            
        }.start();        
    }
    
    /**
     * Atualizar a lista de arquivos pendentes de atualização na Unidade
     * de Backup selecionada.
     */
    private void updateFilesList() {
        new Thread() {
            @Override
            public void run() {
                setCursor(new Cursor(Cursor.WAIT_CURSOR));
                update(true);
                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }            
        }.start();
    }
    
    /**
     * Abrir os arquivos selecionados na JTable com seus respectivos programas.
     */
    private void openSelectedFiles() {
        setCursor(new Cursor(Cursor.WAIT_CURSOR));
        try {
            for (int idx : jtFiles.getSelectedRows()) {
                File file = (File) jtFiles.getValueAt(idx, 1);
                Desktop.getDesktop().open(file);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                this,
                ex.getMessage(),
                "Erro",
                JOptionPane.ERROR_MESSAGE
            );
        }
        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));  
    }
    
    /**
     * Colocar ou retirar a tela do modo de backup.
     * @param backupMode se true, coloca em modo de backup, se false, tira
     * do modo de backup.
     */
    private void setBackupMode(boolean backupMode) {
        stbStatus.setVisible(!backupMode);
        stbProgress.setVisible(backupMode);        
        if (backupMode) {
            setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
            jcbInstaledDrives.setEnabled(false);
            jtFiles.setEnabled(false);
            jbBackup.setEnabled(false);
            jbCancel.setEnabled(false);
            jbEject.setEnabled(false);
            jbRestore.setEnabled(false);
            jbUpdate.setEnabled(false);
            jmiBackup.setEnabled(false);
            jmiRestore.setEnabled(false);
            jmiBackupLog.setEnabled(false);
            jmiDirectories.setEnabled(false);
            jmiClose.setEnabled(false);
            jmiEject.setEnabled(false);
            jmiUninstall.setEnabled(false);
            jmiFilesDetails.setEnabled(false);
            jmiOpenFiles.setEnabled(false);
        } else {
            update(!backupDone);
            jpbBackup.setValue(0);
            jpbBackup.setString("");
            jmiBackupLog.setEnabled(true);
            jmiDirectories.setEnabled(true);
            jmiClose.setEnabled(true);
            jmiEject.setEnabled(true);
            jmiUninstall.setEnabled(true);
            DrivesMonitor.start();
            setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        }
    }
    
    /**
     * Fazer o backup dos arquivos.
     */
    private void performBackup() {
        BackupListener listener = this;
        Component comp = this;
        new Thread() {
            @Override
            public void run() {
                DrivesMonitor.stop();                
                backupDone = false;
                setBackupMode(true);
                jpbBackup.setValue(0);
                jpbBackup.setString("PROCESSANDO...");       
                jpbBackup.setIndeterminate(true);
                try {
                    backupDrive.performBackup(listener);
                } catch (Exception ex) {
                    setBackupMode(false);
                    JOptionPane.showMessageDialog(
                        comp,
                        ex.getMessage(),
                        "Erro",
                        JOptionPane.ERROR_MESSAGE
                    );
                }
            } 
        }.start();
    }
    
    /**
     * Cancelar o backup dos arquivos.
     */
    public void abortBackup() {
        new Thread() {
            @Override
            public void run() {
                setCursor(new Cursor(Cursor.WAIT_CURSOR));
                jpbBackup.setString("CANCELANDO O BACKUP...");
                jbCancelBackup.setEnabled(false);
                backupDrive.abortProcess();                
            }
        }.start();        
    }
    
    /**
     * Abrir a tela para a restauração dos arquivos.
     */
    private void showRestoreDialog() {
        setCursor(new Cursor(Cursor.WAIT_CURSOR));
        new RestoreDialog(this, backupDrive).setVisible(true);
        update(false);
        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }
    
    /**
     * Abrir a tela para instalação de uma Unidade de Backup.
     */
    private void showInstallBackupDriveDialog() {
        setCursor(new Cursor(Cursor.WAIT_CURSOR));
        InstallBackupDriveDialog dialog = new InstallBackupDriveDialog(this);
        dialog.setVisible(true);
        if (dialog.getInstalledDrive() != null) {
            backupDrive = dialog.getInstalledDrive();
            listBackupDrives(true);
            showBackupDirectoriesDialog();
        }
        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }
    
    /**
     * Abrir a tela para a desinstalação da Unidade de Backup selecionada
     * na JComboBox.
     */
    private void showUninstallBackupDriveDialog() {
        setCursor(new Cursor(Cursor.WAIT_CURSOR));
        UninstallBackupDriveDialog dialog = new UninstallBackupDriveDialog(
            this,
            backupDrive
        );
        dialog.setVisible(true);
        if (dialog.uninstalledBackupDrive()) {
            closeSelectedBackupDrive();
        }
        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }
    
    /**
     * Abrir a tela para manutenção dos diretórios de backup da Unidade de
     * Backup selecionada na JCombobox.
     */
    private void showBackupDirectoriesDialog() {
        setCursor(new Cursor(Cursor.WAIT_CURSOR));
        BackupDirectoriesDialog dialog = new BackupDirectoriesDialog(this, backupDrive);
        dialog.setVisible(true);
        if (dialog.haveChanges()) {
            updateFilesList();
        }
        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }
   
    /**
     * Abrir a tela para visualização dos históricos de backups na Unidade
     * de Backup selecionada na JCombobox.
     */
    private void showBackupLogsDialog() {
        setCursor(new Cursor(Cursor.WAIT_CURSOR));
        new BackupLogsDialog(this, backupDrive).setVisible(true);
        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }

    /**
     * Abrir a tela com os créditos da versão atual.
     */
    private void showAboutDialog() {
        setCursor(new Cursor(Cursor.WAIT_CURSOR));
        new AboutDialog(this).setVisible(true);
        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }
    
    /**
     * Abrir a tela com os detalhes dos arquivos selecionados na JTable.
     */
    private void showFilesDetailsDialog() {
        setCursor(new Cursor(Cursor.WAIT_CURSOR));
        int[] selectedRows = jtFiles.getSelectedRows();
        File[] files = new File[jtFiles.getSelectedRows().length];
        for (int i = 0; i < selectedRows.length; i++) {
            files[i] = (File) jtFiles.getValueAt(selectedRows[i], 1);
        }
        new FilesDetailsDialog(this, files).setVisible(true);
        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }
    
    /**
     * Abrir a tela com os dados sobre a Unidade de Backup selecionada.
     */
    private void showBackupDriveDetailsDialog() {
        setCursor(new Cursor(Cursor.WAIT_CURSOR));
        new BackupDriveDetailsDialog(this, backupDrive).setVisible(true);
        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }

    /**
     * Notificação do fim do processo de busca por atualizações de arquivos no
     * sistema local. As listas de arquivos a serem atualizados na Unidade de
     * Backup são passadas nos parâmetros, para que se faça a listagem a fim de
     * que o usuário avalie quais são os arquivos que serão afetados com o backup,
     * antes efetivamente de fazê-lo, evitando, eventualmente a perda de dados
     * por acidente.
     * @param createdFilesList lista de arquivos criados no sistema local.
     * @param deletedFilesList lista de arquivos a serem removidos da Unidade de
     * Backup.
     * @param overwrittenFilesList lista de arquivos a serem atualizados no
     * sistema local.
     * 
     */
    @Override
    public void listUpdatedFiles(final List<File> createdFilesList, 
    final List<File> deletedFilesList, final List<File> overwrittenFilesList) {
        try {
            int rowCount = createdFilesList.size() + deletedFilesList.size() +
            overwrittenFilesList.size();
            TableModel tableModel = getTableModel(rowCount);                             
            int row = -1;
            //Arquivos novos.
            for (File file : createdFilesList) {
                row++;
                BasicFileAttributes attrs = Files.readAttributes(
                file.toPath(), BasicFileAttributes.class);
                Date time1 = new Date(attrs.creationTime().toMillis());
                Date time2 = new Date(attrs.lastModifiedTime().toMillis());
                long size = attrs.size();  
                tableModel.setValueAt("1", row, 0);
                tableModel.setValueAt(file, row, 1);                                   
                tableModel.setValueAt(time1, row, 2);
                tableModel.setValueAt(time2, row, 3);
                tableModel.setValueAt(size, row, 4);
                tableModel.setValueAt("COPIAR", row, 5);
            }
            //Arquivos excluídos.
            for (File file : deletedFilesList) {
                row++;
                BasicFileAttributes attrs = Files.readAttributes(
                file.toPath(), BasicFileAttributes.class);
                Date time1 = new Date(attrs.creationTime().toMillis());
                Date time2 = new Date(attrs.lastModifiedTime().toMillis());
                long size = attrs.size(); 
                tableModel.setValueAt("3", row, 0);
                tableModel.setValueAt(file, row, 1);                                   
                tableModel.setValueAt(time1, row, 2);
                tableModel.setValueAt(time2, row, 3);
                tableModel.setValueAt(size, row, 4);
                tableModel.setValueAt("EXCLUIR", row, 5);
            }
            //Arquivos alterados.
            for (File file : overwrittenFilesList) {
                row++;
                BasicFileAttributes attrs = Files.readAttributes(
                file.toPath(), BasicFileAttributes.class);
                Date time1 = new Date(attrs.creationTime().toMillis());
                Date time2 = new Date(attrs.lastModifiedTime().toMillis());
                long size = attrs.size(); 
                tableModel.setValueAt("2", row, 0);
                tableModel.setValueAt(file, row, 1);                                   
                tableModel.setValueAt(time1, row, 2);
                tableModel.setValueAt(time2, row, 3);
                tableModel.setValueAt(size, row, 4);
                tableModel.setValueAt("ATUALIZAR", row, 5);
            }
            jtFiles.setModel(tableModel);            
            TableCellRenderer2 renderer = new TableCellRenderer2();
            TableColumnModel tcm = jtFiles.getColumnModel();
            tcm.getColumn(1).setCellRenderer(renderer);
            tcm.getColumn(2).setCellRenderer(renderer);
            tcm.getColumn(3).setCellRenderer(renderer);
            tcm.getColumn(4).setCellRenderer(renderer);
            tcm.getColumn(5).setCellRenderer(renderer);
            resizeTableColumns();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                this,
                ex.getMessage(),
                "Erro",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /**
     * Notificação de que uma Unidade de Armazenamento de Dados foi inserida em
     * uma porta USB.
     * @param drive Unidade de Armazenamento de Dados conectada.
     */
    @Override
    public void drivePlugged(Drive drive) {
        if (drive.isInstalled()) {
            listBackupDrives(false);
        }
    }

    /**
     * Notificação de que uma Unidade de Armazenamento de Dados foi desconectada
     * de uma porta USB.
     * @param drive Unidade de Armazenamento de Dados desconectada.
     */
    @Override
    public void driveEjected(Drive drive) {
        if (drive.isInstalled()) {
            listBackupDrives(false);
            if (drive.equals(this.backupDrive)) {
                backupDriveRemoved = true;
                if (processingUpdate) {
                    abortProcess();
                } else {
                    closeSelectedBackupDrive();
                }
            }
        }
    }
    
    /**
     * Notificação de início da backup.
     * @param numberOfFiles número de arquivos a serem atualizados.
     */
    @Override
    public void backupInitialized(int numberOfFiles) {
        jbCancelBackup.setEnabled(true);
        jpbBackup.setIndeterminate(false);
        jpbBackup.setStringPainted(true);
        jpbBackup.setMaximum(numberOfFiles);
        jpbBackup.setString("COPIANDO ARQUIVO 0 DE " + String.valueOf(jpbBackup.getMaximum()));
    }

    /**
     * Notificação de conclusão do backup.
     */
    @Override
    public void backupDone() {
        backupDone = true;
        setBackupMode(false);
    }

    /**
     * Notificação de processamento de um arquivo.
     * @param fileNumber número do arquivo.
     * @param file arquivo.
     * @param mode modo de processamento.
     */
    @Override
    public void processingFile(int fileNumber, File file, int mode) {
        jpbBackup.setValue(fileNumber);
        jpbBackup.setString(
            "COPIANDO ARQUIVO " +
            String.valueOf(fileNumber) +
            " DE " +
            String.valueOf(jpbBackup.getMaximum())
        );
    }

    /**
     * Notificação de backup abortado por motivo de erro.
     * @param ex objeto contendo o erro ocorrido.
     */
    @Override
    public void backupAbortedByError(Exception ex) {
        backupDone = false;
        setBackupMode(false);
        JOptionPane.showMessageDialog(
            this,
            ex.toString(),
            "Erro",
            JOptionPane.ERROR_MESSAGE
        );
    }

    /**
     * Notificação de backup abortado pelo usuário.
     */
    @Override
    public void backupAbortedByUser() {
        backupDone = false;
        setBackupMode(false);
        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPopupMenu1 = new javax.swing.JPopupMenu();
        jmiOpenFiles = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        jmiFilesDetails = new javax.swing.JMenuItem();
        stbStatus = new javax.swing.JPanel();
        jlLastBackupTime = new javax.swing.JLabel();
        jlFilesCount = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jtFiles = new javax.swing.JTable();
        final TableCellRenderer tcr = jtFiles.getTableHeader().getDefaultRenderer();
        jtFiles.getTableHeader().setDefaultRenderer(
            new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table,
                    Object value, boolean isSelected, boolean hasFocus, int row,
                    int column) {
                    Component c = tcr.getTableCellRendererComponent(table, value,
                        isSelected, hasFocus, row, column);
                    if (c instanceof DefaultTableCellRenderer) {
                        DefaultTableCellRenderer dtcr = (DefaultTableCellRenderer) c;
                        dtcr.setHorizontalAlignment(SwingConstants.LEFT);
                        dtcr.setBorder(new CompoundBorder(
                            dtcr.getBorder(),
                            new EmptyBorder(0, 1, 0, 1))
                    );
                }
                return c;
            }
        }
    );
    jToolBar2 = new javax.swing.JToolBar();
    jLabel1 = new javax.swing.JLabel();
    jcbInstaledDrives = new javax.swing.JComboBox<>();
    jbUpdate = new javax.swing.JButton();
    jbCancel = new javax.swing.JButton();
    jbEject = new javax.swing.JButton();
    jbRestore = new javax.swing.JButton();
    jbBackup = new javax.swing.JButton();
    stbProgress = new javax.swing.JPanel();
    jlLastBackupTime1 = new javax.swing.JLabel();
    jpbBackup = new javax.swing.JProgressBar();
    jbCancelBackup = new javax.swing.JButton();
    jMenuBar1 = new javax.swing.JMenuBar();
    jmBackupDrive = new javax.swing.JMenu();
    jmiBackup = new javax.swing.JMenuItem();
    jsRestore = new javax.swing.JPopupMenu.Separator();
    jmiRestore = new javax.swing.JMenuItem();
    jsDirectories = new javax.swing.JPopupMenu.Separator();
    jmiDirectories = new javax.swing.JMenuItem();
    jSeparator2 = new javax.swing.JPopupMenu.Separator();
    jmiBackupLog = new javax.swing.JMenuItem();
    jSeparator3 = new javax.swing.JPopupMenu.Separator();
    jmiEject = new javax.swing.JMenuItem();
    jsUnistall = new javax.swing.JPopupMenu.Separator();
    jmiUninstall = new javax.swing.JMenuItem();
    jsInserirDiretorio = new javax.swing.JPopupMenu.Separator();
    jmiDetails = new javax.swing.JMenuItem();
    jSeparator5 = new javax.swing.JPopupMenu.Separator();
    jmiClose = new javax.swing.JMenuItem();
    jmInstallation = new javax.swing.JMenu();
    jmiInstallBackupUnit = new javax.swing.JMenuItem();
    jmHelp = new javax.swing.JMenu();
    jmiAbout = new javax.swing.JMenuItem();

    jmiOpenFiles.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
    jmiOpenFiles.setText("Abrir arquivos selecionados");
    jmiOpenFiles.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            jmiOpenFilesActionPerformed(evt);
        }
    });
    jPopupMenu1.add(jmiOpenFiles);
    jPopupMenu1.add(jSeparator1);

    jmiFilesDetails.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
    jmiFilesDetails.setText("Detalhes dos arquivos selecionados");
    jmiFilesDetails.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            jmiFilesDetailsActionPerformed(evt);
        }
    });
    jPopupMenu1.add(jmiFilesDetails);

    setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
    setTitle("L2A BACKUP");
    setBackground(new java.awt.Color(51, 51, 51));
    addWindowListener(new java.awt.event.WindowAdapter() {
        public void windowClosing(java.awt.event.WindowEvent evt) {
            formWindowClosing(evt);
        }
        public void windowOpened(java.awt.event.WindowEvent evt) {
            formWindowOpened(evt);
        }
    });

    stbStatus.setBorder(javax.swing.BorderFactory.createEtchedBorder());

    jlLastBackupTime.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
    jlLastBackupTime.setForeground(new java.awt.Color(51, 51, 51));
    jlLastBackupTime.setText("[data backup]");

    jlFilesCount.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
    jlFilesCount.setForeground(new java.awt.Color(51, 51, 51));
    jlFilesCount.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jlFilesCount.setText("[num. atualizações]");
    jlFilesCount.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);

    javax.swing.GroupLayout stbStatusLayout = new javax.swing.GroupLayout(stbStatus);
    stbStatus.setLayout(stbStatusLayout);
    stbStatusLayout.setHorizontalGroup(
        stbStatusLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(stbStatusLayout.createSequentialGroup()
            .addGap(5, 5, 5)
            .addComponent(jlLastBackupTime, javax.swing.GroupLayout.PREFERRED_SIZE, 436, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(jlFilesCount, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGap(5, 5, 5))
    );
    stbStatusLayout.setVerticalGroup(
        stbStatusLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(stbStatusLayout.createSequentialGroup()
            .addGroup(stbStatusLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jlLastBackupTime, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jlFilesCount, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addContainerGap())
    );

    jtFiles.setModel(new javax.swing.table.DefaultTableModel(
        new Object [][] {
            {},
            {},
            {},
            {}
        },
        new String [] {

        }
    ));
    jtFiles.setComponentPopupMenu(jPopupMenu1);
    jtFiles.setFillsViewportHeight(true);
    jtFiles.setRowHeight(20);
    jtFiles.setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    jtFiles.setShowHorizontalLines(false);
    jtFiles.setShowVerticalLines(false);
    jtFiles.getTableHeader().setReorderingAllowed(false);
    jtFiles.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mouseClicked(java.awt.event.MouseEvent evt) {
            jtFilesMouseClicked(evt);
        }
    });
    jScrollPane1.setViewportView(jtFiles);

    jToolBar2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
    jToolBar2.setFloatable(false);
    jToolBar2.setRollover(true);
    jToolBar2.setMaximumSize(new java.awt.Dimension(33340, 28));

    jLabel1.setText(" UNID. BACKUP:  ");
    jToolBar2.add(jLabel1);

    jcbInstaledDrives.setForeground(java.awt.Color.blue);
    jcbInstaledDrives.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            jcbInstaledDrivesActionPerformed(evt);
        }
    });
    jToolBar2.add(jcbInstaledDrives);

    jbUpdate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons16/icon48.png"))); // NOI18N
    jbUpdate.setText("ATUALIZAR");
    jbUpdate.setFocusable(false);
    jbUpdate.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
    jbUpdate.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    jbUpdate.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            jbUpdateActionPerformed(evt);
        }
    });
    jToolBar2.add(jbUpdate);

    jbCancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons16/icon7.png"))); // NOI18N
    jbCancel.setText("CANCELAR");
    jbCancel.setFocusable(false);
    jbCancel.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
    jbCancel.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    jbCancel.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            jbCancelActionPerformed(evt);
        }
    });
    jToolBar2.add(jbCancel);

    jbEject.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons16/icon41.png"))); // NOI18N
    jbEject.setText("EJETAR");
    jbEject.setFocusable(false);
    jbEject.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
    jbEject.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    jbEject.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            jbEjectActionPerformed(evt);
        }
    });
    jToolBar2.add(jbEject);

    jbRestore.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons16/icon42.png"))); // NOI18N
    jbRestore.setText("FAZER O RESTORE");
    jbRestore.setFocusable(false);
    jbRestore.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jbRestore.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
    jbRestore.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    jbRestore.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            jbRestoreActionPerformed(evt);
        }
    });
    jToolBar2.add(jbRestore);

    jbBackup.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons16/icon49.png"))); // NOI18N
    jbBackup.setText("FAZER O BACKUP");
    jbBackup.setFocusable(false);
    jbBackup.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jbBackup.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
    jbBackup.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    jbBackup.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            jbBackupActionPerformed(evt);
        }
    });
    jToolBar2.add(jbBackup);

    stbProgress.setBorder(javax.swing.BorderFactory.createEtchedBorder());

    jlLastBackupTime1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
    jlLastBackupTime1.setForeground(new java.awt.Color(51, 51, 51));
    jlLastBackupTime1.setText("PROGRESSO: ");

    jpbBackup.setForeground(new java.awt.Color(0, 0, 255));
    jpbBackup.setStringPainted(true);

    jbCancelBackup.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons16/icon7.png"))); // NOI18N
    jbCancelBackup.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            jbCancelBackupActionPerformed(evt);
        }
    });

    javax.swing.GroupLayout stbProgressLayout = new javax.swing.GroupLayout(stbProgress);
    stbProgress.setLayout(stbProgressLayout);
    stbProgressLayout.setHorizontalGroup(
        stbProgressLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(stbProgressLayout.createSequentialGroup()
            .addGap(5, 5, 5)
            .addComponent(jlLastBackupTime1)
            .addGap(0, 0, 0)
            .addComponent(jpbBackup, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGap(5, 5, 5)
            .addComponent(jbCancelBackup, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(2, 2, 2))
    );
    stbProgressLayout.setVerticalGroup(
        stbProgressLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(stbProgressLayout.createSequentialGroup()
            .addGroup(stbProgressLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(stbProgressLayout.createSequentialGroup()
                    .addGap(1, 1, 1)
                    .addComponent(jbCancelBackup))
                .addGroup(stbProgressLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jlLastBackupTime1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, stbProgressLayout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addComponent(jpbBackup, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))))
            .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );

    jmBackupDrive.setText("[UNID. BACKUP]");
    jmBackupDrive.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N

    jmiBackup.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
    jmiBackup.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons16/icon49.png"))); // NOI18N
    jmiBackup.setText("Fazer o Backup");
    jmiBackup.setPreferredSize(new java.awt.Dimension(190, 32));
    jmiBackup.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            jmiBackupActionPerformed(evt);
        }
    });
    jmBackupDrive.add(jmiBackup);
    jmBackupDrive.add(jsRestore);

    jmiRestore.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
    jmiRestore.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons16/icon42.png"))); // NOI18N
    jmiRestore.setText("Fazer o Restore");
    jmiRestore.setPreferredSize(new java.awt.Dimension(190, 32));
    jmiRestore.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            jmiRestoreActionPerformed(evt);
        }
    });
    jmBackupDrive.add(jmiRestore);
    jmBackupDrive.add(jsDirectories);

    jmiDirectories.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
    jmiDirectories.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons16/icon46.png"))); // NOI18N
    jmiDirectories.setText("Diretórios para Backup");
    jmiDirectories.setPreferredSize(new java.awt.Dimension(190, 32));
    jmiDirectories.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            jmiDirectoriesActionPerformed(evt);
        }
    });
    jmBackupDrive.add(jmiDirectories);
    jmBackupDrive.add(jSeparator2);

    jmiBackupLog.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
    jmiBackupLog.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons16/icon14.png"))); // NOI18N
    jmiBackupLog.setText("Histórico dos Backups");
    jmiBackupLog.setPreferredSize(new java.awt.Dimension(190, 32));
    jmiBackupLog.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            jmiBackupLogActionPerformed(evt);
        }
    });
    jmBackupDrive.add(jmiBackupLog);
    jmBackupDrive.add(jSeparator3);

    jmiEject.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
    jmiEject.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons16/icon41.png"))); // NOI18N
    jmiEject.setText("Ejetar");
    jmiEject.setPreferredSize(new java.awt.Dimension(190, 32));
    jmiEject.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            jmiEjectActionPerformed(evt);
        }
    });
    jmBackupDrive.add(jmiEject);
    jmBackupDrive.add(jsUnistall);

    jmiUninstall.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
    jmiUninstall.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons16/icon11.png"))); // NOI18N
    jmiUninstall.setText("Desinstalar");
    jmiUninstall.setPreferredSize(new java.awt.Dimension(190, 32));
    jmiUninstall.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            jmiUninstallActionPerformed(evt);
        }
    });
    jmBackupDrive.add(jmiUninstall);
    jmBackupDrive.add(jsInserirDiretorio);

    jmiDetails.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
    jmiDetails.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons16/icon9.png"))); // NOI18N
    jmiDetails.setText("Detalhes");
    jmiDetails.setPreferredSize(new java.awt.Dimension(190, 32));
    jmiDetails.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            jmiDetailsActionPerformed(evt);
        }
    });
    jmBackupDrive.add(jmiDetails);
    jmBackupDrive.add(jSeparator5);

    jmiClose.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
    jmiClose.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons16/icon40.png"))); // NOI18N
    jmiClose.setText("Fechar");
    jmiClose.setPreferredSize(new java.awt.Dimension(190, 32));
    jmiClose.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            jmiCloseActionPerformed(evt);
        }
    });
    jmBackupDrive.add(jmiClose);

    jMenuBar1.add(jmBackupDrive);

    jmInstallation.setText("INSTALAÇÃO");

    jmiInstallBackupUnit.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
    jmiInstallBackupUnit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons16/icon50.png"))); // NOI18N
    jmiInstallBackupUnit.setText("Instalar Unidade de Backup");
    jmiInstallBackupUnit.setPreferredSize(new java.awt.Dimension(227, 32));
    jmiInstallBackupUnit.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            jmiInstallBackupUnitActionPerformed(evt);
        }
    });
    jmInstallation.add(jmiInstallBackupUnit);

    jMenuBar1.add(jmInstallation);

    jmHelp.setText("AJUDA");

    jmiAbout.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
    jmiAbout.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons16/icon9.png"))); // NOI18N
    jmiAbout.setText("Sobre o Programa");
    jmiAbout.setPreferredSize(new java.awt.Dimension(170, 32));
    jmiAbout.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            jmiAboutActionPerformed(evt);
        }
    });
    jmHelp.add(jmiAbout);

    jMenuBar1.add(jmHelp);

    setJMenuBar(jMenuBar1);

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addComponent(stbStatus, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
            .addGap(3, 3, 3)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jToolBar2, javax.swing.GroupLayout.DEFAULT_SIZE, 910, Short.MAX_VALUE))
            .addGap(3, 3, 3))
        .addComponent(stbProgress, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
    );
    layout.setVerticalGroup(
        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(layout.createSequentialGroup()
            .addGap(3, 3, 3)
            .addComponent(jToolBar2, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(3, 3, 3)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 420, Short.MAX_VALUE)
            .addGap(3, 3, 3)
            .addComponent(stbProgress, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(0, 0, 0)
            .addComponent(stbStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(0, 0, 0))
    );

    pack();
    setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jbBackupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbBackupActionPerformed
        performBackup();
    }//GEN-LAST:event_jbBackupActionPerformed

    private void jcbInstaledDrivesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcbInstaledDrivesActionPerformed
        openSelectedBackupDrive();        
    }//GEN-LAST:event_jcbInstaledDrivesActionPerformed

    private void jmiInstallBackupUnitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmiInstallBackupUnitActionPerformed
        showInstallBackupDriveDialog();
    }//GEN-LAST:event_jmiInstallBackupUnitActionPerformed

    private void jmiUninstallActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmiUninstallActionPerformed
        showUninstallBackupDriveDialog();
    }//GEN-LAST:event_jmiUninstallActionPerformed

    private void jtFilesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jtFilesMouseClicked
        if (evt.getClickCount() == 2) {
            openSelectedFiles();
        }
    }//GEN-LAST:event_jtFilesMouseClicked

    private void jmiDirectoriesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmiDirectoriesActionPerformed
        showBackupDirectoriesDialog();
    }//GEN-LAST:event_jmiDirectoriesActionPerformed

    private void jbUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbUpdateActionPerformed
        updateFilesList();        
    }//GEN-LAST:event_jbUpdateActionPerformed

    private void jmiOpenFilesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmiOpenFilesActionPerformed
        openSelectedFiles();
    }//GEN-LAST:event_jmiOpenFilesActionPerformed

    private void jmiFilesDetailsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmiFilesDetailsActionPerformed
        showFilesDetailsDialog();
    }//GEN-LAST:event_jmiFilesDetailsActionPerformed

    private void jbCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbCancelActionPerformed
        abortProcess();
    }//GEN-LAST:event_jbCancelActionPerformed

    private void jmiBackupLogActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmiBackupLogActionPerformed
        showBackupLogsDialog();
    }//GEN-LAST:event_jmiBackupLogActionPerformed

    private void jmiDetailsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmiDetailsActionPerformed
        showBackupDriveDetailsDialog();
    }//GEN-LAST:event_jmiDetailsActionPerformed

    private void jmiCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmiCloseActionPerformed
        closeSelectedBackupDrive();
    }//GEN-LAST:event_jmiCloseActionPerformed

    private void jmiBackupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmiBackupActionPerformed
        performBackup();
    }//GEN-LAST:event_jmiBackupActionPerformed

    private void jmiRestoreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmiRestoreActionPerformed
        showRestoreDialog();
    }//GEN-LAST:event_jmiRestoreActionPerformed

    private void jmiEjectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmiEjectActionPerformed
        ejectBackupDrive();
    }//GEN-LAST:event_jmiEjectActionPerformed

    private void jbEjectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbEjectActionPerformed
        ejectBackupDrive();
    }//GEN-LAST:event_jbEjectActionPerformed

    private void jmiAboutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmiAboutActionPerformed
        showAboutDialog();
    }//GEN-LAST:event_jmiAboutActionPerformed

    private void jbRestoreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbRestoreActionPerformed
        showRestoreDialog();
    }//GEN-LAST:event_jbRestoreActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        DrivesMonitor.addDriveInsertionListener(this);
    }//GEN-LAST:event_formWindowOpened

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        DrivesMonitor.removeDriveInsertionListener(this);
    }//GEN-LAST:event_formWindowClosing

    private void jbCancelBackupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbCancelBackupActionPerformed
        abortBackup();
    }//GEN-LAST:event_jbCancelBackupActionPerformed
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPopupMenu jPopupMenu1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator3;
    private javax.swing.JPopupMenu.Separator jSeparator5;
    private javax.swing.JToolBar jToolBar2;
    private javax.swing.JButton jbBackup;
    private javax.swing.JButton jbCancel;
    private javax.swing.JButton jbCancelBackup;
    private javax.swing.JButton jbEject;
    private javax.swing.JButton jbRestore;
    private javax.swing.JButton jbUpdate;
    private javax.swing.JComboBox<String> jcbInstaledDrives;
    private javax.swing.JLabel jlFilesCount;
    private javax.swing.JLabel jlLastBackupTime;
    private javax.swing.JLabel jlLastBackupTime1;
    private javax.swing.JMenu jmBackupDrive;
    private javax.swing.JMenu jmHelp;
    private javax.swing.JMenu jmInstallation;
    private javax.swing.JMenuItem jmiAbout;
    private javax.swing.JMenuItem jmiBackup;
    private javax.swing.JMenuItem jmiBackupLog;
    private javax.swing.JMenuItem jmiClose;
    private javax.swing.JMenuItem jmiDetails;
    private javax.swing.JMenuItem jmiDirectories;
    private javax.swing.JMenuItem jmiEject;
    private javax.swing.JMenuItem jmiFilesDetails;
    private javax.swing.JMenuItem jmiInstallBackupUnit;
    private javax.swing.JMenuItem jmiOpenFiles;
    private javax.swing.JMenuItem jmiRestore;
    private javax.swing.JMenuItem jmiUninstall;
    private javax.swing.JProgressBar jpbBackup;
    private javax.swing.JPopupMenu.Separator jsDirectories;
    private javax.swing.JPopupMenu.Separator jsInserirDiretorio;
    private javax.swing.JPopupMenu.Separator jsRestore;
    private javax.swing.JPopupMenu.Separator jsUnistall;
    private javax.swing.JTable jtFiles;
    private javax.swing.JPanel stbProgress;
    private javax.swing.JPanel stbStatus;
    // End of variables declaration//GEN-END:variables

}