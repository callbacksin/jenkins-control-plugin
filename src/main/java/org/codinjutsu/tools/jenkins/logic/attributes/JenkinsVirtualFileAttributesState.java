package org.codinjutsu.tools.jenkins.logic.attributes;

import com.intellij.openapi.vfs.VirtualFile;

import java.util.HashMap;

final public class JenkinsVirtualFileAttributesState {
    private final HashMap<VirtualFile, JenkinsVirtualFileAttributes> fileToAttributesMap = new HashMap<>();
    public HashMap<VirtualFile, JenkinsVirtualFileAttributes> getFileToAttributesMap() {
        return fileToAttributesMap;
    }
}
