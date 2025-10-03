package com.example.pwm.controller;
import com.example.pwm.entity.*; import com.example.pwm.repo.*; import com.example.pwm.service.CryptoService;
import org.springframework.security.core.Authentication; import org.springframework.web.bind.annotation.*; import jakarta.validation.Valid;
import java.util.*; import org.springframework.amqp.rabbit.core.RabbitTemplate;

@RestController @RequestMapping("/api/vault")
public class VaultController {
  record CreateBody(String title,String username,String password,String url,String notes){}
  record View(UUID id,String title,String username,String url,String password,String notes){}
  private final VaultItemRepository items; private final UserAccountRepository users; private final CryptoService crypto; private final RabbitTemplate mq;
  public VaultController(VaultItemRepository i,UserAccountRepository u,CryptoService c,RabbitTemplate mq){this.items=i;this.users=u;this.crypto=c;this.mq=mq;}

  private UserAccount current(Authentication auth){
    UUID id = UUID.fromString(auth.getName());
    return users.findById(id).orElseThrow();
  }

  @GetMapping public List<View> list(Authentication a){
    UserAccount u = current(a);
    return items.findByUserOrderByCreatedAtDesc(u).stream().map(v->new View(v.getId(),v.getTitle(),v.getUsername(),v.getUrl(),
      crypto.decrypt(v.getPasswordEnc()), crypto.decrypt(v.getNotesEnc()))).toList();
  }

  @PostMapping public View create(@Valid @RequestBody CreateBody b, Authentication a){
    UserAccount u = current(a);
    VaultItem v=new VaultItem(); v.setUser(u); v.setTitle(b.title()); v.setUsername(b.username()); v.setUrl(b.url());
    v.setPasswordEnc(crypto.encrypt(b.password())); v.setNotesEnc(crypto.encrypt(b.notes())); v=items.save(v);
    try{ mq.convertAndSend("pwm.checks", "{"email":""+u.getEmail()+"","url":""+(b.url()==null?"":b.url())+""}"); }catch(Exception ignored){}
    return new View(v.getId(), v.getTitle(), v.getUsername(), v.getUrl(), b.password(), b.notes());
  }

  @DeleteMapping("/{id}") public void delete(@PathVariable UUID id, Authentication a){
    UserAccount u = current(a); var v = items.findById(id).orElseThrow();
    if(!v.getUser().getId().equals(u.getId())) throw new RuntimeException("not found");
    items.delete(v);
  }
}
