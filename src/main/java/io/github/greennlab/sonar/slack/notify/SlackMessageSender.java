package io.github.greennlab.sonar.slack.notify;

import static java.lang.String.format;

import com.slack.api.Slack;
import com.slack.api.model.Action;
import com.slack.api.model.Attachment;
import com.slack.api.model.Confirmation;
import com.slack.api.model.Field;
import com.slack.api.model.block.HeaderBlock;
import com.slack.api.model.block.composition.PlainTextObject;
import com.slack.api.webhook.Payload;
import com.slack.api.webhook.WebhookResponse;
import io.github.greennlab.sonar.slack.notify.webapi.IssueSummary;
import java.io.IOException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SlackMessageSender {

  private static final Logger log = LoggerFactory.getLogger(SlackMessageSender.class);

  private final Slack client;


  public SlackMessageSender() {
    client = Slack.getInstance();
  }

  private static String headerByTotalIssue(IssueSummary summary) {
    var total = summary.getCount();

    if (total == 0) {
      return "정정코드분석 결과, 완벽하게 코드를 작성 했군요. 🎉";
    } else if (total < 20) {
      return "모두 " + total + "개의 정적코드분석 이슈가 있어요. 😁";
    }

    return "무려 " + total + "개 정적코드분석 이슈가 나왔네요. 🥶";
  }

  private static String colorBy(String type) {
    return switch (type) {
      case "BUG" -> "#faa";
      case "VULNERABILITY" -> "#f55";
      default -> "#5c5";
    };
  }

  public WebhookResponse send(String url, IssueSummary summary) {
    var payload = Payload.builder()
        .blocks(List.of(
                HeaderBlock.builder()
                    .text(PlainTextObject.builder().text(headerByTotalIssue(summary)).build()).build()
            )
        )
        .attachments(
            summary.getIssuesByType().keySet()
                .stream().map(type -> Attachment.builder()
                    .color(colorBy(type))
                    .actions(
                        List.of(
                            Action.builder()
                                .text("자세히...")
                                .url("https://static-analysis.langsa.ai/project/issues?types=" + type
                                    + "&id=" + summary.getProjectKey())
                                .confirm(Confirmation.builder()
                                    .title("Sonarqube")
                                    .text("더 자세한 " + type + " 이슈를 확인 하려면 소나큐브 시스템으로 이동 하세요.")
                                    .dismissText("아니요")
                                    .okText("좋아요")
                                    .build())
                                .build()
                        )
                    )
                    .title(type)
                    .fields(
                        summary.getIssuesByType().get(type).stream().map(issue -> Field.builder()
                            .title(
                                format("🫡%s 🚫%s 🔖%s",
                                    issue.getAuthor() +
                                        (issue.getAuthor().equals(issue.getAssignee())
                                            ? ""
                                            : "(" + issue.getAssignee() + ")"),
                                    issue.getRule(),
                                    String.join("", issue.getTags())
                                )
                            )
                            .value(issue.getMessage())
                            .build()).toList()
                    )
                    .build()
                ).toList()

        )
        .build();

    try {
      return client.send(
          url,
          payload
      );
    } catch (IOException e) {
      log.error("Occurred network error on sending to slack.", e);
    }

    throw new IllegalStateException();
  }

}
