package com.example.pwm.entity;
import jakarta.persistence.*; import java.util.*;
@Entity @Table(name="users", uniqueConstraints=@UniqueConstraint(columnNames="email"))
public class UserAccount {
  @Id @GeneratedValue private UUID id;
  @Column(nullable=false, unique=true) private String email;
  @Column(nullable=false) private String passwordHash;
  private String totpSecret; // base32
  public UUID getId(){return id;} public void setId(UUID id){this.id=id;}
  public String getEmail(){return email;} public void setEmail(String e){this.email=e;}
  public String getPasswordHash(){return passwordHash;} public void setPasswordHash(String h){this.passwordHash=h;}
  public String getTotpSecret(){return totpSecret;} public void setTotpSecret(String s){this.totpSecret=s;}
}
