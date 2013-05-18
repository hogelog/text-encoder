package org.hogel;

import java.awt.EventQueue;

import javax.swing.JOptionPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConsolePrinter implements Printer {

    private static final Logger LOG = LoggerFactory.getLogger(ConsolePrinter.class);

    private StringBuilder messages = new StringBuilder();

    @Override
    public synchronized void print(String message) {
        System.out.println(message);
        messages.append(message).append('\n');
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

    @Override
    public String getMessages() {
        return messages.toString();
    }
}
