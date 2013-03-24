package org.hogel;

import static org.hamcrest.CoreMatchers.is;
import static org.hogel.Encoding.SJIS;
import static org.hogel.Encoding.UTF8;
import static org.junit.Assert.assertThat;

import java.io.File;

import javax.inject.Inject;

import org.junit.Test;

import com.google.common.io.Files;

public class SjisEncoderTest extends TestBase {
    @Inject SjisEncoder sjisEncoder;

    @Test
    public void encodeFiles() throws Exception {
        final String text = "あいうえお";
        final File sjisFile = new File("target/sjis.txt");
        final File utf8File = new File("target/utf8.txt");
        Files.write(text, sjisFile, SJIS);
        Files.write(text, utf8File, UTF8);
        assertThat(Files.toString(sjisFile, SJIS), is(text));
        assertThat(Files.toString(utf8File, UTF8), is(text));
        sjisEncoder.encodeFiles(new String[]{sjisFile.getPath(), utf8File.getPath()});
        assertThat(Files.toString(sjisFile, SJIS), is(text));
        assertThat(Files.toString(utf8File, SJIS), is(text));
    }

}
