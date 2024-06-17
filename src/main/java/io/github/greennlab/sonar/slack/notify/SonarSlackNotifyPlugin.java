package io.github.greennlab.sonar.slack.notify;

import static io.github.greennlab.sonar.slack.notify.SonarSlackNotifyProps.ENABLED;
import static io.github.greennlab.sonar.slack.notify.SonarSlackNotifyProps.WEBHOOK;

import io.github.greennlab.sonar.slack.notify.task.SonarSlackPostProjectAnalysisTask;
import java.util.List;
import org.sonar.api.Plugin;
import org.sonar.api.PropertyType;
import org.sonar.api.config.PropertyDefinition;
import org.sonar.api.config.PropertyFieldDefinition;


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
            PropertyDefinition.builder(ENABLED.value())
                .name("User Token")
                .description("Profile Button > MyAccount > Security > Generate Tokens")
                .type(PropertyType.PASSWORD)
                .category(CATEGORY)
                .subCategory(SUBCATEGORY)
                .index(1)
                .build(),
            PropertyDefinition.builder(WEBHOOK.value())
                .name("Slack Incoming Webhooks")
                .description("https://hooks.slack.com/services/...")
                .type(PropertyType.PROPERTY_SET)
                .category(CATEGORY)
                .subCategory(SUBCATEGORY)
                .index(2)
                .fields(
                    PropertyFieldDefinition.build("enable")
                        .name("Enabled")
                        .type(PropertyType.BOOLEAN)
                        .build(),
                    PropertyFieldDefinition.build("project")
                        .name("Project Name")
                        .description("Must match project name perfectly")
                        .type(PropertyType.STRING)
                        .build(),
                    PropertyFieldDefinition.build("url")
                        .name("Webhook URL")
                        .description("https://hooks.slack.com/service/...")
                        .type(PropertyType.STRING)
                        .build()
                )
                .build(),
            SonarSlackPostProjectAnalysisTask.class
        )
    );
  }
}
