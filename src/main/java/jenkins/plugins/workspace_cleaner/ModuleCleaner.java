package jenkins.plugins.workspace_cleaner;

import hudson.FilePath;
import hudson.model.AbstractBuild;
import hudson.scm.SCM;
import hudson.scm.SubversionSCM;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;

public class ModuleCleaner {

    static void removeUnconfiguredModules(final AbstractBuild<?, ?> build,
            final Messenger messenger) {
        if (hasNoSubversionScm(build)) {
            messenger.informNotSubversionScm();
            return;
        }
        try {
            final List<FilePath> modulesInFileSystem = getModulesInFileSystem(build);
            final List<FilePath> modulesInScm = getModulesInScm(build);
            for (final FilePath moduleInFileSystem : modulesInFileSystem) {
                if (filePathNotIn(modulesInScm, moduleInFileSystem)) {
                    moduleInFileSystem.deleteRecursive();
                }
            }
        } catch (final IOException e) {
            messenger.printStackTrace(e);
        } catch (final InterruptedException e) {
            messenger.printStackTrace(e);
        }
    }

    private static List<FilePath> getModulesInFileSystem(final AbstractBuild<?, ?> build)
            throws IOException, InterruptedException {
        final List<FilePath> modules = build.getWorkspace().list();
        for (int i = modules.size() - 1; i >= 0; i--) {
            if (!modules.get(i).isDirectory()) {
                modules.remove(i);
            }
        }
        return modules;
    }

    private static ArrayList<FilePath> getModulesInScm(final AbstractBuild<?, ?> build) {
        final SCM scm = build.getProject().getRootProject().getScm();
        return Lists.newArrayList(scm.getModuleRoots(build.getWorkspace(), build));
    }

    private static boolean filePathNotIn(final List<FilePath> modulesInScm,
            final FilePath moduleInFileSystem) {
        return !filePathIn(modulesInScm, moduleInFileSystem);
    }

    private static boolean filePathIn(final List<FilePath> modulesInScm,
            final FilePath moduleInFileSystem) {
        for (final FilePath moduleInScm : modulesInScm) {
            if (moduleInScm.equals(moduleInFileSystem)) {
                return true;
            }
        }
        return false;
    }

    private static boolean hasNoSubversionScm(final AbstractBuild<?, ?> build) {
        return !(build.getProject().getRootProject().getScm() instanceof SubversionSCM);
    }

}
