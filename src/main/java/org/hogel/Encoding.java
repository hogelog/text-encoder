package org.hogel;

import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

public class Encoding {
    public static final Charset SJIS = Charset.forName("SJIS");
    public static final Charset UTF8 = Charset.forName("UTF-8");

    private static final Charset[] CANDIDATE_ENCODINGS = {
        Encoding.SJIS, Encoding.UTF8
    };

    public static byte[] encode(Charset target, byte[] data) {
        final ByteBuffer dataBuffer = ByteBuffer.wrap(data);
        for (final Charset candidate : CANDIDATE_ENCODINGS) {
            try {
                final String decoded = decode(candidate, dataBuffer);
                return decoded.getBytes(target);
            } catch (final CharacterCodingException e) {
            }
        }
        throw new IllegalArgumentException("unknown encoding");
    }

    public static String decode(Charset source, byte[] data) throws CharacterCodingException {
        return decode(source, ByteBuffer.wrap(data));
    }

    public static String decode(Charset source, ByteBuffer dataBuffer) throws CharacterCodingException {
        final CharsetDecoder decoder = source.newDecoder();
        dataBuffer.position(0);
        return decoder.decode(dataBuffer).toString();
    }
}
