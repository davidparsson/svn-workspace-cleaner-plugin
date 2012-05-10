package jenkins.plugins.workspace_cleaner;

import hudson.FilePath;
import hudson.model.AbstractBuild;
import hudson.scm.SubversionSCM;

import java.io.IOException;
import java.util.List;

public class ModuleCleaner {

    static void removeUnconfiguredModules(final AbstractBuild<?, ?> build,
            final FilePathAdapter filePathAdapter, final Messenger messenger) {
        if (hasNoSubversionScm(build)) {
            messenger.informNotSubversionScm();
            return;
        }
        try {
            final List<FilePath> fileSystemModules = getModulesInFileSystem(build, filePathAdapter);
            final List<FilePath> scmModules = filePathAdapter.getModulesInScm(build);
            for (final FilePath fileSystemModule : fileSystemModules) {
                if (filePathNotInScm(scmModules, fileSystemModule)) {
                    filePathAdapter.deleteRecursive(fileSystemModule);
                    messenger.informDirectoryDeleted(fileSystemModule);
                }
            }
        } catch (final IOException e) {
            messenger.printStackTrace(e);
        } catch (final InterruptedException e) {
            messenger.printStackTrace(e);
        }
    }

    private static List<FilePath> getModulesInFileSystem(final AbstractBuild<?, ?> build, final FilePathAdapter filePathAdapter)
            throws IOException, InterruptedException {
        final List<FilePath> files = filePathAdapter.getFilesInWorkspace(build);
        for (int i = files.size() - 1; i >= 0; i--) {
            if (!filePathAdapter.isSvnModule(files.get(i))) {
                files.remove(i);
            }
        }
        return files;
    }

    private static boolean filePathNotInScm(final List<FilePath> scmModules,
            final FilePath fileSystemModule) {
        return !filePathIn(scmModules, fileSystemModule);
    }

    private static boolean filePathIn(final List<FilePath> scmModules,
            final FilePath fileSystemModule) {
        for (final FilePath moduleInScm : scmModules) {
            if (moduleInScm.equals(fileSystemModule)) {
                return true;
            }
        }
        return false;
    }

    private static boolean hasNoSubversionScm(final AbstractBuild<?, ?> build) {
        return !(build.getProject().getRootProject().getScm() instanceof SubversionSCM);
    }

}
