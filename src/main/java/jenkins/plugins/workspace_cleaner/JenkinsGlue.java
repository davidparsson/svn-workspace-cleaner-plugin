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

public class JenkinsGlue extends BuildWrapper {

    @DataBoundConstructor
    public JenkinsGlue() {
    }

    @Override
    public ModuleCleanerDescriptorImpl getDescriptor() {
        return (ModuleCleanerDescriptorImpl)super.getDescriptor();
    }

    @Override
    @SuppressWarnings("rawtypes")
    public Environment setUp(final AbstractBuild build, final Launcher launcher,
            final BuildListener listener)
            throws IOException, InterruptedException {
        final Messenger messenger = new Messenger(listener.getLogger());
        return ModuleCleaner.removeUnconfiguredModules(build, messenger);
    }

    @Extension
    public static final class ModuleCleanerDescriptorImpl extends BuildWrapperDescriptor {

        @Override
        public boolean isApplicable(final AbstractProject<?, ?> item) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return "Cleans up unused Subversion modules.";
        }


    }

}
