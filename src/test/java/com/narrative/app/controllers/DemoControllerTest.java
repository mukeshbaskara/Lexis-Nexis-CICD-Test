package com.narrative.app.controllers;

//import org.junit.Test;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import com.narrative.app.beans.Request;
import com.narrative.app.beans.Response;
import com.narrative.app.controllers.DemoController;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DemoControllerTest {

    @Autowired
    private DemoController demoController;

    @Test
    public void handleMyRequest_withCorrectCredentials_returnsSuccessResponse() {
        Request request = new Request("mukesh", "qwerty");
        ResponseEntity<Response> response = demoController.handleMyRequest(request);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getStatus()).isEqualTo("Success");
        assertThat(response.getBody().getMessage()).isEqualTo("hello mukesh");
    }

    @Test
    public void handleMyRequest_withIncorrectCredentials_returnsForbiddenResponse() {
        Request request = new Request("john", "doe");
        ResponseEntity<Response> response = demoController.handleMyRequest(request);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody().getStatus()).isEqualTo("Failed");
        assertThat(response.getBody().getMessage()).isEqualTo("Invalid username or password");
    }
}