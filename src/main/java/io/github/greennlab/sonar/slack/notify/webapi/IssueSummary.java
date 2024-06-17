package io.github.greennlab.sonar.slack.notify.webapi;

import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class IssueSummary {

  private String projectKey;
  private int count;
  private Map<String, List<Issue>> issuesByType;

  public boolean isEmpty() {
    return count == 0;
  }

}
