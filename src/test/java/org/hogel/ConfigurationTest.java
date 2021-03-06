package org.hogel;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

public class ConfigurationTest {
    @Before
    public void before () {
        new File("target/config.yaml").delete();
    }

    @Test
    public void pattern() throws IOException {
        final Configuration config = new Configuration("target/config.yaml");
        assertThat(config.getConfigFile().exists(), is(true));

        config.addReplacePattern("hoge", "log");
        assertThat(config.getReplacePattern("hoge"), is("log"));

        final String configText = Files.toString(config.getConfigFile(), Charsets.UTF_8);
        assertThat(configText.contains("hoge"), is(true));
        assertThat(configText.contains("log"), is(true));

        Map<String, String> replacePatterns = config.getReplacePatterns();
        assertThat(replacePatterns.size(), is(1));
        assertThat(replacePatterns.get("hoge"), is("log"));

        final Map<String, String> patterns = new LinkedHashMap<String, String>();
        patterns.put("moge", "mog");
        config.setReplacePatterns(patterns);

        replacePatterns = config.getReplacePatterns();
        assertThat(replacePatterns.size(), is(1));
        assertThat(replacePatterns.get("moge"), is("mog"));
    }

    @Test
    public void confirm() throws IOException {
        final Configuration config = new Configuration("target/config.yaml");

        Map<String, String> replacePatterns = config.getReplacePatterns();
        assertThat(config.isShowConfirmDialog(), is(false));
    }
}
