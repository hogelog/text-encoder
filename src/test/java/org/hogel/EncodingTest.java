package org.hogel;

import static org.hamcrest.CoreMatchers.is;
import static org.hogel.Encoding.SJIS;
import static org.hogel.Encoding.UTF8;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.nio.charset.Charset;

import org.junit.Test;

public class EncodingTest {

    String text = "日本語日本語";

    private static final Charset[] encodings = {SJIS, UTF8};
    @Test
    public void encode_utf8_to_sjis() throws Exception {
        for (final Charset source : encodings) {
            for (final Charset target : encodings) {
                final byte[] sourceData = text.getBytes(source);
                final byte[] targetData = text.getBytes(target);
                final byte[] encoded = Encoding.encode(target, sourceData);
                assertThat(encoded, is(targetData));
                assertThat(Encoding.decode(target, encoded), is(text));
            }
        }
    }

    @Test
    public void encode_error() throws Exception {
        final byte[] eucjp = text.getBytes("EUC-JP");
        try {
            Encoding.encode(UTF8, eucjp);
            assertTrue(false);
        } catch (final Exception e) {
        }
    }

}
