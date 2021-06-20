package com.sandeera.authserver.controller;

import com.sandeera.authserver.bean.AuthRequest;
import com.sandeera.authserver.jwtConfig.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

@RestController
public class LoginController {

    private static Logger logger = LogManager.getLogger(LoginController.class);

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    @GetMapping( value = "/")
    public String welcomePage(){
        logger.info("welcome Page");
        return "Welcome to Auth Server...!";
    }

    @PostMapping("/authenticate")
    public Map<String, String> generateToken(@RequestBody AuthRequest authRequest) throws Exception {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUserName(), authRequest.getPassword())
            );

        } catch (Exception ex) {
            logger.error("authentication failed. invalid username/ {}",authRequest);
            throw new Exception("invalid username/password");
        }
        logger.info("successfully authenticated user {}",authRequest.getUserName());
        HashMap<String, String> responseMap = new HashMap<>();
        responseMap.put("userName", authRequest.getUserName());
        responseMap.put("accessToken", jwtUtil.generateToken(authRequest.getUserName()));
        logger.info("response {}",responseMap);
        return responseMap;
    }
}