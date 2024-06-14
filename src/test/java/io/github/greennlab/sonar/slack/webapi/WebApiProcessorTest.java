package io.github.greennlab.sonar.slack.webapi;

import io.github.greennlab.sonar.slack.notify.webapi.WebApiProcessor;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

class WebApiProcessorTest {

    private final String server = "https://static-analysis.langsa.ai";
    private final String credential = "sqa_5c221028ab206d3919e389463a946d690530ca5a";


    @Test
    void shouldGetAllUsers() {
        var processor = new WebApiProcessor(server, credential);

        assert null != processor.findAllUsers();
    }

    @Test
    void shouldGetAllIssues() {
        var processor = new WebApiProcessor(server, credential);

        var issues = processor.findAllIssues("langsa_outsourcing_dx-quick_AZAAeInyAfbTcE50ppRJ");
        assert null != issues;
    }

}
