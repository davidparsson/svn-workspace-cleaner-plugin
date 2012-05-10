package jenkins.plugins.workspace_cleaner;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.tasks.BuildWrapper;
import hudson.tasks.BuildWrapperDescriptor;

import java.io.IOException;

import org.kohsuke.stapler.DataBoundConstructor;

@SuppressWarnings("rawtypes")
public class JenkinsGlue extends BuildWrapper {

    @DataBoundConstructor
    public JenkinsGlue() {
    }

    @Override
    public WorkspaceCleanerDescriptorImpl getDescriptor() {
        return (WorkspaceCleanerDescriptorImpl)super.getDescriptor();
    }

    @Override
    public Environment setUp(final AbstractBuild build, final Launcher launcher,
            final BuildListener listener) throws IOException, InterruptedException {
        final FilePathAdapter filePathAdapter = new FilePathAdapter();
        final Messenger messenger = new Messenger(listener.getLogger(), filePathAdapter);
        ModuleCleaner.removeUnconfiguredModules(build, filePathAdapter, messenger);
        return new Environment() {
        };
    }

    @Extension
    public static final class WorkspaceCleanerDescriptorImpl extends BuildWrapperDescriptor {

        @Override
        public boolean isApplicable(final AbstractProject<?, ?> item) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return "Clean up unused Subversion modules";
        }

    }

}
