package org.hogel;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.Map;

public class Encoding {
    public static final Charset SJIS = Charset.forName("SJIS");
    public static final Charset UTF8 = Charset.forName("UTF-8");

    private static final Charset[] DEFAULT_CANDIDATE_ENCODINGS = {
        Encoding.SJIS, Encoding.UTF8
    };

    private final Charset[] candidates;
    private Map<String, String> mapping = null;

    public Encoding() {
        this(DEFAULT_CANDIDATE_ENCODINGS);
    }

    public Encoding(Charset[] candidates) {
        this.candidates = candidates;
    }

    public void setCharacterMapping(Map<String, String> mapping) {
        this.mapping = mapping;
    }

    public byte[] encode(Charset target, byte[] data) throws EncodingException, CharacterCodingException {
        final String decoded = guessDecode(data);
        return encode(target, decoded);
    }

    public byte[] encode(Charset target, String decoded) throws EncodingException {
        if (mapping != null) {
            for (final String search : mapping.keySet()) {
                decoded = decoded.replace(search, mapping.get(search));
            }
        }
        return encodeInternal(target, decoded);
    }

    private byte[] encodeInternal(Charset target, String decoded) throws EncodingException {
        final CharsetEncoder encoder = target.newEncoder();
        final CharBuffer decodedBuffer = CharBuffer.wrap(decoded);
        try {
            final ByteBuffer encodedBuffer = encoder.encode(decodedBuffer);
            final byte[] encoded = new byte[encodedBuffer.limit()];
            encodedBuffer.get(encoded, 0, encodedBuffer.limit());
            return encoded;
        } catch (final CharacterCodingException e) {
            throw new EncodingException(e, target, decoded);
        }
    }

    public String guessDecode(byte[] data) throws CharacterCodingException {
        return guessDecode(ByteBuffer.wrap(data));
    }

    public String guessDecode(ByteBuffer dataBuffer) throws CharacterCodingException {
        for (final Charset candidate : candidates) {
            try {
                return decode(candidate, dataBuffer);
            } catch (final CharacterCodingException e) {
                continue;
            }
        }
        throw new CharacterCodingException();
    }

    public String decode(Charset source, byte[] data) throws CharacterCodingException {
        return decode(source, ByteBuffer.wrap(data));
    }

    public String decode(Charset source, ByteBuffer dataBuffer) throws CharacterCodingException {
        final CharsetDecoder decoder = source.newDecoder();
        dataBuffer.position(0);
        return decoder.decode(dataBuffer).toString();
    }
}
