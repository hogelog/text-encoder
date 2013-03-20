package org.hogel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.charset.CharacterCodingException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.TransferHandler;
import javax.swing.UIManager;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.google.common.io.Files;

public class MainWindow implements TableModelListener {

    private static final Logger LOG = LoggerFactory.getLogger(MainWindow.class);

    JFrame frame;

    JTextArea logTextArea;

    JTable replaceTable;

    Encoding encoding = new Encoding();

    Configuration config = new Configuration("config.yaml");

    private ReplaceTableModel replaceTableModel;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                    final MainWindow window = new MainWindow();
                    window.frame.setVisible(true);
                } catch (final Exception e) {
                    LOG.error(e.getMessage(), e);
                }
            }
        });
    }

    /**
     * Create the application.
     */
    public MainWindow() {
        initialize();
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        frame = new JFrame();
        frame.setBounds(100, 100, 450, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new BorderLayout(0, 0));

        final JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        frame.getContentPane().add(tabbedPane, BorderLayout.CENTER);

        logTextArea = new JTextArea();
        logTextArea.setEditable(false);

        final JScrollPane logPanel = new JScrollPane(logTextArea);
        tabbedPane.addTab("変換ログ", null, logPanel, null);

        replaceTableModel = new ReplaceTableModel();
        replaceTable = new JTable(replaceTableModel);
        replaceTable.setColumnSelectionAllowed(true);
        replaceTable.setCellSelectionEnabled(true);
        replaceTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        replaceTable.setShowGrid(true);
        replaceTable.setGridColor(Color.GRAY);

        final JScrollPane replaceTablePane = new JScrollPane(replaceTable);
//        tabbedPane.addTab("設定", null, replaceTablePane, null);
        final JPanel settingPanel = new JPanel();
        settingPanel.setLayout(new BorderLayout(0, 0));
        settingPanel.add(replaceTablePane, BorderLayout.CENTER);
        tabbedPane.addTab("設定", null, settingPanel, null);

        final TransferHandler dropHandler = new DropHandler();
        frame.setTransferHandler(dropHandler);
        logTextArea.setTransferHandler(dropHandler);
        replaceTable.setTransferHandler(dropHandler);

        final JButton newReplaceButton = new JButton("置き換えパターン追加");
        newReplaceButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final int rowCount = replaceTableModel.getRowCount();
                replaceTableModel.setRowCount(rowCount + 1);
            }
        });
        settingPanel.add(newReplaceButton, BorderLayout.SOUTH);

        loadConfig();
        replaceTableModel.addTableModelListener(this);
    }

    private void loadConfig() {
        log(String.format("設定ファイル %s を読み込みました\n", config.getConfigFile().getAbsolutePath()));
        final Map<String, String> replacePatterns = config.getReplacePatterns();
        encoding.setCharacterMapping(replacePatterns);

        replaceTableModel.setRowCount(0);
        for (final String search : replacePatterns.keySet()) {
            final String replace = replacePatterns.get(search);
            replaceTableModel.addRow(new Object[]{search, replace});
        }
    }

    private class DropHandler extends TransferHandler {
        private static final long serialVersionUID = 1L;
        @Override
        public boolean importData(TransferSupport support) {
            final Transferable transferable = support.getTransferable();
            try {
                @SuppressWarnings("unchecked")
                final List<File> files = (List<File>) transferable.getTransferData(DataFlavor.javaFileListFlavor);
                for (final File file : files) {
                    encodeFile(file);
                }
                return true;
            } catch (final UnsupportedFlavorException e) {
                LOG.error(e.getMessage(), e);
                return false;
            } catch (final IOException e) {
                LOG.error(e.getMessage(), e);
                return false;
            }
        }
        @Override
        public boolean canImport(TransferSupport support) {
            return true;
        }
    }

    private void encodeFile(File file) {
        try {
            final byte[] readData = Files.toByteArray(file);
            final byte[] writeData = encoding.encode(Encoding.SJIS, readData);
            if (Arrays.equals(readData, writeData)) {
                log(String.format("%s は既にShift-JISです。\n", file.getPath()));
            } else {
                Files.write(writeData, file);
                log(String.format("%s をShift-JISに変換しました。\n", file.getPath()));
            }
        } catch (final EncodingException e) {
            final String message = String.format("%sへの変換に失敗しました:\n%s", e.getTarget().displayName(), e.getSource());
            log(message);
            JOptionPane.showMessageDialog(frame, message); }
        catch (final CharacterCodingException e) {
            log(String.format("未知の文字コードのファイルです:\n%s\n", file));
        } catch (final IOException e) {
            log(e.getMessage());
            LOG.error(e.getMessage(), e);
        }
    }

    private void log(String text) {
        System.err.print(text);
        logTextArea.setText(logTextArea.getText() + text);
    }

    @Override
    public void tableChanged(TableModelEvent e) {
        final int rowCount = replaceTableModel.getRowCount();
        final Map<String, String> replaceMap =  new LinkedHashMap<String, String>();
        for (int i = 0; i < rowCount; ++i) {
            final String search = (String) replaceTableModel.getValueAt(i, 0);
            final String replace = (String) replaceTableModel.getValueAt(i, 1);
            if (Strings.isNullOrEmpty(search) || Strings.isNullOrEmpty(replace))
                continue;
            replaceMap.put(search, replace);
        }
        config.setReplacePatterns(replaceMap);
        log(String.format("設定ファイル %s を保存しました\n", config.getConfigFile().getAbsolutePath()));
    }

}
