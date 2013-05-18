package org.hogel;


public interface Printer {

    void print(String message);

    void error(String message, Exception e);

    String getMessages();
}
