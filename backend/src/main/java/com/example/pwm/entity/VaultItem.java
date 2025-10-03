package com.example.pwm.entity;
import jakarta.persistence.*; import java.time.*; import java.util.*;
@Entity @Table(name="vault_items", indexes=@Index(columnList="user_id,createdAt"))
public class VaultItem {
  @Id @GeneratedValue private UUID id;
  @ManyToOne(optional=false, fetch=FetchType.LAZY) private UserAccount user;
  private String title; private String username; private String url;
  @Column(length=4096) private String passwordEnc;
  @Column(length=4096) private String notesEnc;
  private Instant createdAt = Instant.now();
  public UUID getId(){return id;} public void setId(UUID id){this.id=id;}
  public UserAccount getUser(){return user;} public void setUser(UserAccount u){this.user=u;}
  public String getTitle(){return title;} public void setTitle(String s){this.title=s;}
  public String getUsername(){return username;} public void setUsername(String s){this.username=s;}
  public String getUrl(){return url;} public void setUrl(String s){this.url=s;}
  public String getPasswordEnc(){return passwordEnc;} public void setPasswordEnc(String s){this.passwordEnc=s;}
  public String getNotesEnc(){return notesEnc;} public void setNotesEnc(String s){this.notesEnc=s;}
  public Instant getCreatedAt(){return createdAt;} public void setCreatedAt(Instant t){this.createdAt=t;}
}
