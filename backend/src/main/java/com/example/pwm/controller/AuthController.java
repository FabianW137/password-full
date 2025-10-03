package com.example.pwm.controller;
import com.example.pwm.entity.UserAccount; import com.example.pwm.repo.UserAccountRepository;
import com.example.pwm.service.JwtService; import jakarta.validation.constraints.*; import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.*; import java.net.URLEncoder; import java.nio.charset.StandardCharsets; import java.util.*;
import org.apache.commons.codec.binary.Base32;

@RestController @RequestMapping("/api/auth")
public class AuthController {
  record RegisterBody(@Email String email, @Size(min=8) String password){}
  record RegisterResp(String otpauthUrl, String secretBase32, String message){}
  record LoginBody(@Email String email, String password){}
  record LoginResp(boolean requiresTotp, String tmpToken){}
  record VerifyBody(String tmpToken, String code){}
  record TokenResp(String token){}

  private final UserAccountRepository users; private final JwtService jwt;
  public AuthController(UserAccountRepository u, JwtService j){ this.users=u; this.jwt=j; }

  @PostMapping("/register")
  public RegisterResp register(@RequestBody RegisterBody b){
    users.findByEmail(b.email()).ifPresent(x->{ throw new RuntimeException("E-Mail existiert"); });
    Base32 base32 = new Base32();
    byte[] secret = new byte[20]; new java.security.SecureRandom().nextBytes(secret);
    String secretBase32 = base32.encodeToString(secret).replace("=","");
    UserAccount u = new UserAccount();
    u.setEmail(b.email()); u.setPasswordHash(BCrypt.hashpw(b.password(), BCrypt.gensalt())); u.setTotpSecret(secretBase32);
    users.save(u);
    String issuer = URLEncoder.encode("PWM", StandardCharsets.UTF_8);
    String account = URLEncoder.encode(b.email(), StandardCharsets.UTF_8);
    String otpauth = "otpauth://totp/"+issuer+":"+account+"?secret="+secretBase32+"&issuer="+issuer+"&digits=6&period=30";
    return new RegisterResp(otpauth, secretBase32, "ok");
  }

  @PostMapping("/login")
  public LoginResp login(@RequestBody LoginBody b){
    UserAccount u = users.findByEmail(b.email()).orElseThrow(()->new RuntimeException("E-Mail unbekannt"));
    if(!BCrypt.checkpw(b.password(), u.getPasswordHash())) throw new RuntimeException("Passwort falsch");
    String tmp = jwt.create(u.getId()); // ephemeral; in real world: store with short TTL
    return new LoginResp(true, tmp);
  }

  @PostMapping("/totp-verify")
  public TokenResp verify(@RequestBody VerifyBody body){
    java.util.UUID uid = jwt.verify(body.tmpToken());
    // very light totp check for demo: len == 6; replace with actual verify if desired
    if(body.code()==null || !body.code().matches("\\d{6}")) throw new RuntimeException("TOTP invalid");
    return new TokenResp(jwt.create(uid));
  }
}
