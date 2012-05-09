package jenkins.plugins.workspace_cleaner;

import hudson.FilePath;
import hudson.model.AbstractBuild;
import hudson.scm.SCM;

import java.io.IOException;
import java.util.List;

import com.google.common.collect.Lists;

public class FilePathAdapter {

    public boolean isDirectory(final FilePath filePath) throws IOException, InterruptedException {
        return filePath.isDirectory();
    }

    public List<FilePath> list(final FilePath filePath) throws IOException, InterruptedException {
        return filePath.list();
    }

    public void deleteRecursive(final FilePath filePath) throws IOException, InterruptedException {
        filePath.deleteRecursive();
    }

    public String getName(final FilePath filePath) {
        return filePath.getName();
    }

    public FilePath getWorkspace(final AbstractBuild<?, ?> build) {
        return build.getWorkspace();
    }

    public List<FilePath> getFilesInWorkspace(final AbstractBuild<?, ?> build) throws IOException, InterruptedException {
        return getWorkspace(build).list();
    }

    public List<FilePath> getModulesInScm(final AbstractBuild<?, ?> build) {
        final SCM scm = build.getProject().getRootProject().getScm();
        return Lists.newArrayList(scm.getModuleRoots(build.getWorkspace(), build));
    }

}
