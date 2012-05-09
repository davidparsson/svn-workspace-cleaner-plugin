package jenkins.plugins.workspace_cleaner;

import hudson.FilePath;

import java.io.PrintStream;

class Messenger {

    private static final String PREFIX = "[WORKSPACE-CLEANER] ";

    static final String NOT_SUBVERSION_SCM = PREFIX +
            "Will not remove any modules since this is not a Subversion job.";

    static final String DIRECTORY_DELETED = PREFIX +
            "Deleting directory '%s' since it's not a configured module in SVN.";

    private final PrintStream logger;

    private final FilePathAdapter filePathAdapter;

    Messenger(final PrintStream logger, final FilePathAdapter filePathAdapter) {
        this.logger = logger;
        this.filePathAdapter = filePathAdapter;
    }

    void informNotSubversionScm() {
        logger.println(NOT_SUBVERSION_SCM);
    }

    void log(final String message) {
        logger.println(message);
    }

    public void printStackTrace(final Exception exception) {
        exception.printStackTrace(logger);
    }

    public void informDirectoryDeleted(final FilePath deletedDirectory) {
        logger.println(String.format(DIRECTORY_DELETED, filePathAdapter.getName(deletedDirectory)));
    }

}
