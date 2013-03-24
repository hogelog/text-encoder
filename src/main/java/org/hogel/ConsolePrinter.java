package org.hogel;

import java.awt.EventQueue;

import javax.swing.JOptionPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConsolePrinter implements Printer {

    private static final Logger LOG = LoggerFactory.getLogger(ConsolePrinter.class);

    @Override
    public void print(String message) {
        System.err.println(message);
    }

    @Override
    public void error(final String message, Exception e) {
        LOG.error(message, e);

        try {
            EventQueue.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    JOptionPane.showMessageDialog(null, message);
                }
            });
        } catch (final Exception e1) {
            LOG.error(e1.getMessage(), e1);
        }
    }

}
