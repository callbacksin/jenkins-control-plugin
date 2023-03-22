package org.codinjutsu.tools.jenkins.view;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.vfs.VirtualFile;
import org.codinjutsu.tools.jenkins.logic.attributes.JenkinsVirtualFileAttributesService;
import org.jetbrains.annotations.NotNull;

import static org.codinjutsu.tools.jenkins.util.JenkinsScriptContentSaver.isJenkinsScriptSaved;

public class FileEditorEventsListener implements FileEditorManagerListener.Before {

    @Override
    public void beforeFileClosed(@NotNull FileEditorManager source, @NotNull VirtualFile file) {
        if (!isJenkinsScriptSaved(source, file)) return;
        JenkinsVirtualFileAttributesService attributesService = JenkinsVirtualFileAttributesService.getInstance(source.getProject());
        attributesService.removeAttributes(file);
        FileEditorManagerListener.Before.super.beforeFileClosed(source, file);
    }

}
