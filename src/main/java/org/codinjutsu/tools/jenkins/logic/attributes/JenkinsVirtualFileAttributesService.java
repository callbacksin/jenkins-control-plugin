package org.codinjutsu.tools.jenkins.logic.attributes;

import com.intellij.openapi.components.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(name = "JenkinsVirtualFileAttributesService", storages = {
        @Storage(value = StoragePathMacros.PRODUCT_WORKSPACE_FILE, roamingType = RoamingType.DISABLED)
})
public final class JenkinsVirtualFileAttributesService implements PersistentStateComponent<JenkinsVirtualFileAttributesState> {
    private JenkinsVirtualFileAttributesState state = new JenkinsVirtualFileAttributesState();

    @NotNull
    private final Project project;

    public JenkinsVirtualFileAttributesService(@NotNull Project project) {
        this.project = project;
    }

    public static JenkinsVirtualFileAttributesService getInstance(@NotNull Project project) {
        return project.getService(JenkinsVirtualFileAttributesService.class);
    }

    @Override
    public @Nullable JenkinsVirtualFileAttributesState getState() {
        return state;
    }

    @Override
    public void loadState(@NotNull JenkinsVirtualFileAttributesState state) {
        this.state = state;
    }

    public JenkinsVirtualFileAttributes getAttributes(VirtualFile virtualFile) {
        return this.state.getFileToAttributesMap().get(virtualFile);
    }

    public void putAttributes(VirtualFile file, JenkinsVirtualFileAttributes attributes) {
        this.state.getFileToAttributesMap().put(file, attributes);
    }

    public boolean hasAttributes(VirtualFile file) {
        return this.state.getFileToAttributesMap().containsKey(file);
    }

    public void removeAttributes(VirtualFile file) {
        this.state.getFileToAttributesMap().remove(file);
    }
}

