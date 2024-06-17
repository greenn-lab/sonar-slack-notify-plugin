package io.github.greennlab.sonar.slack.notify.webapi;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

  private String login;
  private String name;
  private String email;

}
