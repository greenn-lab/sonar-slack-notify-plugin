package io.github.greennlab.sonar.slack.notify;

import io.github.greennlab.sonar.slack.notify.task.SonarSlackPostProjectAnalysisTask;
import org.sonar.api.Plugin;
import org.sonar.api.PropertyType;
import org.sonar.api.config.PropertyDefinition;

import java.util.List;

import static io.github.greennlab.sonar.slack.notify.SonarSlackNotifyProps.ENABLED;
import static io.github.greennlab.sonar.slack.notify.SonarSlackNotifyProps.HOOK_URL;


public class SonarSlackNotifyPlugin implements Plugin {

    private static final String CATEGORY = "Slack";
    private static final String SUBCATEGORY = "Slack Notify";

    @Override
    public void define(Context context) {
        context.addExtensions(List.of(
                PropertyDefinition.builder(ENABLED.value())
                    .name("Plugin enabled")
                    .description("Enable Slack notifications")
                    .defaultValue("true")
                    .type(PropertyType.BOOLEAN)
                    .category(CATEGORY)
                    .subCategory(SUBCATEGORY)
                    .index(0)
                    .build(),
                PropertyDefinition.builder(HOOK_URL.value())
                    .name("Slack Incoming Webhook")
                    .description("https://hooks.slack.com/services/...")
                    .type(PropertyType.STRING)
                    .category(CATEGORY)
                    .subCategory(SUBCATEGORY)
                    .index(1)
                    .build(),
                SonarSlackPostProjectAnalysisTask.class
            )
        );
    }
}
