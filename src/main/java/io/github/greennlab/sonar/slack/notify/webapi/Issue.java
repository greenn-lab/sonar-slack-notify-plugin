package io.github.greennlab.sonar.slack.notify.webapi;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Issue {

  private String author;

  @Setter
  private String assignee;

  @Setter
  private String component;

  private String message;
  private String rule;
  private String type;
  private String status;
  private List<String> tags;

}
