package com.cema.administration.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {

    private final Logger LOG = LoggerFactory.getLogger(Controller.class);

    @RequestMapping(value = "/administration/process", method = RequestMethod.GET)
    public ResponseEntity<String> processTransaction(@RequestHeader HttpHeaders httpHeaders) {

        LOG.info("Request");

        return new ResponseEntity<>("Todo ok", HttpStatus.ACCEPTED);
    }

}
