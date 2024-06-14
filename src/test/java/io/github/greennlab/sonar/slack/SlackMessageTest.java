package io.github.greennlab.sonar.slack;

import com.slack.api.Slack;
import com.slack.api.model.Action;
import com.slack.api.model.Attachment;
import com.slack.api.model.Confirmation;
import com.slack.api.model.Field;
import com.slack.api.model.block.HeaderBlock;
import com.slack.api.model.block.RichTextBlock;
import com.slack.api.model.block.RichTextBlock.RichTextBlockBuilder;
import com.slack.api.model.block.SectionBlock;
import com.slack.api.model.block.composition.MarkdownTextObject;
import com.slack.api.model.block.composition.PlainTextObject;
import com.slack.api.webhook.Payload;
import java.io.IOException;
import java.util.List;
import javax.swing.text.html.HTMLDocument.BlockElement;

class SlackMessageTest {

  public static void main(String[] args) throws IOException {
    Slack slack = Slack.getInstance();
    var response = slack.send(
        "https://hooks.slack.com/services/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",
        Payload.builder()
            .attachments(
                List.of(
                    Attachment.builder()
                        .color("#236a97")
                        .title("Sonarqube 정적코드분석이 실행 되었습니다.")
                        .text("Hi, there!")
                        .actions(
                            List.of(
                                Action.builder()
                                    .text("more ...")
                                    .url("https://github.com/greenn-lab")
                                    .confirm(Confirmation.builder()
                                        .title("Sonarqube")
                                        .text("내가 작성한 코드에 이슈가 없는지 확인하려면 리포트를 확인하세요.")
                                        .dismissText("아니요")
                                        .okText("좋아요")
                                        .build())
                                    .build()
                            )
                        )
                        .fields(
                            List.of(
                                Field.builder()
                                    .title("Field title")
                                    .value("field value")
                                    .build(),
                                Field.builder()
                                    .title("Field title2")
                                    .value("field value2")
                                    .build()
                            )
                        )
                        .build()
                )
            )
            .blocks(List.of(
                HeaderBlock.builder().text(PlainTextObject.builder().text("Header").build()).build(),
                SectionBlock.builder()
                    .fields(List.of(
                        MarkdownTextObject.builder()
                            .text("*bold*\n\n_underline_ <font color=red>hello</font> <https://gitlab.com|gitlab>")
                            .verbatim(false)
                            .build()
                    ))
                    .build()
            ))
            .text("hi, hello!")
            .build());

    System.out.println("response.getCode(): " + response.getCode());
    System.out.println("response.getMessage(): " + response.getMessage());
    System.out.println("response.getBody(): " + response.getBody());
  }

}
