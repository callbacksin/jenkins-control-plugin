/*
 * Copyright (c) 2012 David Boissier
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.codinjutsu.tools.jenkins.view.action.search;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAware;
import org.apache.commons.lang.StringUtils;
import org.codinjutsu.tools.jenkins.util.GuiUtil;
import org.codinjutsu.tools.jenkins.view.JobSearchComponent;

import javax.swing.*;
import java.awt.event.KeyEvent;

public class NextOccurrenceAction extends AnAction implements DumbAware {

    private static final Icon NEXT_ICON = GuiUtil.isUnderDarcula() ? GuiUtil.loadIcon("next_dark.png") : GuiUtil.loadIcon("next.png");

    private final JobSearchComponent jobSearchComponent;

    public NextOccurrenceAction(JobSearchComponent jobSearchComponent) {
        super("Search Next", "Search the next occurrence", NEXT_ICON);
        this.jobSearchComponent = jobSearchComponent;

        registerCustomShortcutSet(KeyEvent.VK_F3, 0, jobSearchComponent);
        registerCustomShortcutSet(KeyEvent.VK_DOWN, 0, jobSearchComponent);
    }

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        jobSearchComponent.findNextOccurrence(jobSearchComponent.getSearchField().getText());
    }

    @Override
    public void update(final AnActionEvent e) {
        e.getPresentation().setEnabled(StringUtils.isNotEmpty(jobSearchComponent.getSearchField().getText()));
    }

}
