package io.github.greennlab.sonar.slack.webapi;

import io.github.greennlab.sonar.slack.notify.webapi.WebApiProcessor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
class WebApiProcessorTest {

  private final String server = "https://static-analysis.langsa.ai";


  @Test
  void shouldGetAllUsers() {
    var processor = new WebApiProcessor(server);

    assert null != processor.findAllUsers();
  }

  @Test
  void shouldGetAllIssues() {
    var processor = new WebApiProcessor(server);
    var issues = processor.findAllIssues("langsa_outsourcing_dx-quick_AZAAeInyAfbTcE50ppRJ");

    assert !issues.isEmpty();
  }

}
