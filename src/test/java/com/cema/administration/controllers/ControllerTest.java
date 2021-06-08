package com.cema.administration.controllers;

import org.junit.Test;
import org.springframework.http.ResponseEntity;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ControllerTest  {

    @Test
    public void processTransactionShouldAlwaysReturnMessage(){
        Controller controller = new Controller();
        ResponseEntity<String> result = controller.processTransaction(null);
        String response = result.getBody();

        assertThat(response, is("Request Correcta."));
    }

}