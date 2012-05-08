package jenkins.plugins.workspace_cleaner;

import hudson.FilePath;
import hudson.model.AbstractBuild;
import hudson.scm.SubversionSCM;
import hudson.tasks.BuildWrapper.Environment;

import java.io.IOException;
import java.util.List;

public class ModuleCleaner {

    static Environment removeUnconfiguredModules(final AbstractBuild build, final Messenger messenger) {
        if (hasNoSubversionScm(build)) {
            messenger.informNotSubversionScm();
        }
        try {
            final List<FilePath> list = build.getWorkspace().list();
            for (final FilePath filePath : list) {
                messenger.log(filePath.toString());
            }
        } catch (final IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (final InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    private static boolean hasNoSubversionScm(final AbstractBuild build) {
        return !(build.getProject().getRootProject().getScm() instanceof SubversionSCM);
    }

}
