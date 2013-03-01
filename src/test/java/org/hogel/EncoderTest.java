package org.hogel;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.nio.charset.Charset;

import org.junit.Test;

public class EncoderTest {
    Charset UTF8 = Charset.forName("UTF-8");
    Charset SJIS = Charset.forName("SJIS");

    String text = "日本語日本語";

    @Test
    public void sjis_to_utf8() {
        final byte[] sjis = text.getBytes(SJIS);
        final byte[] utf8 = text.getBytes(UTF8);
        final Encoder encoder = new Encoder(UTF8);
        final byte[] encoded = encoder.encode(sjis);
        assertThat(encoded, is(utf8));
    }

    @Test
    public void utf8_to_utf8() {
        final byte[] utf8 = text.getBytes(UTF8);
        final Encoder encoder = new Encoder(UTF8);
        final byte[] encoded = encoder.encode(utf8);
        assertThat(encoded, is(utf8));
    }
    @Test
    public void error() throws Exception {
        final byte[] eucjp = text.getBytes("EUC-JP");
        final Encoder encoder = new Encoder(UTF8);
        try {
            encoder.encode(eucjp);
            assertTrue(false);
        } catch (final Exception e) {
        }
    }
}
