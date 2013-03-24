package org.hogel;

import static org.hamcrest.CoreMatchers.is;
import static org.hogel.Encoding.SJIS;
import static org.hogel.Encoding.UTF8;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;

import org.fest.swing.edt.FailOnThreadViolationRepaintManager;
import org.fest.swing.edt.GuiActionRunner;
import org.fest.swing.edt.GuiQuery;
import org.fest.swing.fixture.FrameFixture;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.io.Files;

public class MainWindowTest {

    private FrameFixture frame;
    private MainWindow window;

    @BeforeClass
    public static void beforeClass() {
        FailOnThreadViolationRepaintManager.install();
    }

    @Test
    public void test_show() {
        window = GuiActionRunner.execute(new GuiQuery<MainWindow>() {
            @Override
            protected MainWindow executeInEDT() {
                return new MainWindow();
            }
        });
        frame = new FrameFixture(window.frame);
        frame.show();
    }

    @Test
    public void test_arguments() throws IOException {
        final String text = "あいうえお";
        final File sjisFile = new File("target/sjis.txt");
        final File utf8File = new File("target/utf8.txt");
        Files.write(text, sjisFile, SJIS);
        Files.write(text, utf8File, UTF8);
        assertThat(Files.toString(sjisFile, SJIS), is(text));
        assertThat(Files.toString(utf8File, UTF8), is(text));
        MainWindow.main(new String[]{sjisFile.getPath(), utf8File.getPath()});
        assertThat(Files.toString(sjisFile, SJIS), is(text));
        assertThat(Files.toString(utf8File, SJIS), is(text));
    }

}
