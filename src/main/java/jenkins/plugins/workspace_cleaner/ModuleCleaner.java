package jenkins.plugins.workspace_cleaner;

import hudson.FilePath;
import hudson.model.AbstractBuild;
import hudson.scm.SubversionSCM;

import java.io.IOException;
import java.util.List;

import com.google.common.collect.Lists;

public class ModuleCleaner {

    static void removeUnconfiguredModules(final AbstractBuild<?, ?> build,
            final FilePathAdapter filePathAdapter, final Messenger messenger) {
        if (hasNoSubversionScm(build)) {
            messenger.informNotSubversionScm();
            return;
        }
        try {
            final List<FilePath> fileSystemModules = getSvnModulesInFileSystem(build, filePathAdapter);
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

    private static List<FilePath> getSvnModulesInFileSystem(final AbstractBuild<?, ?> build, final FilePathAdapter filePathAdapter)
            throws IOException, InterruptedException {
        final List<FilePath> files = filePathAdapter.getFilesInWorkspace(build);
        for (int i = files.size() - 1; i >= 0; i--) {
            final FilePath currentFile = files.get(i);
            if (!filePathAdapter.isSvnModule(currentFile)) {
                files.remove(i);
                files.addAll(getSvnModulesIn(currentFile, filePathAdapter));
            }
        }
        return files;
    }

    private static List<FilePath> getSvnModulesIn(final FilePath parentFile, final FilePathAdapter filePathAdapter)
            throws IOException, InterruptedException {
        final List<FilePath> svnModules = Lists.newArrayList();
        if (!filePathAdapter.isDirectory(parentFile)) {
            return svnModules;
        }
        final List<FilePath> filesInDirectory = filePathAdapter.listDirectory(parentFile);
        for (final FilePath childFile : filesInDirectory) {
            if (filePathAdapter.isSvnModule(childFile)) {
                svnModules.add(childFile);
            } else if (filePathAdapter.isDirectory(childFile)) {
                svnModules.addAll(getSvnModulesIn(childFile, filePathAdapter));
            }
        }
        return svnModules;
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
