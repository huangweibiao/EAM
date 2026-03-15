package com.eam.mapper;

import com.eam.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * User Repository
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Find user by username
     */
    Optional<User> findByUsername(String username);

    /**
     * Find user by OAuth2 provider and user ID
     */
    Optional<User> findByOAuth2ProviderAndOAuth2UserId(String provider, String userId);

    /**
     * Check if username exists
     */
    boolean existsByUsername(String username);
}
