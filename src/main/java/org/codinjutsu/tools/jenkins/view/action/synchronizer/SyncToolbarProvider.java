package org.codinjutsu.tools.jenkins.view.action.synchronizer;

import com.intellij.openapi.editor.toolbar.floating.AbstractFloatingToolbarProvider;

public class SyncToolbarProvider extends AbstractFloatingToolbarProvider {
    private static final String ACTION_GROUP = "org.codinjutsu.tools.jenkins.view.action.synchronizer.SyncActionGroup";

    public SyncToolbarProvider() {
        super(ACTION_GROUP);
    }

    @Override
    public boolean getAutoHideable() {
        return true;
    }
}
