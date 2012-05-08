package jenkins.plugins.workspace_cleaner;

import java.io.PrintStream;

class Messenger {

    public static final String NOT_SUBVERSION_SCM =
            "Will not remove any modules since this is not a Subversion job.";

    private final PrintStream logger;

    Messenger(final PrintStream logger) {
        this.logger = logger;
    }

    void informNotSubversionScm() {
        logger.println(NOT_SUBVERSION_SCM);
    }

    void log(final String message) {
        logger.println(message);
    }

}
