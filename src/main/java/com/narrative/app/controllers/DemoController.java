package com.narrative.app.controllers;

import com.narrative.app.beans.Request;
import com.narrative.app.beans.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController("/")
public class DemoController {


    @PostMapping("/login")
    public ResponseEntity<Response> handleMyRequest(@RequestBody Request request) {

        Response response = null;

        if(request.getUserName().equals("mukesh") && request.getPassword().equals("qwerty")){
            response = new Response("Success", "hello "+request.getUserName());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            response = new Response("Failed", "Invalid username or password");
        }

        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }
}