package com.example.pwm.config;
import com.example.pwm.service.JwtService; import org.springframework.context.annotation.*; import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity; import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter; import org.springframework.security.authentication.*;
import org.springframework.security.core.*; import org.springframework.security.core.authority.SimpleGrantedAuthority; import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.*; import jakarta.servlet.http.*; import java.io.IOException; import java.util.List; import java.util.UUID;

@Configuration public class SecurityConfig {
  private final JwtService jwt;
  public SecurityConfig(JwtService jwt){ this.jwt=jwt; }

  @Bean public SecurityFilterChain chain(HttpSecurity http) throws Exception {
    http.csrf(csrf->csrf.disable())
      .authorizeHttpRequests(auth->auth
        .requestMatchers("/api/auth/**","/graphiql","/graphql","/actuator/health","/api/status").permitAll()
        .anyRequest().authenticated()
      )
      .addFilterBefore(new JwtFilter(jwt), UsernamePasswordAuthenticationFilter.class)
      .httpBasic(Customizer.withDefaults());
    return http.build();
  }

  static class JwtFilter extends OncePerRequestFilter {
    private final JwtService jwt; JwtFilter(JwtService j){this.jwt=j;}
    @Override protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
      throws ServletException, IOException {
      String h = req.getHeader("Authorization");
      if(h!=null && h.startsWith("Bearer ")){
        try{
          UUID id = jwt.verify(h.substring(7));
          Authentication auth = new UsernamePasswordAuthenticationToken(id.toString(), null, List.of(new SimpleGrantedAuthority("USER")));
          SecurityContextHolder.getContext().setAuthentication(auth);
        }catch(Exception ignored){}
      }
      chain.doFilter(req,res);
    }
  }
}
