package org.hogel;

import javax.swing.JOptionPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Slf4jPrinter implements Printer {

    private static final Logger LOG = LoggerFactory.getLogger(Slf4jPrinter.class);

    @Override
    public void print(String message) {
        LOG.info(message);
    }

    @Override
    public void error(String message, Exception e) {
        LOG.error(message, e);
        JOptionPane.showMessageDialog(null, message);
    }

}
