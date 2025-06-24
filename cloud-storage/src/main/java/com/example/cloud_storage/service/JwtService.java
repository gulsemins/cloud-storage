package com.example.cloud_storage.service;

import com.example.cloud_storage.entity.UserEntity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    private final String secretKey;

    public JwtService(
            @Value("${JWT_SECRET}") String secretKey) {
        this.secretKey = secretKey;
    }

    public String generateToken(UserEntity user) {
        //Token içine eklenecek ek bilgiler burd  TUTULUR
        Map<String, Object> claims = new HashMap<>();

        return Jwts.builder()//jwt builder nesnesi olşturuyor
                .setClaims(claims)//token içerisine gömülecek ek bilgiler
                .setSubject(user.getUsername())//Bu token'ın kime ait olduğunu belirtir. Genellikle kullanıcı adı verilir.
                .setIssuer("yukki")//bu tokeni oluşturanlar kim
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 10)) // 10 dakika
                .signWith(generateKey(), SignatureAlgorithm.HS256)
                .compact();// token stringe çevrilip döndürülüyor
    }

    private SecretKey generateKey() {
        byte[] decode = Decoders.BASE64.decode(secretKey);

        return Keys.hmacShaKeyFor(decode);
    }

    public String extractUsername(String token) {
        return extractClaims(token, Claims::getSubject);
    }

    private <T> T extractClaims(String token, Function<Claims, T> claimResolver) {
        Claims claims = extractClaims(token);
        return claimResolver.apply(claims);
    }
    private Claims extractClaims(String token) {//Token içindeki username, role, issuedAt, exp gibi bilgileri bir Claims nesnesi olarak döner.
        return Jwts
                .parserBuilder()
                .setSigningKey(generateKey())//Token imzası doğru mu diye kontrol eder
                .build()// Parser objesini tamamla
                .parseClaimsJws(token) // Token'ı çöz ve doğrula
                .getBody(); // İçindeki claim’leri al
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String userName = extractUsername(token);
        return (userName.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaims(token, Claims::getExpiration);
    }

}
