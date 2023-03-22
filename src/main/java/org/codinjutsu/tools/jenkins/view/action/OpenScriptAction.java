package org.codinjutsu.tools.jenkins.view.action;

import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.io.FileUtilRt;
import com.intellij.openapi.vfs.*;
import org.codinjutsu.tools.jenkins.logic.RequestManager;
import org.codinjutsu.tools.jenkins.logic.attributes.JenkinsVirtualFileAttributes;
import org.codinjutsu.tools.jenkins.logic.attributes.JenkinsVirtualFileAttributesService;
import org.codinjutsu.tools.jenkins.model.Job;
import org.codinjutsu.tools.jenkins.view.BrowserPanel;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

public class OpenScriptAction extends AnAction implements DumbAware {
    public static final String ACTION_ID = "Jenkins.OpenScript";
    private static final Logger LOG = Logger.getInstance(OpenScriptAction.class.getName());

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        ActionUtil.getProject(e).ifPresent(this::actionPerformed);
    }

    private void actionPerformed(@NotNull Project project) {
        final BrowserPanel browserPanel = BrowserPanel.getInstance(project);
        final LocalFileSystem localFileSystem = LocalFileSystem.getInstance();
        final FileEditorManager fileEditorManager = FileEditorManager.getInstance(project);
        final JenkinsVirtualFileAttributesService attributesService = JenkinsVirtualFileAttributesService.getInstance(project);
        try {
            Optional.ofNullable(browserPanel.getSelectedJob()).ifPresent(job -> {
                String scriptFileAbsolutePath = FileUtilRt.getTempDirectory() + "\\" + job.getFullName() + ".groovy";

                VirtualFile scriptVirtualFile = VirtualFileManager.getInstance().findFileByUrl("file:///" + scriptFileAbsolutePath);
                boolean isInJenkinsAttributesMap = attributesService.hasAttributes(scriptVirtualFile);
                if (scriptVirtualFile != null && fileEditorManager.isFileOpen(scriptVirtualFile)
                        || isInJenkinsAttributesMap) {
                    browserPanel.notifyInfoJenkinsToolWindow("Already opened");
                    return;
                }

                File scriptFile = new File(scriptFileAbsolutePath);
                boolean fileExists = Optional.ofNullable(localFileSystem.findFileByIoFile(scriptFile)).isPresent();
                if (fileExists && !scriptFile.delete()) {
                    LOG.error("Something went wrong: see file already exist, tried to delete it. Unsuccessfully");
                    return;
                }

                openScript(project, browserPanel, localFileSystem, job, attributesService);
            });
        } catch (Exception ex) {
            final String message = ex.getMessage() == null ? "Unknown error" : ex.getMessage();
            LOG.error(message, ex);
            browserPanel.notifyErrorJenkinsToolWindow("Can't open the script: " + message);
        }
    }

    private void openScript(@NotNull Project project, BrowserPanel browserPanel, LocalFileSystem localFileSystem,
                            @NotNull Job job, JenkinsVirtualFileAttributesService attributesService) {
        File scriptFile = null;
        VirtualFile scriptVirtualFile;
        final RequestManager requestManager = RequestManager.getInstance(project);
        final String jobXml = requestManager.loadJobConfig(job);
        final String script = requestManager.getJobScript(jobXml);
        try {
            scriptFile = FileUtil.createTempFile(job.getFullName(), ".groovy");
        } catch (IOException e) {
            browserPanel.notifyErrorJenkinsToolWindow("Can't create a file for the script. Something went wrong");
            throw new RuntimeException(e);
        }
        scriptVirtualFile = localFileSystem.findFileByIoFile(scriptFile);
        if( scriptVirtualFile != null ) {
            attributesService.putAttributes(scriptVirtualFile, new JenkinsVirtualFileAttributes(job, jobXml));
            ApplicationManager.getApplication().runWriteAction(() -> {
                try {
                    scriptVirtualFile.setBinaryContent(script.getBytes());
                } catch (IOException e) {
                    browserPanel.notifyErrorJenkinsToolWindow("Error occurred while writing the script content to the file");
                }
            });
            FileEditor[] editors =
                    FileEditorManager.getInstance(project).openFile(scriptVirtualFile, true);
        }
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        final Project project = ActionUtil.getProject(e).get();
        final BrowserPanel browserPanel = BrowserPanel.getInstance(project);
        final Job job = browserPanel.getSelectedJob();
        e.getPresentation().setVisible(job != null && browserPanel.hasXMLScriptTag(job));
    }
}
