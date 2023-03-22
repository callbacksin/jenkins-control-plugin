package org.codinjutsu.tools.jenkins.logic.attributes;

import lombok.Getter;
import lombok.Setter;
import org.codinjutsu.tools.jenkins.model.Job;
import org.jetbrains.annotations.NotNull;


@Getter
@Setter
public final class JenkinsVirtualFileAttributes {
    @NotNull
    private String jobXml;
    @NotNull
    private Job job;

    public JenkinsVirtualFileAttributes(@NotNull Job job, @NotNull String jobXml) {
        this.job = job;
        this.jobXml = jobXml;
    }
}
