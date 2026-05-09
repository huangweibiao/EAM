package com.eam.service;

import com.eam.entity.User;
import com.eam.mapper.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.core.oidc.OidcUser;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

/**
 * User Service
 */
@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword() != null ? user.getPassword() : "")
                .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")))
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(user.getStatus() == 0)
                .build();
    }

    /**
     * Create or update user from OAuth2 login
     */
    @Transactional
    public User createOrUpdateUserFromOidcUser(OidcUser oidcUser, String provider) {
        String oauth2UserId = oidcUser.getSubject();
        String username = oidcUser.getPreferredUsername() != null ?
                oidcUser.getPreferredUsername() : oauth2UserId;
        String email = oidcUser.getEmail();
        String nickname = oidcUser.getGivenName() != null ?
                oidcUser.getGivenName() : username;
        String avatar = oidcUser.getPicture();

        // Try to find existing user by OAuth2 user ID
        return userRepository.findByOAuth2ProviderAndOAuth2UserId(provider, oauth2UserId)
                .map(user -> {
                    // Update existing user
                    updateUserInfo(user, username, email, nickname, avatar);
                    return userRepository.save(user);
                })
                .orElseGet(() -> {
                    // Create new user
                    User newUser = new User();
                    newUser.setUsername(username);
                    newUser.setEmail(email);
                    newUser.setNickname(nickname);
                    newUser.setAvatar(avatar);
                    newUser.setOauth2Provider(provider);
                    newUser.setOauth2UserId(oauth2UserId);
                    newUser.setStatus(1);
                    return userRepository.save(newUser);
                });
    }

    private void updateUserInfo(User user, String username, String email, String nickname, String avatar) {
        if (email != null && !email.equals(user.getEmail())) {
            user.setEmail(email);
        }
        if (nickname != null && !nickname.equals(user.getNickname())) {
            user.setNickname(nickname);
        }
        if (avatar != null && !avatar.equals(user.getAvatar())) {
            user.setAvatar(avatar);
        }
    }

    /**
     * Get user by username
     */
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }
}
