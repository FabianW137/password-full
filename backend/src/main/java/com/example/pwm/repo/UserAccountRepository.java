package com.example.pwm.repo;
import com.example.pwm.entity.UserAccount; import java.util.*; import org.springframework.data.jpa.repository.JpaRepository;
public interface UserAccountRepository extends JpaRepository<UserAccount, UUID> { Optional<UserAccount> findByEmail(String email); }
