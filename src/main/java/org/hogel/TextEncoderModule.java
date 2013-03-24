package org.hogel;

import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.Provides;

public class TextEncoderModule extends AbstractModule implements Module {

    private Configuration config;
    private final Printer printer;

    public TextEncoderModule(Printer printer) {
        this.printer = printer;
    }

    @Override
    protected void configure() {
        config = new Configuration("config.yaml");
    }

    @Provides
    Configuration provideConfiguration() {
        return config;
    }

    @Provides
    Printer providePrinter() {
        return printer;
    }
}
