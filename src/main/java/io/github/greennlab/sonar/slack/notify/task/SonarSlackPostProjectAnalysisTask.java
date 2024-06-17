package io.github.greennlab.sonar.slack.notify.task;

import static io.github.greennlab.sonar.slack.notify.SonarSlackNotifyProps.ENABLED;
import static io.github.greennlab.sonar.slack.notify.SonarSlackNotifyProps.WEBHOOK;
import static java.lang.Boolean.FALSE;

import io.github.greennlab.sonar.slack.notify.SlackMessageSender;
import io.github.greennlab.sonar.slack.notify.webapi.WebApiProcessor;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.ce.posttask.PostProjectAnalysisTask;
import org.sonar.api.config.Configuration;


public class SonarSlackPostProjectAnalysisTask implements PostProjectAnalysisTask {

  private static final Logger log = LoggerFactory.getLogger("sonar-slack-notify-plugin");

  private final Configuration config;
  private final SlackMessageSender slack;


  public SonarSlackPostProjectAnalysisTask(Configuration config) {
    this.config = config;
    this.slack = new SlackMessageSender();
  }


  @Override
  public void finished(Context context) {
    var allEnabled = config.getBoolean(ENABLED.value()).orElse(FALSE);
    if (FALSE.equals(allEnabled)) {
      log.info("Slack notification is disabled.");
      return;
    }

    var server = config.get("sonar.core.serverBaseURL").orElseThrow();
    var webApiProcessor = new WebApiProcessor(server);

    var analysis = context.getProjectAnalysis();
    var project = analysis.getProject();
    var projectKey = project.getKey();

    for (String number : config.getStringArray(WEBHOOK.value())) {
      if (config.getBoolean(WEBHOOK.value(number, "enabled")).orElse(FALSE)) {
        var hookedProject =
            config.get(WEBHOOK.value(number, "project")).orElse("");

        if (Objects.equals(hookedProject, projectKey)) {
          var url = config.get(WEBHOOK.value(number, "url")).orElse("");
          var issues = webApiProcessor.findAllIssues(projectKey);

          log.info("Post Project Analysis: {}({} issues) => {}",
              projectKey, issues.getCount(), url);

          var sent = slack.send(url, issues);
          if (200 != sent.getCode()) {
            throw new IllegalStateException(sent.getMessage());
          }
        }
      }
    }
  }

}
