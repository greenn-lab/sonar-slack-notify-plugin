package io.github.greennlab.sonar.slack.notify.task;

import static io.github.greennlab.sonar.slack.notify.SonarSlackNotifyProps.WEBHOOK;
import static java.lang.Boolean.FALSE;
import static org.sonar.api.internal.apachecommons.lang.StringUtils.isNotBlank;

import io.github.greennlab.sonar.slack.notify.SlackMessageSender;
import io.github.greennlab.sonar.slack.notify.webapi.WebApiProcessor;
import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.ce.posttask.PostProjectAnalysisTask;
import org.sonar.api.config.Configuration;


public class SonarSlackPostProjectAnalysisTask implements PostProjectAnalysisTask {

  private static final Logger log = LoggerFactory.getLogger(
      SonarSlackPostProjectAnalysisTask.class);

  private final Configuration config;
  private final SlackMessageSender slack;


  public SonarSlackPostProjectAnalysisTask(Configuration config) {
    this.config = config;
    this.slack = new SlackMessageSender();
  }


  @Override
  public void finished(Context context) {
    var allEnabled = config.getBoolean("green.slack.notify.enabled").orElse(FALSE);
    if (FALSE.equals(allEnabled)) {
      return;
    }

    var server = config.get("sonar.core.serverBaseURL").orElseThrow();
    var webApiProcessor = new WebApiProcessor(server);

    var analysis = context.getProjectAnalysis();
    var project = analysis.getProject();
    var projectKey = project.getKey();
    var projectName = project.getName();

    List.of(config.get("green.slack.notify.webhook").orElse("").split(","))
        .forEach(number -> {
          if (config.getBoolean(WEBHOOK.value(number, "enabled")).orElse(FALSE)) {
            var hookedProject = config.get(WEBHOOK.value(number, "project"))
                .orElse("");

            if (Objects.equals(hookedProject, projectName)) {
              log.info("Post Project Analysis: {}", projectName);

              var url = config.get(WEBHOOK.value(number, "url")).orElse("");
              if (isNotBlank(url)) {
                var issues = webApiProcessor.findAllIssues(projectKey);
                slack.send(url, issues);
              }
            }
          }
        });
  }

}
