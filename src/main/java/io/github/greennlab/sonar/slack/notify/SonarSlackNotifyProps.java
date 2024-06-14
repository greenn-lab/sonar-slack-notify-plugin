package io.github.greennlab.sonar.slack.notify;

public enum SonarSlackNotifyProps {
    WEBHOOK("green.slack.notify.webhook"),

    ENABLED("green.slack.notify.enabled");


    private final String value;


    SonarSlackNotifyProps(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
