package jenkins.plugins.workspace_cleaner;

import static org.mockito.Mockito.verify;

import java.io.PrintStream;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;


public class MessengerTest extends AbstractMockitoTestCase {

    private static final String GENERIC_TEXT = "Some generic text";

    private Messenger messenger;

    @Mock
    private PrintStream logger;

    @Mock
    private Exception exception;

    @Before
    public void setup() {
        messenger = new Messenger(logger);
    }

    @Test
    public void logNotSubversionScm() throws Exception {
        messenger.informNotSubversionScm();
        verify(logger).println(Messenger.NOT_SUBVERSION_SCM);
    }

    @Test
    public void logsInput() throws Exception {
        messenger.log(GENERIC_TEXT);
        verify(logger).println(GENERIC_TEXT);
    }

    @Test
    public void logsStackTrace() throws Exception {
        messenger.printStackTrace(exception);
        verify(exception).printStackTrace(logger);
    }
}
