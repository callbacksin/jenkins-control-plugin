package org.codinjutsu.tools.jenkins.view.action;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import org.codinjutsu.tools.jenkins.JenkinsAppSettings;
import org.codinjutsu.tools.jenkins.logic.RequestManager;
import org.codinjutsu.tools.jenkins.logic.attributes.JenkinsVirtualFileAttributes;
import org.codinjutsu.tools.jenkins.logic.attributes.JenkinsVirtualFileAttributesService;
import org.codinjutsu.tools.jenkins.model.Job;
import org.codinjutsu.tools.jenkins.view.BrowserPanel;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.Collections;

import static org.codinjutsu.tools.jenkins.util.ScriptParserUtilKt.substituteDescriptionAndGetNewJobConfig;

public class CreateJobAction extends AnAction implements DumbAware {

    private final BrowserPanel browserPanel;
    private static final Logger LOG = Logger.getInstance(CreateJobAction.class.getName());

    public CreateJobAction(BrowserPanel browserPanel) {
        super("Create Job", "Create a declarative pipeline", AllIcons.Actions.AddFile);
        this.browserPanel = browserPanel;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        ActionUtil.getProject(e).ifPresent(this::actionPerformed);
    }

    private void actionPerformed(@NotNull Project project) {
        final RequestManager requestManager = RequestManager.getInstance(project);
        CreateDeclarativeJobDialog dialog = new CreateDeclarativeJobDialog(project);
        CreateJobDialogState state;
        if (dialog.showAndGet() && dialog.getExitCode() == DialogWrapper.OK_EXIT_CODE) {
            state = dialog.getState();
        } else {
            return;
        }
        String jobXml = substituteDescriptionAndGetNewJobConfig(state.getDescription());
        try {
            requestManager.createJob(state.getName(), jobXml);
        } catch (IOException e) {
            browserPanel.notifyErrorJenkinsToolWindow("Could not create the job on Jenkins Server: " + e.getMessage());
            return;
        }
        Job job = Job.builder().name(state.getName()).buildable(false).fullName(state.getName()).url("")
                .parameters(Collections.emptyList()).inQueue(false).build();
        File tempFile;
        try {
            tempFile = FileUtil.createTempFile(state.getName(), ".groovy");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        VirtualFile vf = LocalFileSystem.getInstance().findFileByIoFile(tempFile);
        if( vf != null ) {
            JenkinsVirtualFileAttributesService attributesService = JenkinsVirtualFileAttributesService.getInstance(project);
            attributesService.putAttributes(vf, new JenkinsVirtualFileAttributes(job, jobXml));
            FileEditor[] editors =
                    FileEditorManager.getInstance(project).openFile(vf, true);
        }
        try {
            browserPanel.refreshCurrentView();
        } catch (Exception ex) {
            browserPanel.notifyErrorJenkinsToolWindow("Unable to refresh: " + ex.getMessage());
        }
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.EDT;
    }

    @Override
    public void update(@NotNull AnActionEvent event) {
        boolean isServerConnected = !browserPanel.getJenkins().getServerUrl().equals(JenkinsAppSettings.DUMMY_JENKINS_SERVER_URL);
        event.getPresentation().setEnabled(browserPanel.isConfigured() && isServerConnected);
    }
}
