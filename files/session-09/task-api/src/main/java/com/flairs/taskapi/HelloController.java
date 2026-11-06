package com.flairs.taskapi;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// The smallest possible endpoint. Step 1 of the live build.
//   GET /api/hello                -> "Hello, World!"
//   GET /api/hello?name=Sara      -> "Hello, Sara!"
@RestController
public class HelloController {

    @GetMapping("/api/hello")
    public String hello(@RequestParam(defaultValue = "World") String name) {
        return "Hello, " + name + "!";
    }
}
