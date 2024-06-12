package io.github.greennlab.sonar.slack.notify.task;

import com.slack.api.Slack;
import com.slack.api.webhook.Payload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.ce.posttask.PostProjectAnalysisTask;
import org.sonar.api.ce.posttask.Project;
import org.sonar.api.ce.posttask.ScannerContext;
import org.sonar.api.config.Configuration;

import java.io.IOException;

import static io.github.greennlab.sonar.slack.notify.SonarSlackNotifyProps.HOOK_URL;


public class SonarSlackPostProjectAnalysisTask implements PostProjectAnalysisTask {

    private static final Logger log = LoggerFactory.getLogger(SonarSlackPostProjectAnalysisTask.class);
    private static final String LF = "\n";

    private final Configuration configuration;
    private final Slack slack;


    public SonarSlackPostProjectAnalysisTask(Configuration configuration) {
        this(Slack.getInstance(), configuration);
    }

    public SonarSlackPostProjectAnalysisTask(Slack slack, Configuration configuration) {
        this.configuration = configuration;
        this.slack = slack;
    }


    @Override
    public void finished(Context context) {
        var payload = new StringBuilder("📣 Sonarqube" + LF + LF);

        var analysis = context.getProjectAnalysis();
        var qualityGate = analysis.getQualityGate();

        var project = analysis.getProject();
        payload.append(project.getName()).append(" (").append(project.getKey()).append(")").append(LF);

        if (qualityGate != null) {
            var status = qualityGate.getStatus();
            payload.append("STATUS: ").append(status).append(LF);
        }


        sonarqubeLink(project, payload);


        log.warn("{}", payload);

        try {
            slack.send(
                configuration.get(HOOK_URL.value()).orElseThrow(NullPointerException::new),
                Payload.builder().text(payload.toString()).build()
            );
        } catch (IOException e) {
            log.error("Occurred network error on sending to slack.", e);
        }
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
