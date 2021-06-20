package com.sandeera.resourceserver.controller;


import com.sandeera.resourceserver.Service.CustomerDetailsService;
import com.sandeera.resourceserver.Service.TokenValidationService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;


@RestController
public class CustomerDetailsController {
    private static Logger logger = LogManager.getLogger(CustomerDetailsController.class);
    @Autowired
    private CustomerDetailsService customerDetailsService;
    @Autowired
    private TokenValidationService tokenValidationService;

    @GetMapping("/customerDetails")
    public ResponseEntity getCustomerDetails(@RequestHeader HttpHeaders headers){
        logger.info("processing getCustomerDetails");
       boolean validationResult =  tokenValidationService.validateJwtToken(headers);

       if(validationResult) {
           logger.warn("authorization successes !!!");
           return ResponseEntity.ok(customerDetailsService.getAllCustomerDetails());
       } else {
           logger.warn("authorization failed !!!");
           HashMap<String, String> map = new HashMap<>();
           map.put("status", HttpStatus.UNAUTHORIZED.toString());
           map.put("error", "accessDenied");
           return ResponseEntity.ok(map);
       }
    }
}
