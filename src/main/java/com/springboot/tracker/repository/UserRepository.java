package com.springboot.tracker.repository;

import com.springboot.tracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUsernameOrEmail(String username, String email);
    Optional<User> findByUsername(String username);
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);

    User findById(long id);

    boolean existsById(Long id);


    @Query("SELECT u FROM User u WHERE u.username=:username")
    User getUserFromUsername(String username);

    @Query("SELECT  u.barcode from User u where u.id=:id")
    byte[] getBarcodeById(long id);


}
