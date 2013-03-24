package org.hogel;

import org.junit.Before;

import com.google.inject.Guice;
import com.google.inject.Injector;

public abstract class TestBase {
    Injector injector;

    @Before
    public void before() {
        final Printer printer = getPrinter();
        injector = Guice.createInjector(new TextEncoderModule(printer));
        injector.injectMembers(this);
    }

    protected Printer getPrinter() {
        return new ConsolePrinter();
    }
}
