package com.example.pwm.repo;
import com.example.pwm.entity.*; import java.util.*; import org.springframework.data.jpa.repository.JpaRepository;
public interface VaultItemRepository extends JpaRepository<VaultItem, UUID> { List<VaultItem> findByUserOrderByCreatedAtDesc(UserAccount u); }
