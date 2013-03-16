package org.hogel;

import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;

public class EncodingException extends Exception {

    private static final long serialVersionUID = 1L;

    private final Charset target;
    private final String source;

    public EncodingException(CharacterCodingException e, Charset target, String source) {
        super(e);
        this.target = target;
        this.source = source;
    }

    public Charset getTarget() {
        return target;
    }

    public String getSource() {
        return source;
    }

}
