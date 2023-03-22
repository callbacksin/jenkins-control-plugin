package org.codinjutsu.tools.jenkins.util;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.vfs.VirtualFile;
import org.codinjutsu.tools.jenkins.logic.RequestManager;
import org.codinjutsu.tools.jenkins.logic.attributes.JenkinsVirtualFileAttributes;
import org.codinjutsu.tools.jenkins.logic.attributes.JenkinsVirtualFileAttributesService;
import org.codinjutsu.tools.jenkins.model.Job;
import org.codinjutsu.tools.jenkins.view.BrowserPanel;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Arrays;

import static org.codinjutsu.tools.jenkins.util.ScriptParserUtilKt.updateContentOfTag;

public class JenkinsScriptContentSaver {
    private static final Logger logger = Logger.getInstance(JenkinsScriptContentSaver.class);

    public static boolean isJenkinsScriptSaved(@NotNull FileEditorManager source, @NotNull VirtualFile file) {
        Project project = source.getProject();
        JenkinsVirtualFileAttributesService attributesService = JenkinsVirtualFileAttributesService.getInstance(project);
        if (!attributesService.hasAttributes(file)) {
            return false;
        }
        JenkinsVirtualFileAttributes attributes = attributesService.getAttributes(file);
        Job requestedJob = attributes.getJob();
        String jobXml = attributes.getJobXml();

        FileDocumentManager fileDocumentManager = FileDocumentManager.getInstance();
        Document document = ApplicationManager.getApplication().runReadAction((Computable<Document>) () ->
            fileDocumentManager.getDocument(file)
        );
        if (document == null) {
            logger.info("Document cannot be used here");
            return false;
        }
        boolean isContentChanged;
        try {
            isContentChanged = !Arrays.equals(document.getText().getBytes(), file.contentsToByteArray());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if(isContentChanged) {
            Task.Modal task = new Task.Modal(source.getProject(), "Syncing " + file.getName(), true) {
                @Override
                public void run(@NotNull ProgressIndicator indicator) {
                    ApplicationManager.getApplication().invokeAndWait(() -> {
                        FileDocumentManager.getInstance().saveDocument(document);
                        RequestManager requestManager = RequestManager.getInstance(source.getProject());
                        String updatedScript = document.getText();
                        String updatedJobConf = updateContentOfTag("script", jobXml, updatedScript);
                        try {
                            requestManager.updateJobConfig(requestedJob, updatedJobConf);
                        } catch(IOException e) {
                            logger.warn("cannot update xml config for " + requestedJob.getFullName());
                            final BrowserPanel browserPanel = BrowserPanel.getInstance(project);
                            browserPanel.notifyErrorJenkinsToolWindow("Can't update the script. Request failed");
                        }
                    });
                }
            };
            ProgressManager.getInstance().run(task);
        }
        return true;
    }
}
