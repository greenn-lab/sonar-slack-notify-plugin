package io.github.greennlab.sonar.slack.notify.webapi;

import com.google.gson.annotations.Expose;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Issue {

    private String author;

    @Setter
    private String assignee;

    @Setter
    private String component;

    private int line;
    private String message;
    private String rule;
    private List<String> tags;
    private String creationDate;

}
