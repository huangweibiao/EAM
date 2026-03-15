package com.eam.controller;

import com.eam.common.Result;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Hello Controller
 */
@RestController
@RequestMapping("/api")
public class HelloController {

    @GetMapping("/hello")
    public Result<String> hello() {
        return Result.success("Welcome to EAM Enterprise Asset Management System!");
    }

    @GetMapping("/whoami")
    public Result<String> whoami(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            return Result.success("Hello, " + authentication.getName() + "!");
        }
        return Result.success("Hello, anonymous user!");
    }
}
