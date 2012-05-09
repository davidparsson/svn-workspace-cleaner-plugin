package jenkins.plugins.workspace_cleaner;

import hudson.FilePath;
import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import hudson.scm.NullSCM;
import hudson.scm.SubversionSCM;

import java.io.File;
import java.io.IOException;

import org.jvnet.hudson.test.HudsonHomeLoader.CopyExisting;
import org.jvnet.hudson.test.HudsonTestCase;

@SuppressWarnings({ "deprecation" })
public class PluginAcceptanceTest extends HudsonTestCase {

    private static final String MODULE_1 = "module1";
    private static final String MODULE_2 = "module2";
    private FreeStyleProject job;
    private FreeStyleBuild currentBuild;

    public void testShouldOnlyLogWhenNotSubversionScm() throws Exception {
        givenJobWithNullScm();

        currentBuild = build(job);

        assertLogContains(Messenger.NOT_SUBVERSION_SCM, currentBuild);
    }

    public void testShouldRemoveDeletedModule() throws Exception {
        givenJobWithDeletedModule();
        assertDirectoryInWorkspace(MODULE_1);
        assertDirectoryInWorkspace(MODULE_2);

        currentBuild = build(job);

        assertDirectoryInWorkspace(MODULE_1);
        assertDirectoryNotInWorkspace(MODULE_2);
    }

    private void assertDirectoryNotInWorkspace(final String directoryName) throws Exception {
        final FilePath directory = currentBuild.getWorkspace().child(directoryName);
        assertFalse("Expected directory '" + directory + "' to be removed, but was not!",
                directory.exists());
    }

    private void assertDirectoryInWorkspace(final String directoryName) throws Exception {
        final FilePath directory = currentBuild.getWorkspace().child(directoryName);
        assertTrue("Expected directory '" + directory + "' to exist, but did not!",
                directory.exists());
    }

    private void givenJobWithDeletedModule() throws Exception {
        job = getJobNamed("subversion-scm-job");
        job.setScm(getScmWithModules(MODULE_1, MODULE_2));
        currentBuild = build(job);
        job.setScm(getScmWithModules(MODULE_1));
    }

    private FreeStyleBuild build(final FreeStyleProject job) throws Exception {
        return job.scheduleBuild2(0).get();
    }

    private SubversionSCM getScmWithModules(final String... modules) throws Exception {
        final File repo = getRepoWithTwoModules();
        final String svnUrl = "file://" + repo.getPath();
        final String[] svnUrls = new String[modules.length];
        for (int i = 0; i < modules.length; i++) {
            svnUrls[i] = svnUrl + "/" + modules[i];
        }
        return new SubversionSCM(svnUrls, modules, true, null);
    }

    private void givenJobWithNullScm() throws Exception {
        job = getJobNamed("no-scm-job");
        job.setScm(new NullSCM());
    }

    private FreeStyleProject getJobNamed(final String name) throws IOException {
        final FreeStyleProject newJob = createFreeStyleProject(name);
        newJob.getBuildWrappersList().add(new JenkinsGlue());
        newJob.setAssignedLabel(hudson.getSelfLabel());
        return newJob;
    }

    /**
     * Repo at revision 1 with structure
     *   module1/
     *           file1
     *   module2/
     *           file2
     */
    private File getRepoWithTwoModules() throws Exception {
        return new CopyExisting(getClass().getResource("repoWithTwoModules.zip")).allocate();
    }

}
