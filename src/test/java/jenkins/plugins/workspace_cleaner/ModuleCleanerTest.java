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
    private final FilePath directory = new FilePath(new File("folder"));
    private final FilePath file = new FilePath(new File("file.txt"));

    @Before
    public void setUp() throws Exception {
        when(build.getProject()).thenReturn(project);
        when(project.getRootProject()).thenReturn(rootProject);
        when(rootProject.getScm()).thenReturn(subversionScm);
        givenIsSvnModule(module1);
        givenIsSvnModule(module2);
        givenIsDirectory(directory);
        givenIsFile(file);
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
    public void shouldNotDeleteFoldersWithoutDotSvnChild() throws Exception {
        givenFilesInWorkspace(directory);
        givenNoModulesInScm();

        whenRemovingModules();

        verify(filePathAdapter, never()).deleteRecursive(directory);
    }

    @Test
    public void shouldNotLogWhenNotDeleting() throws Exception {
        givenFilesInWorkspace(module1);
        givenModulesInScm(module1);

        whenRemovingModules();

        verifyNoMoreInteractions(messenger);
    }

    @Test
    public void shouldDeleteNestedModuleInFilesystemWhenNotInScm() throws Exception {
        givenFilesInWorkspace(directory);
        givenFilesInDirectory(directory, module1, module2);
        givenModulesInScm(module1);

        whenRemovingModules();

        verify(filePathAdapter).deleteRecursive(module2);
    }

    @Test
    public void shouldNotDeleteNestedModuleInFilesystemWhenInScm() throws Exception {
        givenFilesInWorkspace(directory);
        givenFilesInDirectory(directory, module1, module2);
        givenModulesInScm(module1);

        whenRemovingModules();

        verify(filePathAdapter, never()).deleteRecursive(module1);
    }

    private void givenIsFile(final FilePath filePath) throws Exception {
        when(filePathAdapter.isSvnModule(filePath)).thenReturn(false);
        when(filePathAdapter.isDirectory(filePath)).thenReturn(false);
    }

    private void givenIsDirectory(final FilePath filePath) throws Exception {
        when(filePathAdapter.isSvnModule(filePath)).thenReturn(false);
        when(filePathAdapter.isDirectory(filePath)).thenReturn(true);
    }

    private void givenIsSvnModule(final FilePath filePath) throws Exception {
        when(filePathAdapter.isSvnModule(filePath)).thenReturn(true);
        when(filePathAdapter.isDirectory(filePath)).thenReturn(true);
    }

    private void givenNoModulesInScm() throws Exception {
        givenModulesInScm();
    }

    private void givenFilesInWorkspace(final FilePath... files) throws Exception {
        when(filePathAdapter.getFilesInWorkspace(build)).thenReturn(Lists.newArrayList(files));
    }

    private void givenFilesInDirectory(final FilePath parentDirectory, final FilePath... files)
            throws Exception {
        when(filePathAdapter.listDirectory(parentDirectory)).thenReturn(
                Lists.newArrayList(files));
    }

    private void givenModulesInScm(final FilePath... modules) throws Exception {
        when(filePathAdapter.getModulesInScm(build)).thenReturn(Lists.newArrayList(modules));
    }

}

