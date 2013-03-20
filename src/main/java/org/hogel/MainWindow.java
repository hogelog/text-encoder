package org.hogel;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.nio.charset.CharacterCodingException;
import java.util.Arrays;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.TransferHandler;
import javax.swing.UIManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.Files;

public class MainWindow {

    private static final Logger LOG = LoggerFactory.getLogger(MainWindow.class);

    JFrame frame;

    JTextArea logTextArea;

    JTable replaceTable;

    Encoding encoding = new Encoding();

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

        final JScrollPane settingPanel = new JScrollPane(replaceTable);
        tabbedPane.addTab("設定", null, settingPanel, null);

        final TransferHandler dropHandler = new TransferHandler() {
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

            private static final long serialVersionUID = 1L;
        };
        frame.setTransferHandler(dropHandler);
        logTextArea.setTransferHandler(dropHandler);
        replaceTable.setTransferHandler(dropHandler);
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

}
