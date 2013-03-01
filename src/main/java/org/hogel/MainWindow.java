package org.hogel;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
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

    private JFrame frame;

    private JTextArea logTextArea;

    private JTable replaceTable;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager
                            .getSystemLookAndFeelClassName());
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

        final JPanel logPanel = new JPanel();
        logPanel.setToolTipText("");
        tabbedPane.addTab("変換ログ", null, logPanel, null);
        logPanel.setLayout(new BorderLayout(0, 0));

        logTextArea = new JTextArea();
        logPanel.add(logTextArea);
        logTextArea.setEditable(false);

        final JPanel settingPanel = new JPanel();
        tabbedPane.addTab("設定", null, settingPanel, null);
        settingPanel.setLayout(new BorderLayout(0, 0));

        replaceTable = new JTable();
        replaceTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        settingPanel.add(replaceTable, BorderLayout.CENTER);

        final JScrollPane scrollPane = new JScrollPane();
        settingPanel.add(scrollPane, BorderLayout.EAST);

        final TransferHandler dropHandler = new TransferHandler() {
            @Override
            public boolean importData(TransferSupport support) {
                final Transferable transferable = support.getTransferable();
                try {
                    @SuppressWarnings("unchecked")
                    final
                    List<File> files = (List<File>) transferable
                            .getTransferData(DataFlavor.javaFileListFlavor);
                    for (final File file : files) {
                        encodeFile(file);
                    }
                } catch (UnsupportedFlavorException | IOException e) {
                    e.printStackTrace();
                    return false;
                }
                return true;
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

    private final Encoder sjisencoder = new Encoder(Encoder.SJIS);
    private void encodeFile(File file) throws IOException {
        final byte[] readData = Files.toByteArray(file);
        final byte[] writeData = sjisencoder.encode(readData);
        if (Arrays.equals(readData, writeData)) {
            log(String.format("%s は既にShift-JISです。\n", file.getPath()));
        } else {
            Files.write(writeData, file);
            log(String.format("%s をShift-JISに変換しました。\n", file.getPath()));
        }
    }

    private void log(String text) {
        System.err.print(text);
        logTextArea.setText(logTextArea.getText() + text);
    }

}
