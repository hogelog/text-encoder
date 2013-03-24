package org.hogel;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class TextEncoderModuleTest extends TestBase {

    @Test
    public void test() {
        final Configuration config = injector.getInstance(Configuration.class);
        assertThat(config.getConfigFile().getPath(), is("config.yaml"));

        final Encoding encoding = injector.getInstance(Encoding.class);
        System.err.println(encoding);
    }

}
