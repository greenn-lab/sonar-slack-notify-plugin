package io.github.greennlab.sonar.slack;

import com.slack.api.Slack;
import com.slack.api.webhook.Payload;

import java.io.IOException;

class SlackMessageTest {

    public static void main(String[] args) throws IOException {
        Slack slack = Slack.getInstance();
        slack.send("https://hooks.slack.com/services/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",
            Payload.builder().text("hi~").build());
    }

}
