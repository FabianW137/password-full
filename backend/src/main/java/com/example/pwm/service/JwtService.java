package com.example.pwm.service;
import io.jsonwebtoken.*; import io.jsonwebtoken.security.Keys; import java.security.Key; import java.util.*;
public class JwtService {
  private final Key key;
  public JwtService(String secret){
    if(secret==null || secret.length()<32) throw new IllegalStateException("JWT_SECRET fehlt");
    this.key = Keys.hmacShaKeyFor(secret.getBytes(java.nio.charset.StandardCharsets.UTF_8));
  }
  public String create(UUID userId){
    return Jwts.builder().setSubject(userId.toString()).setIssuedAt(new Date()).signWith(key, SignatureAlgorithm.HS256).compact();
  }
  public UUID verify(String token){
    Jws<Claims> j = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
    return UUID.fromString(j.getBody().getSubject());
  }
}
