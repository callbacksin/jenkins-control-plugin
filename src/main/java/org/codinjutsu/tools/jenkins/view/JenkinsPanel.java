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

package org.codinjutsu.tools.jenkins.view;

import javax.swing.*;
import java.awt.*;

public class JenkinsPanel extends JPanel {

    public static JenkinsPanel onePanel(JenkinsBrowserPanel jenkinsBrowserPanel, RssLatestBuildPanel rssLatestJobPanel) {
        return new JenkinsPanel(jenkinsBrowserPanel, rssLatestJobPanel);
    }

//    public static JenkinsPanel browserOnly(JenkinsBrowserPanel jenkinsBrowserPanel) {
//        return new JenkinsPanel(jenkinsBrowserPanel);
//    }
//
//    private JenkinsPanel(JenkinsBrowserPanel jenkinsBrowserPanel) {
//        setLayout(new BorderLayout());
//        add(jenkinsBrowserPanel, BorderLayout.CENTER);
//    }

    private JenkinsPanel(JenkinsBrowserPanel jenkinsBrowserPanel, RssLatestBuildPanel rssLatestJobPanel) {
        setLayout(new BorderLayout());

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setOneTouchExpandable(true);
        splitPane.setTopComponent(jenkinsBrowserPanel);
        splitPane.setBottomComponent(rssLatestJobPanel);
        splitPane.setDividerLocation(600);

        add(splitPane, BorderLayout.CENTER);
    }
}
