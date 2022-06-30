package com.example.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class MyController {
	
	private static final String RESOURCE_URL = "http://localhost:8082/t/fn-demo/hello-world";
    private RestTemplate restTemplate;

    @Autowired
    public MyController(RestTemplateBuilder builder) {
        this.restTemplate = builder.build();
    }

    @RequestMapping("/callfunction")
    public String callfunction() {

        ResponseEntity<String> response = restTemplate.getForEntity(RESOURCE_URL, String.class);

        return response.getBody();
    }

    @RequestMapping("/hello")
    public String hello() {
        return "Hello!";
    }
}
