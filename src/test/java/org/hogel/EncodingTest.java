package org.hogel;

import static org.hamcrest.CoreMatchers.is;
import static org.hogel.Encoding.SJIS;
import static org.hogel.Encoding.UTF8;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;

import org.junit.Test;

public class EncodingTest {

    String text = "日本語日本語";

    private static final Charset[] encodings = {SJIS, UTF8};
    private final Encoding encoding = new Encoding(encodings);

    @Test
    public void encode_utf8_to_sjis() throws Exception {
        for (final Charset source : encodings) {
            for (final Charset target : encodings) {
                final byte[] sourceData = text.getBytes(source);
                final byte[] targetData = text.getBytes(target);
                final byte[] encoded = encoding.encode(target, sourceData);
                assertThat(encoded, is(targetData));
                assertThat(encoding.decode(target, encoded), is(text));
            }
        }
    }

    @Test(expected=CharacterCodingException.class)
    public void encode_error() throws Exception {
        final byte[] eucjp = text.getBytes("EUC-JP");
        encoding.encode(UTF8, eucjp);
    }

    @Test(expected=EncodingException.class)
    public void encode_unmappable_unicode() throws Exception {
        final String text = "维基百科";
        final byte[] utf8 = text.getBytes(UTF8);
        final byte[] sjis = encoding.encode(SJIS, utf8);
        System.err.println(new String(sjis, SJIS));
    }

    @Test
    public void encode_unmappable_unicode2() throws CharacterCodingException {
        final String text = "维基百科";
        final byte[] utf8 = text.getBytes(UTF8);
        try {
            encoding.encode(SJIS, utf8);
            assertTrue(false);
        } catch (final EncodingException e) {
            assertThat(e.getSource(), is(text));
            assertThat(e.getTarget(), is(SJIS));
        }
    }

}
