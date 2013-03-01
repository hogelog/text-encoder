package org.hogel;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

public class Encoder {
    public static final Charset SJIS = Charset.forName("SJIS");
    public static final Charset UTF8 = Charset.forName("UTF-8");

    private static final Charset[] CANDIDATE_ENCODINGS = {
        SJIS, UTF8
    };

    private final Charset target;

    public Encoder(Charset target) {
        this.target = target;
    }

    public byte[] encode(byte[] data) {
        final ByteBuffer dataBuffer = ByteBuffer.wrap(data);
        for (final Charset candidate : CANDIDATE_ENCODINGS) {
            final CharsetDecoder decoder = candidate.newDecoder();
            try {
                dataBuffer.position(0);
                final CharBuffer decoded = decoder.decode(dataBuffer);
                return decoded.toString().getBytes(target);
            } catch (final CharacterCodingException e) {
            }
        }
        throw new IllegalArgumentException("unknown encoding");
    }

}
