package com.vanna.jwtservice7.controllers;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.vanna.jwtservice7.dto.GenerateRequest;
import com.vanna.jwtservice7.dto.ValidateRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/api/token")
class JwtGostController {

    @PostMapping("/generate")
    public Map<String, String> generateToken(@RequestBody GenerateRequest request) {
        try {
            // Декодируем P12 из Base64
            System.out.println("p12 from request: " + request.getP12());
            byte[] p12Bytes = Base64.getDecoder().decode(request.getP12());

            // Загружаем P12 в KeyStore
            KeyStore keyStore = KeyStore.getInstance("PKCS12", "KALKAN");// не понял
            keyStore.load(new ByteArrayInputStream(p12Bytes), request.getPassword().toCharArray());

            // Предполагаем, что в хранилище один ключ (берём первый)
            String alias = keyStore.aliases().nextElement();
            PrivateKey privateKey = (PrivateKey) keyStore.getKey(alias, request.getPassword().toCharArray());
            X509Certificate certificate = (X509Certificate) keyStore.getCertificate(alias);

            // добавили вручную header
            Map<String, Object> headerClaims = new HashMap<>();
            headerClaims.put("alg", "GG2015");
            headerClaims.put("typ", "JWT");

            // Генерируем токен с использованием GG2015
            Algorithm algorithm = Algorithm.GG2015(
                    (ECPublicKey) certificate.getPublicKey(),
                    (ECPrivateKey) privateKey
            );
            var jwtBuilder = JWT.create().withHeader(headerClaims);
            Map<String, Object> payload = request.getPayload();
            if (payload != null) {
                for (Map.Entry<String, Object> entry : payload.entrySet()) {
                    String key = entry.getKey();
                    Object value = entry.getValue();
                    // Добавляем в зависимости от типа значения
                    if (value instanceof String) {
                        jwtBuilder.withClaim(key, (String) value);
                    } else if (value instanceof Integer) {
                        jwtBuilder.withClaim(key, (Integer) value);
                    } else if (value instanceof Long) {
                        jwtBuilder.withClaim(key, (Long) value);
                    } else if (value instanceof Boolean) {
                        jwtBuilder.withClaim(key, (Boolean) value);
                    } else {
                        jwtBuilder.withClaim(key, value.toString()); // по умолчанию строка
                    }
                }
            }

            String token = jwtBuilder.sign(algorithm);


            // Возвращаем токен и сертификат в base64
            Map<String, String> response = new HashMap<>();
            response.put("token", token);
            response.put("cert", Base64.getEncoder().encodeToString(certificate.getEncoded()));
            return response;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to generate token: " + e.getMessage());
        }
    }

    // Валидация токена по сертификату (ГОСТ)
    @PostMapping("/validate")
    public boolean validateToken(@Valid @RequestBody ValidateRequest request) {
        try {
            // Декодируем сертификат из base64
            byte[] certBytes = Base64.getDecoder().decode(request.getCert());

            // Парсим X.509 сертификат
            CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
            X509Certificate certificate = (X509Certificate) certFactory.generateCertificate(
                    new ByteArrayInputStream(certBytes)
            );

            // Получаем public key из сертификата
            ECPublicKey publicKey = (ECPublicKey) certificate.getPublicKey();

            // Создаём алгоритм для валидации
            Algorithm algorithm = Algorithm.GG2015(publicKey, null);

            // Верифицируем токен (бросает исключение, если токен недействителен)
            System.out.println("TOKEN: " + request.getToken());
            System.out.println("CERT: " + request.getCert());
            System.out.println("PUBLIC KEY: " + publicKey);
            System.out.println("ALGORITHM: " + algorithm.getName());

            JWTVerifier verifier = JWT.require(algorithm).build();
            verifier.verify(request.getToken());


            return true;

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Token validation failed: " + e.getMessage());
            return false;
        }
    }
}