package com.pranjal.auth;

import com.pranjal.security.JwtService;
import com.pranjal.user.Role;
import com.pranjal.user.UserEntity;
import com.pranjal.user.UserRepository;
import com.pranjal.auth.dto.AuthResponse;
import com.pranjal.auth.dto.UserProfileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public AuthResponse registerOwner(String name, String email, String password) {

        if (userRepository.existsByRole(Role.OWNER)) {
            throw new IllegalStateException("Store already initialized");
        }

        UserEntity userEntity = UserEntity.builder()
                .name(name)
                .email(email)
                .password(passwordEncoder.encode(password))
                .role(Role.OWNER)
                .isActive(true)
                .build();

        userRepository.save(userEntity);

        return AuthResponse.builder()
                .token(jwtService.generateToken(userEntity))
                .name(userEntity.getName())
                .email(userEntity.getEmail())
                .role(userEntity.getRole().name())
                .build();
    }

    public AuthResponse login(String email, String password) {
        UserEntity userEntity = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Invalid credentials"));

        if (!passwordEncoder.matches(password, userEntity.getPassword())) {
            throw new UsernameNotFoundException("Invalid credentials");
        }

        return AuthResponse.builder()
                .token(jwtService.generateToken(userEntity))
                .name(userEntity.getName())
                .email(userEntity.getEmail())
                .role(userEntity.getRole().name())
                .build();
    }

    public UserProfileResponse getMe(String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return UserProfileResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .isActive(user.isActive())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
