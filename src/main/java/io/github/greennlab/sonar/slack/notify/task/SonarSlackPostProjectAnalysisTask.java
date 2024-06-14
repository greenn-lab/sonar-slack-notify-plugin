package io.github.greennlab.sonar.slack.notify.task;

import com.slack.api.Slack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.ce.posttask.PostProjectAnalysisTask;
import org.sonar.api.ce.posttask.Project;
import org.sonar.api.config.Configuration;

import java.util.List;
import java.util.Objects;

import static java.lang.Boolean.FALSE;
import static org.sonar.api.internal.apachecommons.lang.StringUtils.isNotBlank;


public class SonarSlackPostProjectAnalysisTask implements PostProjectAnalysisTask {

    private static final Logger log = LoggerFactory.getLogger(SonarSlackPostProjectAnalysisTask.class);
    private static final String LF = "\n";

    private final Configuration configuration;
    private final Slack slack;


    public SonarSlackPostProjectAnalysisTask(Configuration configuration) {
        this.configuration = configuration;
        this.slack = Slack.getInstance();
    }


    @Override
    public void finished(Context context) {
        var allEnabled = configuration.getBoolean("green.slack.notify.enabled").orElse(FALSE);
        if (FALSE.equals(allEnabled)) return;

        var analysis = context.getProjectAnalysis();
        var project = analysis.getProject();
        var projectName = project.getName();
        log.info("Post Project Analysis: {}", projectName);

        List.of(configuration.get("green.slack.notify.webhook").orElse("").split(","))
            .forEach(number -> {
                var hookedProject = configuration.get("green.slack.notify.webhook." + number + ".project").orElse("");
                var url = configuration.get("green.slack.notify.webhook." + number + ".url").orElse("");

                if (Objects.equals(hookedProject, projectName) && isNotBlank(hookedProject) && isNotBlank(url)) {
                    sendToSlack(url, project, analysis);
                }
            });
    }

    private void sendToSlack(String url, Project project, ProjectAnalysis analysis) {
        log.info("Slack Notify project: {}", project);

        analysis.getQualityGate().getStatus();

//        sonarqubeLink(project, payload);
//
//
//        try {
//            slack.send(
//                configuration.get(WEBHOOK.value()).orElseThrow(NullPointerException::new),
//                Payload.builder().text(payload.toString()).build()
//            );
//        } catch (IOException e) {
//            log.error("Occurred network error on sending to slack.", e);
//        }
    }

    private void sonarqubeLink(Project project, StringBuilder payload) {
        // %s/project/issues?id=%s&myIssues=true

        var serverBaseUrl = configuration.get("sonar.core.serverBaseURL");

        payload
            .append(LF)
            .append("<")
            .append(String.format("%s/project/issues?id=%s&myIssues=true", serverBaseUrl, project.getKey()))
            .append("|")
            .append("dashboard")
            .append(">")
            .append(LF)
        ;
    }
}
