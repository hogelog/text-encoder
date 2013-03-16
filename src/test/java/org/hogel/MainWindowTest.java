package org.hogel;

import org.fest.swing.edt.FailOnThreadViolationRepaintManager;
import org.fest.swing.edt.GuiActionRunner;
import org.fest.swing.edt.GuiQuery;
import org.fest.swing.fixture.FrameFixture;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class MainWindowTest {

    private FrameFixture frame;
    private MainWindow window;

    @BeforeClass
    public static void beforeClass() {
        FailOnThreadViolationRepaintManager.install();
    }

    @Before
    public void before() {
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
    public void test() {
    }

}
