package com.api.controller;

import java.security.Principal;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiController {

	@GetMapping(value="/")
	public Principal home(Principal principal) {
        return principal;
    }
}
