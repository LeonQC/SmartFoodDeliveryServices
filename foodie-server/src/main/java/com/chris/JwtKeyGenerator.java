package com.chris;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;

public class JwtKeyGenerator {
    public static void main(String[] args) {
        // 生成一个随机的 HS256 对称密钥
        SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS512);
        // 将原始 key.getEncoded() 结果编码成 Base64 字符串
        String base64Key = Encoders.BASE64.encode(key.getEncoded());
        System.out.println("===== COPY THIS BASE64 SECRET KEY =====");
        System.out.println(base64Key);
        System.out.println("=======================================");
    }
}
