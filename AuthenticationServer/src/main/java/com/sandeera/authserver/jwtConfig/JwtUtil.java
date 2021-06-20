package com.sandeera.authserver.jwtConfig;
import com.sandeera.authserver.Service.JWKDetailsService;
import com.sandeera.authserver.bean.JwkExposedModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.thymeleaf.util.DateUtils;
import org.springframework.stereotype.Service;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.util.*;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.JWT;
import javax.annotation.PostConstruct;

@Service
public class JwtUtil {

    private static Logger logger = LogManager.getLogger(JwtUtil.class);

    private final Set<KeyPairContainer> cachedKeyPairs = new HashSet<>();
    @Value("${JWK.Keypair.Expiration.inSeconds}")
    private Long keyPairExpiration;
    @Value("${JWT.Expiration.inSeconds}")
    private int jwtExpiration;
    @Autowired
    private JWKDetailsService jWKDetailsService;

    @PostConstruct
    public Optional<KeyPair> getKeyPair() throws Exception {
        cachedKeyPairs.removeIf(kpc -> kpc.isExpired());
        populateCache();
        Optional<KeyPairContainer> optKpc =
                cachedKeyPairs.stream().filter(kpc -> !kpc.isExpired()).findAny();
        return Optional.ofNullable(optKpc.isPresent() ? optKpc.get().getKeyPair() : null);
    }


    private void populateCache() throws Exception {
        while (cachedKeyPairs.size() < 2) {
            logger.info("generating new key pairs");
            KeyPair kp = generateNewKeyPair();
            String keyId = UUID.randomUUID().toString();
            KeyPairContainer kpc = new KeyPairContainer(keyId, kp, keyPairExpiration);

            logger.info("generating JWK");
            RSAKey rsaKey = new RSAKey.Builder((RSAPublicKey) kp.getPublic())
                    .keyUse(KeyUse.SIGNATURE)
                    .keyID(keyId)
                    .build();

            JwkExposedModel jem = new JwkExposedModel();
            jem.setKId(keyId);
            jem.setKty(rsaKey.getKeyType().getValue());
            jem.setUse(rsaKey.getKeyUse().getValue());
            jem.setN(rsaKey.getModulus().toString());
            jem.setE(rsaKey.getPublicExponent().toString());
            jem.setExp(kpc.getExpiredAt());
            // 3. use repository to store the new JWK model
            jWKDetailsService.addNew(jem);
            // 4. store RSA Key Pair in local cache
            cachedKeyPairs.add(kpc);
        }
    }

    private KeyPair generateNewKeyPair() throws Exception {
        KeyPairGenerator kpg;
        try {
            kpg = KeyPairGenerator.getInstance("RSA");

            kpg.initialize(1024, SecureRandom.getInstanceStrong());

            return kpg.generateKeyPair();

        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    // used to generate the token
    public String generateToken(String username) throws Exception {
        return getSignedJwt("Mubasher_OMS", "DT", username);
    }

    public String getSignedJwt(String issuer, String audience, String subject) throws Exception {
        KeyPair kp = getKeyPair().orElseThrow(() -> new Exception("Unable to obtain a valid RSA KeyPair"));

        Algorithm algorithm =
                Algorithm.RSA256((RSAPublicKey) kp.getPublic(), (RSAPrivateKey) kp.getPrivate());

        String kid = cachedKeyPairs.stream().filter(ckp -> ckp.getKeyPair().equals(kp)).findAny().get().getKeyId();

        Calendar now = DateUtils.createNow(TimeZone.getTimeZone("UTC"));
        Calendar exp = DateUtils.createNow(TimeZone.getTimeZone("UTC"));
        exp.add(Calendar.SECOND, jwtExpiration);

        return JWT.create()
                .withKeyId(kid)
                .withIssuer(issuer)
                .withAudience(audience)
                .withJWTId(UUID.randomUUID().toString())
                .withIssuedAt(now.getTime())
                .withSubject(subject)
                .withNotBefore(now.getTime())
                .withExpiresAt(exp.getTime())
                .sign(algorithm);

    }
}