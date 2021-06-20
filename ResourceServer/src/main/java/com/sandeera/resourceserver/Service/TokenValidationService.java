package com.sandeera.resourceserver.Service;

import com.auth0.jwk.*;
import com.auth0.jwt.exceptions.JWTDecodeException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.security.interfaces.RSAPublicKey;
import java.util.*;
import java.net.MalformedURLException;

import org.apache.commons.validator.routines.UrlValidator;

import java.net.URL;

@Service
public class TokenValidationService {

    private static Logger logger = LogManager.getLogger(TokenValidationService.class);

    private final Map<String, Jwk> cacheJwkStore = new HashMap<>();

    @Value("${authServer.URL}")
    private String authServerUrl;

    public boolean validateJwtToken(HttpHeaders headers) {
        String authorizationHeader = headers.get("Authorization") == null ? null :
                headers.get("Authorization").get(0).isEmpty() ? null : headers.get("Authorization").get(0);

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return getJWTVerify(authorizationHeader.substring(7));
        }
        return false;
    }

    public boolean getJWTVerify(String token) {
        DecodedJWT jwt = null;
        //JWTVerifier verifier = null;
        Algorithm alg;
        String kid = null;
        try {
            jwt = JWT.decode(token);
            kid = jwt.getKeyId();
        } catch (JWTDecodeException e) {
            logger.error("error occurred when decoding token {} error {}", token, e);
            return false;
        }

        if (authServerUrl == null) {
            authServerUrl = "http://localhost:8081/.well-known/jwks";
        }

        Jwk jwk = retrieveJwk(kid);

        if (jwk == null) {
            logger.error("jwk is not found");
            return false;
        }
        try {
            //Create algortihm using the JWK public key
            alg = Algorithm.RSA256((RSAPublicKey) jwk.getPublicKey(), null);
            //Create verifier using algorithm
            // verifier = JWT.require(alg).withIssuer("Mubasher_OMS").build();
        } catch (Exception e) {
            logger.error("Unexpected error occurred", e);
            return false;
        }

        try {
//            DecodedJWT decodedJwt = verifier.verify(jwt);
            alg.verify(jwt);
            logger.info("successful JwtVerification");
//            logger.info("JWT token claims: {}", decodedJwt.getClaims());
            logger.info("check whether token is expired or not");
            if (jwt.getExpiresAt().before(getUtcCalendar().getTime())) {
                logger.error("token is expired");
                return false;
            }
            logger.info("token is not expired. so jwt authorization process is success");
        } catch (Exception e) {
            logger.error("Invalid JWT Signature", e);
            return false;
        }
        return true;
    }

    public Jwk retrieveJwk(String kid) {
        Jwk jwk = null;
        if (kid == null || kid.isEmpty()) {
            logger.warn("invalid 'kid' header: [{}], it is required to define the Platform public rey.", kid);
        } else {
            try {
                jwk = cacheJwkStore.get(kid);
                if (Objects.isNull(jwk) || isJwkExpired(jwk)) {

                    logger.info("JWK for id {} NOT found or expired in internal cache: {}, retrieving from keyset URL [{}]...", kid, jwk, authServerUrl);

                    JwkProvider jwkProvider = new UrlJwkProvider(new URL(authServerUrl));

                    jwk = jwkProvider.get(kid);

                    //cache the JWK
                    cacheJwkStore.put(kid, jwk);

                    logger.info("JWK for id {} successfully retrieved from keyset URL and stored in internal cache...", kid);
                } else {
                    logger.info("JWK for id {} retrieved from internal cache store...", kid);
                }
            } catch (MalformedURLException e) {
                logger.error("Invalid URL: {}, unable to obtain Public key to perform token verification",
                        authServerUrl, e);
            } catch (JwkException e) {
                logger.error("Unable to define JWK for kid: {}, using keyset url {}", kid, authServerUrl, e);
            }
        }

        return jwk;
    }

    private boolean isJwkExpired(Jwk jwk) {
        Optional<Calendar> optExpDate = getExpirationDate(jwk);

        return optExpDate.isPresent()
                && optExpDate.get().before(getUtcCalendar());
    }

    private Optional<Calendar> getExpirationDate(Jwk jwk) {
        Object expObj = jwk.getAdditionalAttributes().get("exp");

        Calendar expCal = null;
        if (Objects.nonNull(expObj)) {
            expCal = getUtcCalendar();
            //"exp expressed in seconds, need to convert to milliseconds
            expCal.setTimeInMillis(Long.valueOf(expObj.toString()) * 1000);
        }

        return Optional.ofNullable(expCal);
    }

    private Calendar getUtcCalendar() {
        return Calendar.getInstance(TimeZone.getTimeZone("UTC"));
    }

}
