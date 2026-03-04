package com.congdongviet.auth.service;

import com.congdongviet.auth.entity.User;
import com.congdongviet.auth.repository.UserRepository;
import com.congdongviet.security.UserPrincipal;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Resolves a user by identifier using a priority chain:
     *  1. email   — contains "@"
     *  2. phone   — starts with "+" or is all digits
     *  3. username — fallback (used by SUPER_ADMIN and admin accounts)
     */
    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        User user;
        if (identifier.contains("@")) {
            user = userRepository.findByEmail(identifier)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found: " + identifier));
        } else if (identifier.startsWith("+") || identifier.matches("\\d+")) {
            user = userRepository.findByPhone(identifier)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found: " + identifier));
        } else {
            user = userRepository.findByUsername(identifier)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found: " + identifier));
        }
        return toUserPrincipal(user);
    }

    public UserDetails loadUserById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + id));
        return toUserPrincipal(user);
    }

    private UserPrincipal toUserPrincipal(User user) {
        List<SimpleGrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
        );
        return new UserPrincipal(
                user.getId(),
                user.getEmail(),
                user.getPassword(),
                authorities,
                user.isVerified()
        );
    }
}
