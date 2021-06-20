package com.sandeera.authserver.controller;

import com.sandeera.authserver.Service.JWKDetailsService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class JwkSetController {
    private static Logger logger = LogManager.getLogger(JwkSetController.class);

    @Autowired
    private JWKDetailsService jWKDetailsService;

    @GetMapping("/.well-known/jwks")
    public ResponseEntity<Map<String,?>> getAvailableJwk() throws Exception {
        logger.info("Retrieving available JWK set ...");
        Map<String, Object> response = new HashMap<>();
        response.put("keys", jWKDetailsService.findAll());
        logger.info("public Keys successfully retrieved" );
        return ResponseEntity.ok(response);
    }
}
