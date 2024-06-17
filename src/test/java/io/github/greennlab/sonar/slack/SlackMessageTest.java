package io.github.greennlab.sonar.slack;

import io.github.greennlab.sonar.slack.notify.SlackMessageSender;
import io.github.greennlab.sonar.slack.notify.webapi.WebApiProcessor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
class SlackMessageTest {

  @Test
  void send() {
    var processor = new WebApiProcessor("https://static-analysis.langsa.ai");

    var projectKey = "langsa_outsourcing_ccaas-demo_AZAGDWuE16vWvKVHK6SE";
    var url = System.getenv("SLACK_WEBHOOK");
    var response = new SlackMessageSender().send(url, processor.findAllIssues(projectKey));

    log.info("response.getCode(): {}", response.getCode());
    log.info("response.getMessage(): {}", response.getMessage());
    log.info("response.getBody(): {}", response.getBody());
  }

}
