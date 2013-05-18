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
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.ImageIcon;
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

import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class MainWindow implements TableModelListener {
    private static final Logger LOG = LoggerFactory.getLogger(MainWindow.class);

    @Inject SjisEncoder encoder;
    @Inject Configuration config;
    @Inject Printer printer;

    JFrame frame;

    JTextArea logTextArea;

    JTable replaceTable;

    private ReplaceTableModel replaceTableModel;

    private JButton newReplaceButton;

    public class WindowPrinter implements Printer {
        private StringBuilder messages = new StringBuilder();

        @Override
        public synchronized void print(String message) {
            System.out.print(message);
            messages.append(message).append('\n');
            logTextArea.setText(messages.toString());
        }

        @Override
        public void error(String message, Exception e) {
            LOG.error(message, e);
            messages.append(message).append('\n');
            logTextArea.setText(messages.toString());
            JOptionPane.showMessageDialog(frame, message);
        }

        @Override
        public String getMessages() {
            return messages.toString();
        }
    }

    public static void main(final String[] args) {
        try {
            EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    innerMain(args);
                }
            });
        } catch (final Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }

    public static void testMain(final String[] args) throws InvocationTargetException, InterruptedException {
        EventQueue.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                innerMain(args);
            }
        });
    }

    private static void innerMain(String[] args) {
        if (args.length > 0) {
            launchEncoder(args);
        } else {
            launchWindow();
        }
    }

    private static void launchWindow() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (final Exception e) {
            LOG.error(e.getMessage(), e);
        }
        final MainWindow window = new MainWindow();
        window.frame.setVisible(true);
    }

    private static void launchEncoder(String[] args) {
        final ConsolePrinter printer = new ConsolePrinter();
        final Injector injector = Guice.createInjector(new TextEncoderModule(printer));
        final SjisEncoder encoder = injector.getInstance(SjisEncoder.class);
        encoder.encodeFilesFromCommand(args, printer);
    }

    public MainWindow() {
        initialize();

        final WindowPrinter printer = new WindowPrinter();
        final Injector injector = Guice.createInjector(new TextEncoderModule(printer));
        injector.injectMembers(this);

        loadConfig();

        initializeAction();
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        frame = new JFrame("Shift-JIS Encoder");
        frame.setBounds(100, 100, 450, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new BorderLayout(0, 0));

        final JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        frame.getContentPane().add(tabbedPane, BorderLayout.CENTER);

        final ImageIcon windowIcon = new ImageIcon(getClass().getResource("/icon.png"));
        frame.setIconImage(windowIcon.getImage());

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
        final JPanel settingPanel = new JPanel();
        settingPanel.setLayout(new BorderLayout(0, 0));
        settingPanel.add(replaceTablePane, BorderLayout.CENTER);
        tabbedPane.addTab("設定", null, settingPanel, null);

        newReplaceButton = new JButton("置き換えパターン追加");
        settingPanel.add(newReplaceButton, BorderLayout.SOUTH);
    }

    private void initializeAction() {
        final TransferHandler dropHandler = new DropHandler();
        frame.setTransferHandler(dropHandler);
        logTextArea.setTransferHandler(dropHandler);
        replaceTable.setTransferHandler(dropHandler);

        newReplaceButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final int rowCount = replaceTableModel.getRowCount();
                replaceTableModel.setRowCount(rowCount + 1);
            }
        });

        replaceTableModel.addTableModelListener(this);
    }

    private void loadConfig() {
        printer.print(String.format("設定ファイル %s を読み込みました", config.getConfigFile().getAbsolutePath()));
        encoder.loadConfig();

        final Map<String, String> replacePatterns = config.getReplacePatterns();
        replaceTableModel.setRowCount(0);
        for (final Entry<String, String> pattern : replacePatterns.entrySet()) {
            final String search = pattern.getKey();
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
                encoder.encodeFiles(files);
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
        encoder.loadConfig();
        printer.print(String.format("設定ファイル %s を保存しました", config.getConfigFile().getAbsolutePath()));
    }

}
