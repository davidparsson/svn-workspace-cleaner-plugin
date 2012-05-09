package jenkins.plugins.workspace_cleaner;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import hudson.FilePath;

import java.io.File;
import java.io.PrintStream;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;


public class MessengerTest extends AbstractMockitoTestCase {

    private static final String GENERIC_TEXT = "Some generic text";
    private static final String DIRECTORY_NAME = "Directory name";

    private Messenger messenger;

    @Mock
    private PrintStream logger;

    @Mock
    private FilePathAdapter filePathAdapter;

    @Mock
    private Exception exception;

    private final FilePath directory = new FilePath(new File(""));

    @Before
    public void setup() {
        messenger = new Messenger(logger, filePathAdapter);
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
    public void logsDirectoryDeleted() throws Exception {
        when(filePathAdapter.getName(directory)).thenReturn(DIRECTORY_NAME);

        messenger.informDirectoryDeleted(directory);

        verify(logger).println(String.format(Messenger.DIRECTORY_DELETED, DIRECTORY_NAME));
    }

    @Test
    public void logsStackTrace() throws Exception {
        messenger.printStackTrace(exception);
        verify(exception).printStackTrace(logger);
    }
}
