package jenkins.plugins.workspace_cleaner;

import hudson.FilePath;
import hudson.model.AbstractBuild;
import hudson.scm.SCM;

import java.io.IOException;
import java.util.List;

import com.google.common.collect.Lists;

public class FilePathAdapter {

    private static final String SVN_DIRECTORY_NAME = ".svn";

    public void deleteRecursive(final FilePath filePath) throws IOException, InterruptedException {
        filePath.deleteRecursive();
    }

    public String getName(final FilePath filePath) {
        return filePath.getName();
    }

    public List<FilePath> getFilesInWorkspace(final AbstractBuild<?, ?> build) throws IOException, InterruptedException {
        return build.getWorkspace().list();
    }

    public List<FilePath> getModulesInScm(final AbstractBuild<?, ?> build) {
        final SCM scm = build.getProject().getRootProject().getScm();
        return Lists.newArrayList(scm.getModuleRoots(build.getWorkspace(), build));
    }

    public boolean isSvnModule(final FilePath filePath) throws IOException, InterruptedException {
        if (!filePath.isDirectory()) {
            return false;
        }
        final List<FilePath> childFilePaths = filePath.list();
        for (final FilePath child : childFilePaths) {
            if (SVN_DIRECTORY_NAME.equals(child.getName()) && child.isDirectory()) {
                return true;
            }
        }
        return false;
    }

}
