package jenkins.plugins.workspace_cleaner;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import hudson.FilePath;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.scm.NullSCM;
import hudson.scm.SubversionSCM;

import java.io.File;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import com.google.common.collect.Lists;

@SuppressWarnings("rawtypes")
public class ModuleCleanerTest extends AbstractMockitoTestCase {

    @Mock
    private AbstractBuild build;

    @Mock
    private FilePathAdapter filePathAdapter;

    @Mock
    private Messenger messenger;

    @Mock
    private AbstractProject project;

    @Mock
    private AbstractProject rootProject;

    @Mock
    private SubversionSCM subversionScm;

    @Mock
    private NullSCM nullScm;

    private final FilePath module1 = new FilePath(new File("module1"));
    private final FilePath module2 = new FilePath(new File("module2"));
    private final FilePath dotFolder = new FilePath(new File(".folder"));
    private final FilePath file = new FilePath(new File("file.txt"));

    @Before
    public void setUp() throws Exception {
        when(build.getProject()).thenReturn(project);
        when(project.getRootProject()).thenReturn(rootProject);
        when(rootProject.getScm()).thenReturn(subversionScm);
        when(filePathAdapter.isDirectory(module1)).thenReturn(true);
        when(filePathAdapter.isDirectory(module2)).thenReturn(true);
        when(filePathAdapter.isDirectory(dotFolder)).thenReturn(true);
        when(filePathAdapter.isDirectory(file)).thenReturn(false);
        when(filePathAdapter.getName(module1)).thenReturn("module1");
        when(filePathAdapter.getName(module2)).thenReturn("module2");
        when(filePathAdapter.getName(dotFolder)).thenReturn(".folder");
        when(filePathAdapter.getName(file)).thenReturn("file.txt");
    }

    @Test
    public void shouldOnlyInformWhenNotSubversionScm() throws Exception {
        when(rootProject.getScm()).thenReturn(nullScm);

        whenRemovingModules();

        verify(messenger).informNotSubversionScm();
    }

    private void whenRemovingModules() {
        ModuleCleaner.removeUnconfiguredModules(build, filePathAdapter, messenger);
    }

    @Test
    public void shouldDeleteModuleInFilesystemWhenNoneInScm() throws Exception {
        givenFilesInWorkspace(module1);
        givenNoModulesInScm();

        whenRemovingModules();

        verify(filePathAdapter).deleteRecursive(module1);
    }

    @Test
    public void shouldInformWhenDeletingModule() throws Exception {
        givenFilesInWorkspace(module1);
        givenNoModulesInScm();

        whenRemovingModules();

        verify(messenger).informDirectoryDeleted(module1);
    }

    @Test
    public void shouldNotDeleteDirInFilesystemWhenInScm() throws Exception {
        givenFilesInWorkspace(module1);
        givenModulesInScm(module1, module2);

        whenRemovingModules();

        verify(filePathAdapter, never()).deleteRecursive(any(FilePath.class));
    }

    @Test
    public void shouldNotDeleteFiles() throws Exception {
        givenFilesInWorkspace(file);
        givenNoModulesInScm();

        whenRemovingModules();

        verify(filePathAdapter, never()).deleteRecursive(file);
    }

    @Test
    public void shouldNotDeleteDotFolders() throws Exception {
        givenFilesInWorkspace(dotFolder);
        givenNoModulesInScm();

        whenRemovingModules();

        verify(filePathAdapter, never()).deleteRecursive(dotFolder);
    }

    @Test
    public void shouldNotLogWhenNotDeleting() throws Exception {
        givenFilesInWorkspace(module1);
        givenModulesInScm(module1);

        whenRemovingModules();

        verifyNoMoreInteractions(messenger);
    }

    private void givenNoModulesInScm() throws Exception {
        givenModulesInScm();
    }

    private void givenFilesInWorkspace(final FilePath... files) throws Exception {
        when(filePathAdapter.getFilesInWorkspace(build)).thenReturn(Lists.newArrayList(files));
    }

    private void givenModulesInScm(final FilePath... modules) throws Exception {
        when(filePathAdapter.getModulesInScm(build)).thenReturn(Lists.newArrayList(modules));
    }
}
